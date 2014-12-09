package com.sxs.styd.stbook.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sxs.styd.stbook.R;
import com.sxs.styd.stbook.vo.BookVO;
/**.
 * Adapter 适配器
 * @author user
 *
 */
public class BookGridViewAdapter extends BaseAdapter {
    
    private ArrayList<BookVO> listData;
    private Context myContext;
    
    /**.
     * 构造方法
     * @param context 上下文
     */
    public BookGridViewAdapter(Context context){
        myContext = context;
    }

    /**
     * @param listData the listData to set
     */
    public void setListData(ArrayList<BookVO> listData) {
        this.listData = listData;
    }

    @Override
      public int getCount() {
        return listData.size();
    }

    @Override
      public Object getItem(int position) {
          // TODO Auto-generated method stub
        return null;
    }

    @Override
      public long getItemId(int position) {
          // TODO Auto-generated method stub
        return 0;
    }

    @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(myContext).inflate(R.layout.book_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.bookTv = (TextView) convertView.findViewById(R.id.book_name_text);
            viewHolder.bookImgTv = (TextView) convertView.findViewById(R.id.book_name_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bookTv.setText(listData.get(position).name);
        viewHolder.bookImgTv.setText(listData.get(position).name);
        return convertView;
    }
    /**.
     * adapter 存储标签类
     * @author user
     *
     */
    class ViewHolder{
        public TextView bookTv;
        public TextView bookImgTv;
    }
}
