package com.sxs.styd.stbook.util;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sxs.styd.stbook.R;
import com.sxs.styd.stbook.base.AppManager;
import com.sxs.styd.stbook.base.IActivity;
import com.sxs.styd.stbook.config.Constants;

/**.
* set default mock parameter.（方法说明）
* @return data manager(返回值说明)
* @throws Exception if has error(异常说明)
*/
public class AlertDialogs {
    public static final String TAG_EXIT = "exit"; //...
    public static final String TAG_DELETE = "delete"; //...
    public static final String TAG_NET = "net"; //...
    public static final Float PERCENT_WD = 0.6f; //...
    public static final Float PERCENT_HG = 0.2f; //...
    
    private Context context;
    private Button exitButton;
    private Button noButton;
    private TextView tvTitle;
    private TextView tvContent;
    private AlertDialog aDialog;
    private IActivity activity;
    
    /**.
     * 构造方法
     * @param context 常用的
     * @param activity 接口
     */
    public AlertDialogs(Context context, IActivity activity) {
        this.context = context;
        this.activity = activity;
    }
    /**.
     * 弹出提示窗口
     * @param title 标题
     * @param content 内容
     * @param btsString1 按钮一的名称
     * @param btString2 按钮二的名称
     * @param tag 设置回调参数类型
     */
    public void alertDialog(String title, String content, String btsString1,
        String btString2, String tag) {
        final View view;
        view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);
        exitButton = (Button) view.findViewById(R.id.right_btn);
        noButton = (Button) view.findViewById(R.id.left_btn);
        tvTitle = (TextView) view.findViewById(R.id.dialog_title);
        tvContent = (TextView) view.findViewById(R.id.dialog_content);
        exitButton.setText(btsString1);
        noButton.setText(btString2);
        tvTitle.setText(title);
        tvContent.setText(content);
        aDialog = new AlertDialog.Builder(context).create();
        aDialog.show();
        WindowManager.LayoutParams params = aDialog.getWindow().getAttributes();
        params.width =(int) (Constants.SCREEN_WIDTH * PERCENT_WD);
        params.height = (int) (Constants.SCREEN_HEIGHT* PERCENT_HG);
        aDialog.getWindow().setAttributes(params);
        aDialog.getWindow().setContentView(view);
        noButton.setTag(tag);
        exitButton.setTag(tag);
        noButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.dismiss();
                if (v.getTag().equals(TAG_EXIT)) {
                    aDialog.dismiss();
                } else if (v.getTag().equals(TAG_NET)) {
                    aDialog.dismiss();
                    AppManager.getInstance().appExit(context);
                }
            }
        });
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              if (v.getTag().equals(TAG_EXIT)) {
                    aDialog.dismiss();
                    AppManager.getInstance().appExit(context);
            } else if (v.getTag().equals(TAG_DELETE)) {
                    aDialog.dismiss();
                    activity.update();
            }
            }
        });
    }
}
