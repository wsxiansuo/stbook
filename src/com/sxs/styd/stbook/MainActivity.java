package com.sxs.styd.stbook;

import java.io.File;
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

/**
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
     * @param savedInstanceState 状态保存
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
                new AlertDialogs(MainActivity.this, MainActivity.this).alertDialog("确定删除吗？", "", "删除", "取消", "delete");
                return true;
            }
        });
        bookGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookVO item = bookDataList.get(position);
                if (item != null){
                    File file = new File(item.path);
                    if (file.length() == 0){
                        showToast("该文件为空文件");
                    } else {
                        DBManager.getInstance().updateBookById(item.id, item.lastPostion + "", System.currentTimeMillis() + "");
                        Bundle bd = new Bundle();
                        bd.putSerializable("itemVO", item);
                        openActivity(BookReadActivity.class, bd);
//                        openActivity(pClass, pBundle)
//                        itemVO = (ProductItemVO)intent.getExtras().getSerializable("item");
                    }
                }
            }
        });
    }
    /**.
     * 初始化数据
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
	 * 回调更新页面
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
     * 处理回调的时候刷数据
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
