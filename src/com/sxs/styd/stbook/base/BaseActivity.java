package com.sxs.styd.stbook.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**.
 * 基础类
 * @author user
 *
 */
public class BaseActivity extends Activity {
	
    public static final String TAG = BaseActivity.class.getSimpleName();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
    @Override
    protected void onResume() {
        super.onResume();
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
    @Override
    protected void onPause() {
        super.onPause();
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
    @Override
    protected void onStop() {
        super.onStop();
    }
	
	/**.
	 * 通过类名启动Activity
	 * @param pClass class类
	 */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

	/**.
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass clasee
	 * @param pBundle bundle
	 */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
      	Intent intent = new Intent(this, pClass);
      	if (pBundle != null) {
      	    intent.putExtras(pBundle);
      	}
      	intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
      	startActivity(intent);
    }

	/**.
	 * 通过Action启动Activity
	 * @param pAction 动作
	 */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

	/**.
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction 动作
	 * @param pBundle 数据
	 */
    protected void openActivity(String pAction, Bundle pBundle) {
      	Intent intent = new Intent(pAction);
      	if (pBundle != null) {
      	    intent.putExtras(pBundle);
      	}
      	startActivity(intent);
    }
	/**.
	 * 显示toast信息
	 * @param text 显示文本
	 */
    protected void showToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
	
}
