package com.sxs.styd.stbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sxs.styd.stbook.adapter.FileListAdapter;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.data.DBManager;
import com.sxs.styd.stbook.util.MapUtil;
import com.sxs.styd.stbook.vo.BookVO;
/**.
 * 本地文件目录文件读写
 * @author user
 *
 */
public class FileHandlerActivity extends BaseActivity {
    private static final int  STR_LEN = 4;
    private ArrayList<Map<String, String>> listData = null; //存放显示的名称
    private String       rootPath = "/"; //起始目录
    private String       lastPath = "";
    private FileListAdapter   adapter;
    private int selectCount = 0;
    private ArrayList<Map<String, String>> selectList = null;
    /**.
     * 设置选择
     * @param selectCount 数量
     */
    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
        if (selectCount > 0){
            insertBtn.setEnabled(true);
        } else {
            insertBtn.setEnabled(false);
        }
    }

    @ViewInject(R.id.file_list_lv) private ListView  list;
    @ViewInject(R.id.show_path_tv) private TextView  showPathTV;
    @ViewInject(R.id.show_back_rl) private RelativeLayout   showBackTV;
    @ViewInject(R.id.insert_to_sql_btn) private Button   insertBtn;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ViewUtils.inject(this);
        getFileDir(rootPath);
    }
    /**.
     * 取得文件架构的method
     * @param filePath 文件路径
     */
    private void getFileDir(String filePath){
        setSelectCount(0);
        showPathTV.setText("路径：" + filePath);
        listData = new ArrayList<Map<String, String>>(); 
        selectList = new ArrayList<Map<String, String>>();
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (!filePath.equals(rootPath)){
            showBackTV.setVisibility(View.VISIBLE);
            lastPath = f.getParent();
        } else {
            showBackTV.setVisibility(View.GONE);
        }
        for (int i = 0; i < files.length; i++){
            handlerFolder(files, i);
        }
        adapter = new FileListAdapter(this);
        adapter.setListData(listData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(onListItemClick);
    }
    /**.
     * 处理操作文件件
     * @param files 文件夹
     * @param index 索引
     */
    private void handlerFolder(File[] files, int index){
        File file = files[index];
        if (!file.canRead()){
            return;
        }
        Map<String, String> item = new HashMap<String, String>();
        String count = FileListAdapter.MAP_COUNT_DEFAULT;
        if (file.isDirectory()){
            File[] ff = file.listFiles();
            if (ff == null){
                return;
            } else {
                count = ff.length + "";
            }
            item.put(FileListAdapter.MAP_FILE_STATE, FileListAdapter.MAP_FILE_NO_TXT);
        } else if (file.isFile() && file.toString().contains(".txt") && ".txt".equals(file.toString().substring(file.toString().length() - STR_LEN))){
            item.put(FileListAdapter.MAP_FILE_STATE, FileListAdapter.MAP_FILE_TXT_NO_CHECK);
        } else {
            return;
        }
        item.put(FileListAdapter.MAP_COUNT, count);
        item.put(FileListAdapter.MAP_PATH, file.getPath());
        item.put(FileListAdapter.MAP_NAME, file.getName());
        listData.add(item);
    }
    
    private AdapterView.OnItemClickListener onListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Map<String, String> map = listData.get(arg2);
            String path = MapUtil.getString(map, FileListAdapter.MAP_PATH);
            String txtState = MapUtil.getString(map, FileListAdapter.MAP_FILE_STATE);
            ImageView img = (ImageView) arg1.findViewById(R.id.right_icon_iv);
            if (FileListAdapter.MAP_FILE_TXT_YES_CHECK.equals(txtState)){
                map.put(FileListAdapter.MAP_FILE_STATE, FileListAdapter.MAP_FILE_TXT_NO_CHECK);
                img.setImageResource(R.drawable.check_nol);
                setSelectCount(selectCount - 1);
                selectList.remove(map);
            } else if (FileListAdapter.MAP_FILE_TXT_NO_CHECK.equals(txtState)){
                map.put(FileListAdapter.MAP_FILE_STATE, FileListAdapter.MAP_FILE_TXT_YES_CHECK);
                img.setImageResource(R.drawable.check_sel);
                setSelectCount(selectCount + 1);
                selectList.add(map);
            } else if (FileListAdapter.MAP_FILE_TXT_IMPORT.equals(txtState)){
                return;
            } else {
                File file = new File(path);
                if (file.canRead()){
                    if (file.isDirectory()){
                        getFileDir(path); //如果是文件夹再进去读取
                    } else {
                        showAlertDialog("Message", "["+file.getName()+"] is File!");
                    }
                } else {
                    showAlertDialog("Message", "权限不足！"); 
                }
            }
            
        }
    };
    /**.
     * 点击返回上一层
     * @param v view
     */
    @OnClick(R.id.show_back_rl)
    public void backClick(View v){
        getFileDir(lastPath);
    }
    /**.
     * 导入数据
     * @param v view
     */
    @OnClick(R.id.insert_to_sql_btn)
    public void insertBtnClick(View v){
        if (selectList != null && selectList.size() > 0){
            ArrayList<BookVO> bookList = DBManager.getInstance().getBookList();
            for (int i = 0; i < selectList.size(); i++){
                Map<String, String> map = selectList.get(i);
                String name = MapUtil.getString(map, FileListAdapter.MAP_NAME);
                boolean isExist = getDataExist(bookList, name);
                if (!isExist){
                    DBManager.getInstance().insertBookToDB(name.substring(0, name.length() - STR_LEN), 
                        FileListAdapter.MAP_PATH, "", "0", System.currentTimeMillis()+"");
                }
            }
        }
    }
    /**.
     * 判断重复处理
     * @param bookList 书列表
     * @param name 名称
     * @return bool
     */
    private boolean getDataExist(ArrayList<BookVO> bookList, String name){
        if (bookList != null && bookList.size() > 0){
            for (int j = 0; j < bookList.size(); j++){
                if (name.equals(bookList.get(j).name)){
                    return true;
                }
            }
        }
        return false;
    }
    /**.
     * 弹出提示窗
     * @param title 标题
     * @param message 内容
     */
    private void showAlertDialog(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK", onclickListener).show();
    }
    private DialogInterface.OnClickListener onclickListener = new DialogInterface.OnClickListener() {
      
        @Override
        public void onClick(DialogInterface dialog, int which) {
          
        }
    };
    
    
    
    
}
