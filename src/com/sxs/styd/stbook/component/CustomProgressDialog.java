package com.sxs.styd.stbook.component;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxs.styd.stbook.R;

/**
 * ����loading Ч��
 * @author user
 *
 */
public class CustomProgressDialog extends Dialog {
    private static CustomProgressDialog instance = null;
    
    public CustomProgressDialog(Context context){
        super(context);
    }
    
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
    
    
    public static CustomProgressDialog createDialog(Context context){
        instance = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        instance.setContentView(R.layout.customprogressdialog);
        instance.setCanceledOnTouchOutside(false);
        instance.getWindow().getAttributes().gravity = Gravity.CENTER;
        
        return instance;
    }
    public void onWindowFocusChanged(boolean hasFocus){
        if (instance == null){
            return;
        }
        ImageView imageView = (ImageView) instance.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
 
    public CustomProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)instance.findViewById(R.id.tv_loadingmsg);
        
        if (tvMsg != null && strMessage != null && strMessage != ""){
            //添加空格，为了不让文字，紧靠右边�?
            tvMsg.setText(strMessage + "    ");
        }
        
        return instance;
    }
    
    @Override
    public void dismiss() {
        super.dismiss();
    }
}
