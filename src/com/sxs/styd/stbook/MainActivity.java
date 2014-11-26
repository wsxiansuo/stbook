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

public class MainActivity extends Activity implements IActivity{

	public static final String TAG = MainActivity.class.getSimpleName();
	@ViewInject(R.id.book_grid)  		GridView  book_grid;
	
	
	private ArrayList<BookVO> bookDataList = null;
	private BookGridViewAdapter grid_adapter;
	private int delete_post;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		initData();
		book_grid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
					delete_post = position;
				    new AlertDialogs(MainActivity.this,MainActivity.this)
				   .alertDialog("确定删除吗？", "", "删除", "取消", "delete");
				return true;
			}
		});
	}
	
	private void initData(){
		bookDataList = new ArrayList<BookVO>();
		for(int i = 0;i < 20;i++){
			BookVO item = new BookVO();
			item.id = i + "";
			item.name = "测试" +i;
			item.author = "路人甲";
			bookDataList.add(item);
		}
		grid_adapter = new BookGridViewAdapter(this);
		grid_adapter.setListData(bookDataList);
		book_grid.setAdapter(grid_adapter);
		
	}
	/**
	 * 回调更新页面
	 */
	@Override
	public void update(){
		if(delete_post >= 0){
			bookDataList.remove(delete_post);
			delete_post = 0;
			grid_adapter.setListData(bookDataList);
			grid_adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
