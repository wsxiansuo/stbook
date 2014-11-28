package com.sxs.styd.stbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**.
 * �����ļ�Ŀ¼�ļ���д
 * @author user
 *
 */
public class FileHandlerActivity extends ListActivity {
    private List<String> items = null; //�����ʾ������
    private List<String> paths = null; //����ļ�·��
    private String       rootPath = "/"; //��ʼĿ¼
    private TextView     mPath;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        mPath = (TextView) findViewById(R.id.mpath);
        getFileDir(rootPath);
      
    }
    /**.
     * ȡ���ļ��ܹ���method
     * @param filePath �ļ�·��
     */
    private void getFileDir(String filePath){
        mPath.setText(filePath);
        
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (!filePath.equals(rootPath)){
            items.add("Back to " + rootPath);
            paths.add(rootPath);
            items.add("Back to ../");
            paths.add(f.getParent());
        }
        
        for (int i = 0; i < files.length; i++){
            File file = files[i];
            items.add(file.getName());
            paths.add(file.getPath());
        }
        
        ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items); 
        setListAdapter(adapter);
    }
    /* (non-Javadoc)
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
        File file = new File(paths.get(position));
        if (file.canRead()){
            if (file.isDirectory()){
                getFileDir(paths.get(position)); //������ļ����ٽ�ȥ��ȡ
            } else {
                showAlertDialog("Message", "["+file.getName()+"] is File!");
            }
        } else {
            showAlertDialog("Message", "Ȩ�޲��㣡"); 
        }
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
