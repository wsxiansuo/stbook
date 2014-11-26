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
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.base.IActivity;
import com.sxs.styd.stbook.config.Constants;


public class AlertDialogs {
	public static final String TAG_EXIT = "exit";
	public static final String TAG_DELETE = "delete";
	public static final String TAG_NET = "net";
	
	private Context context;
	private Button exitButton;
	private Button noButton;
	private TextView tv_title, tv_content;
	private AlertDialog aDialog;
	private IActivity activity;

	public AlertDialogs(Context context, IActivity activity) {
		this.context = context;
		this.activity = activity;
	}

	public void alertDialog(String title, String content, String btsString1,
			String btString2, String tag) {
		final View view;
		view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout,null);
		exitButton = (Button) view.findViewById(R.id.right_btn);
		noButton = (Button) view.findViewById(R.id.left_btn);
		tv_title = (TextView) view.findViewById(R.id.dialog_title);
		tv_content = (TextView) view.findViewById(R.id.dialog_content);
		exitButton.setText(btsString1);
		noButton.setText(btString2);
		tv_title.setText(title);
		tv_content.setText(content);
		aDialog = new AlertDialog.Builder(context).create();
		aDialog.show();
		WindowManager.LayoutParams params = aDialog.getWindow().getAttributes();
		params.width =(int) (Constants.SCREEN_WIDTH*0.6);
		params.height = (int) (Constants.SCREEN_HEIGHT*0.2);
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
					AppManager.getInstance().AppExit(context);
				}
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			
				if (v.getTag().equals(TAG_EXIT)) {
					aDialog.dismiss();
					AppManager.getInstance().AppExit(context);
				} else if (v.getTag().equals(TAG_DELETE)) {
					aDialog.dismiss();
					activity.update();
				}
			}
		});
	}

}
