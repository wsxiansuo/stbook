package com.sxs.styd.stbook;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sxs.styd.stbook.adapter.BookGridViewAdapter;
import com.sxs.styd.stbook.base.IActivity;
import com.sxs.styd.stbook.util.AlertDialogs;
import com.sxs.styd.stbook.vo.BookVO;

/**.
 * MainActivity
 * @author xssong
 */
public class MainActivity extends Activity implements IActivity{
    public static final int    COUNT = 20;
    public static final String TAG = MainActivity.class.getSimpleName();
    @ViewInject(R.id.book_grid)  		GridView  bookGrid;
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
        initData();
        bookGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
        
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    deletePost = position;
                    new AlertDialogs(MainActivity.this, MainActivity.this).alertDialog("确定删除吗？", "", "删除", "取消", "delete");
                    return true;
                }
        });
    }
    /**.
     * 初始化数据
     */
    private void initData(){
        bookDataList = new ArrayList<BookVO>();
        for (int i = 0; i < COUNT; i++){
            BookVO item = new BookVO();
            item.id = i + "";
            item.name = "测试呢吗" + i;
            bookDataList.add(item);
        }
        gridAdapter = new BookGridViewAdapter(this);
        gridAdapter.setListData(bookDataList);
        bookGrid.setAdapter(gridAdapter);
    }
	/**.
	 * 回调更新页面
	 */
    @Override
    public void update(){
        if (deletePost >= 0){
            bookDataList.remove(deletePost);
            deletePost = 0;
            gridAdapter.setListData(bookDataList);
            gridAdapter.notifyDataSetChanged();
        }
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
