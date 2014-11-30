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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sxs.styd.stbook.adapter.FileListAdapter;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.util.MapUtil;
/**.
 * �����ļ�Ŀ¼�ļ���д
 * @author user
 *
 */
public class FileHandlerActivity extends BaseActivity {
    private static final int  STR_LEN = 4;
    private ArrayList<Map<String, String>> listData = null; //�����ʾ������
    private String       rootPath = "/"; //��ʼĿ¼
    private String       lastPath = "";
    private FileListAdapter   adapter;
    
    @ViewInject(R.id.file_list_lv) private ListView  list;
    @ViewInject(R.id.show_path_tv) private TextView  showPathTV;
    @ViewInject(R.id.show_back_rl) private RelativeLayout   showBackTV;
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
     * ȡ���ļ��ܹ���method
     * @param filePath �ļ�·��
     */
    private void getFileDir(String filePath){
        showPathTV.setText("·����" + filePath);
        listData = new ArrayList<Map<String, String>>(); 
        
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
     * ���������ļ���
     * @param files �ļ���
     * @param index ����
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
            } else if (FileListAdapter.MAP_FILE_TXT_NO_CHECK.equals(txtState)){
                map.put(FileListAdapter.MAP_FILE_STATE, FileListAdapter.MAP_FILE_TXT_YES_CHECK);
                img.setImageResource(R.drawable.check_sel);
            } else if (FileListAdapter.MAP_FILE_TXT_IMPORT.equals(txtState)){
                return;
            }
            File file = new File(path);
            if (file.canRead()){
                if (file.isDirectory()){
                    getFileDir(path); //������ļ����ٽ�ȥ��ȡ
                } else {
                    showAlertDialog("Message", "["+file.getName()+"] is File!");
                }
            } else {
                showAlertDialog("Message", "Ȩ�޲��㣡"); 
            }
        }
    };
    /**.
     * ���������һ��
     * @param v view
     */
    @OnClick(R.id.show_back_rl)
    public void backClick(View v){
        getFileDir(lastPath);
    }

    /**.
     * ������ʾ��
     * @param title ����
     * @param message ����
     */
    private void showAlertDialog(String title, String message){
        new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", onclickListener).show();
    }
    private DialogInterface.OnClickListener onclickListener = new DialogInterface.OnClickListener() {
      
        @Override
        public void onClick(DialogInterface dialog, int which) {
          
        }
    };
    
    
    
    
}