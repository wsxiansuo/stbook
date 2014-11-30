package com.sxs.styd.stbook;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sxs.styd.stbook.adapter.BookGridViewAdapter;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.base.IActivity;
import com.sxs.styd.stbook.data.DBManager;
import com.sxs.styd.stbook.util.AlertDialogs;
import com.sxs.styd.stbook.vo.BookVO;

/**.
 * MainActivity
 * @author xssong
 */
public class MainActivity extends BaseActivity implements IActivity{
    public static final int    COUNT = 20;
    public static final String TAG = MainActivity.class.getSimpleName();
    @ViewInject(R.id.book_grid)       GridView  bookGrid;
    @ViewInject(R.id.nodata_tip_tv)   TextView  noDataTipTV;
    private ArrayList<BookVO> bookDataList = null;
    private BookGridViewAdapter gridAdapter;
    private int deletePost;
	
      /**.
       * oncreate
       * @param savedInstanceState ״̬����
       */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
//        initData();
        bookGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deletePost = position;
                new AlertDialogs(MainActivity.this, MainActivity.this).alertDialog("ȷ��ɾ����", "", "ɾ��", "ȡ��", "delete");
                return true;
            }
        });
    }
    /**.
     * ��ʼ������
     */
    private void initData(){
        bookDataList = DBManager.getInstance().getBookList();
        if (bookDataList != null && bookDataList.size() == 0){
            bookGrid.setVisibility(View.INVISIBLE);
            noDataTipTV.setVisibility(View.VISIBLE);
        } else {
            bookGrid.setVisibility(View.VISIBLE);
            noDataTipTV.setVisibility(View.GONE);
            gridAdapter = new BookGridViewAdapter(this);
            gridAdapter.setListData(bookDataList);
            bookGrid.setAdapter(gridAdapter);
        }
    }
    /**.
     * dianji
     * @param v view
     */
    @OnClick(R.id.nodata_tip_tv)
    public void onTVClick(View v){
        openActivity(FileHandlerActivity.class);
        Log.i("aaa", "tiao zhuan");
    }
	/**.
	 * �ص�����ҳ��
	 */
    @Override
    public void update(){
        if (deletePost >= 0){
            DBManager.getInstance().deleteBookById(bookDataList.get(deletePost).id);
            bookDataList.remove(deletePost);
            deletePost = 0;
            gridAdapter.setListData(bookDataList);
            gridAdapter.notifyDataSetChanged();
            if (bookDataList.size() == 0){
                bookGrid.setVisibility(View.INVISIBLE);
                noDataTipTV.setVisibility(View.VISIBLE);
            }
        }
    }
    /**.
     * ����ص���ʱ��ˢ����
     */
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
