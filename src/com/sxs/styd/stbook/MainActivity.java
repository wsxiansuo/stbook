package com.sxs.styd.stbook;

import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sxs.styd.stbook.base.IActivity;
import com.sxs.styd.stbook.vo.BookVO;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class MainActivity extends Activity implements IActivity{

	public static final String TAG = MainActivity.class.getSimpleName();
	@ViewInject(R.id.book_grid)  		GridView  book_grid;
	
	
	private ArrayList<BookVO> bookDataList = null;
	
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
				
				return false;
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
		
	}
	/**
	 * 回调更新页面
	 */
	@Override
	public void update(){
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
