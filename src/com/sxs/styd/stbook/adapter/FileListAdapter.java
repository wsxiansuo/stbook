package com.sxs.styd.stbook.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxs.styd.stbook.R;
import com.sxs.styd.stbook.util.MapUtil;

/**.
 *  文件目录信息
 * @author xiansuo
 *
 */
public class FileListAdapter extends BaseAdapter {
    public static final String MAP_NAME = "NAME";
    public static final String MAP_PATH = "PATH";
    public static final String MAP_COUNT = "COUNT";
    public static final String MAP_FILE_STATE = "FILE_STATE";
    
    public static final String MAP_COUNT_DEFAULT = "0";
    public static final String MAP_FILE_NO_TXT = "0";
    public static final String MAP_FILE_TXT_NO_CHECK = "1";
    public static final String MAP_FILE_TXT_YES_CHECK = "2";
    public static final String MAP_FILE_TXT_IMPORT = "3";
    
    
    private Context myContext;
    private ArrayList<Map<String, String>> listData;
    /**.
     * 设置数据源
     * @param listData listData
     */
    public void setListData(ArrayList<Map<String, String>> listData) {
        this.listData = listData;
    }
    /**.
     * 构造方法
     * @param context context
     */
    public FileListAdapter(Context context){
        myContext = context;
    }
    
    @Override
    public int getCount() {
        return listData.size();
    }
  
    @Override
    public Object getItem(int arg0) {
        return null;
    }
  
    @Override
    public long getItemId(int arg0) {
        return 0;
    }
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(myContext).inflate(R.layout.file_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.fileNameTV = (TextView) convertView.findViewById(R.id.file_name_tv);
            viewHolder.fileCountTV = (TextView) convertView.findViewById(R.id.file_count_tv);
            viewHolder.rightIconIV = (ImageView) convertView.findViewById(R.id.right_icon_iv);
            viewHolder.leftIconIV = (ImageView) convertView.findViewById(R.id.file_icon_iv);
            viewHolder.rightImportTV = (TextView) convertView.findViewById(R.id.right_import_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Map<String, String> map = listData.get(position);
        viewHolder.fileNameTV.setText(MapUtil.getString(map, MAP_NAME));
        String count = MapUtil.getString(listData.get(position), MAP_COUNT);
        if (MAP_COUNT_DEFAULT.equals(count) || "".equals(count)){
            viewHolder.fileCountTV.setText("");
        } else {
            viewHolder.fileCountTV.setText(count + "个文件");
        }
        String state = MapUtil.getString(map, MAP_FILE_STATE);
        int fileCountVB = View.VISIBLE;
        int rightIconVB = View.VISIBLE;
        int rightImportVB = View.VISIBLE;
        if (MAP_FILE_TXT_NO_CHECK.equals(state)){
            viewHolder.leftIconIV.setImageResource(R.drawable.txt_icon);
            viewHolder.rightIconIV.setImageResource(R.drawable.check_nol);
            fileCountVB = View.GONE;
            rightImportVB = View.GONE;
        } else if (MAP_FILE_TXT_IMPORT.equals(state)){
            viewHolder.leftIconIV.setImageResource(R.drawable.txt_icon);
            fileCountVB = View.GONE;
            rightIconVB = View.INVISIBLE;
        } else {
            viewHolder.leftIconIV.setImageResource(R.drawable.folder);
            viewHolder.rightIconIV.setImageResource(R.drawable.jiantou_right);
            rightImportVB = View.GONE;
        }
        viewHolder.fileCountTV.setVisibility(fileCountVB);
        viewHolder.rightIconIV.setVisibility(rightIconVB);
        viewHolder.rightImportTV.setVisibility(rightImportVB);
        return convertView;
    }
    /**.
     * 文件操作类
     * @author xiansuo
     *
     */
    class ViewHolder{
        TextView    rightImportTV;
        ImageView   leftIconIV;
        TextView    fileNameTV;
        TextView    fileCountTV;
        ImageView   rightIconIV;
    }
}
