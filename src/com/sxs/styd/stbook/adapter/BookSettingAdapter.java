package com.sxs.styd.stbook.adapter;

import java.util.ArrayList;

import com.sxs.styd.stbook.R;
import com.sxs.styd.stbook.adapter.BookGridViewAdapter.ViewHolder;
import com.sxs.styd.stbook.vo.SettingVO;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 设置适配器
 * @author user
 *
 */
public class BookSettingAdapter extends BaseAdapter {
    private Context myContext;
    private ArrayList<SettingVO> listData;
    
    /**
     * @param listData the listData to set
     */
    public void setListData(ArrayList<SettingVO> listData) {
        this.listData = listData;
    }
    public BookSettingAdapter(Context context){
        myContext = context;
    }
    @Override
    public int getCount() {
        return listData.size();
    }
  
    @Override
    public Object getItem(int position) {
        return null;
    }
  
    @Override
    public long getItemId(int position) {
        return 0;
    }
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(myContext).inflate(R.layout.book_setting_item, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTV = (TextView) convertView.findViewById(R.id.tv_setting_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleTV.setText(listData.get(position).textTitle);
        Drawable topDrawable = myContext.getResources().getDrawable(listData.get(position).imageId);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        viewHolder.titleTV.setCompoundDrawables(null, topDrawable, null, null);
        return convertView;
    }
    /**
     * 文件设置
     * @author user
     */
    class ViewHolder{
        private TextView titleTV;
    }
}
