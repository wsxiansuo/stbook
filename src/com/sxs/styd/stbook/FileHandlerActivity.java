package com.sxs.styd.stbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import com.sxs.styd.stbook.util.TxtReadUtil;
import com.sxs.styd.stbook.vo.BookVO;
import com.sxs.styd.stbook.vo.SectionVO;
/**.
 * �����ļ�Ŀ¼�ļ���д
 * @author user
 *
 */
public class FileHandlerActivity extends BaseActivity {
    private static final int  STR_LEN = 4;
    private static final String CHAPTER_TAG = "\\u7b2c[\\s\\d\\u96f6\\u4e00\\u4e8c\\u4e09\\u56db\\u4e94\\u516d\\u4e03\\u516b\\u4e5d\\u5341\\u767e\\u5343\\u4e07]{1,10}[\\u7ae0\\u5377\\u56de\\u8bdd\\u8282\\u96c6]";
    private static String CHAPTERPATTER ="(" + CHAPTER_TAG + "$" + ")" + "|" + "(" + CHAPTER_TAG + ".*?[^,.;?!��������������]$"+")";
    private ArrayList<Map<String, String>> listData = null; //�����ʾ������
    private String       rootPath = "/"; //��ʼĿ¼
    private String       lastPath = "";
    private FileListAdapter   adapter;
    private int selectCount = 0;
    private ArrayList<Map<String, String>> selectList = null;
    /**.
     * ����ѡ��
     * @param selectCount ����
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
     * ȡ���ļ��ܹ���method
     * @param filePath �ļ�·��
     */
    private void getFileDir(String filePath){
        setSelectCount(0);
        showPathTV.setText("·����" + filePath);
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
     * ��������ļ���
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
                        getFileDir(path); //������ļ����ٽ�ȥ��ȡ
                    } else {
                        showAlertDialog("Message", "["+file.getName()+"] is File!");
                    }
                } else {
                    showAlertDialog("Message", "Ȩ�޲��㣡"); 
                }
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
     * ��������
     * @param v view
     */
    @OnClick(R.id.insert_to_sql_btn)
    public void insertBtnClick(View v){
        showLoading();
        new Thread(new MyThread()).start();
    }
    private void readBookTxt(BookVO bookItem){
        String content = ""; //�ļ������ַ���
        File file = new File(bookItem.path); //���ļ�
        if (file.isDirectory()){ //���path�Ǵ��ݹ����Ĳ�����������һ����Ŀ¼���ж�
            return;
        } 
        try {
            InputStream instream = new FileInputStream(file); 
            if (instream != null){
                int length = instream.available();  
                byte[] buffer = new byte[length];  
                instream.read(buffer);  
                content = EncodingUtils.getString(buffer, TxtReadUtil.STR_CHAREST_NAME);  
                instream.close();
                Log.i(TAG, content.length() + "");
                String[] arr = content.split("\r\n");
                Pattern pat = Pattern.compile(CHAPTERPATTER);  
                int splitLen = "\r\n".getBytes(TxtReadUtil.STR_CHAREST_NAME).length;
                int postion = 0;
                String word;
                if (arr != null && arr.length > 0){
                    int len = arr.length;
                    for (int i = 0; i < len; i++){
                        word = arr[i];
                        Matcher mat = pat.matcher(word);
                        if (mat.find()){
                            Log.i(TAG, postion + word);
                            SectionVO item = new SectionVO();
                            item.bookId = bookItem.id;
                            item.name = word.length() > 15 ? word.substring(0, 15) : word;
                            item.postion = postion;
                            DBManager.getInstance().insertSectionToDB(item);
                        }
                        postion += splitLen + word.getBytes(TxtReadUtil.STR_CHAREST_NAME).length;
                    }
                }
            }
        } catch (java.io.FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }
        
    }
    
    /**.
     * �ж��ظ�����
     * @param bookList ���б�
     * @param name ����
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
     * ������ʾ��
     * @param title ����
     * @param message ����
     */
    private void showAlertDialog(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK", onclickListener).show();
    }
    private DialogInterface.OnClickListener onclickListener = new DialogInterface.OnClickListener() {
      
        @Override
        public void onClick(DialogInterface dialog, int which) {
          
        }
    };
    
    private void insertBook(){
        if (selectList != null && selectList.size() > 0){
            ArrayList<BookVO> bookList = DBManager.getInstance().getBookList();
            for (int i = 0; i < selectList.size(); i++){
                Map<String, String> map = selectList.get(i);
                String name = MapUtil.getString(map, FileListAdapter.MAP_NAME);
                boolean isExist = getDataExist(bookList, name);
                if (!isExist){
                    DBManager.getInstance().insertBookToDB(name.substring(0, name.length() - STR_LEN), 
                        MapUtil.getString(map, FileListAdapter.MAP_PATH), "", "0", System.currentTimeMillis()+"", "0");
                }
            }
        }
    }
    private void insertSection(){
        ArrayList<BookVO> noStateList = DBManager.getInstance().getBookListByState();
        if (noStateList != null && noStateList.size() > 0){
            int count = noStateList.size();
            for (int j = 0; j < count; j++){
                BookVO item = noStateList.get(j);
                readBookTxt(item);
                DBManager.getInstance().updateBookById(item.id, item.lastPostion + "", item.lastTime + "", "1");
            }
        }
    }
    /**
     *  ���߳�ִ��
     * @author user
     */
    public class MyThread implements Runnable {
        @Override
        public void run() {
            insertBook();
            insertSection();
            hideLoading();
        }
    }
}
