package com.sxs.styd.stbook.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**.
 * ������
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
	 * ͨ����������Activity
	 * @param pClass class��
	 */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

	/**.
	 * ͨ����������Activity�����Һ���Bundle����
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
	 * ͨ��Action����Activity
	 * @param pAction ����
	 */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

	/**.
	 * ͨ��Action����Activity�����Һ���Bundle����
	 * 
	 * @param pAction ����
	 * @param pBundle ����
	 */
    protected void openActivity(String pAction, Bundle pBundle) {
      	Intent intent = new Intent(pAction);
      	if (pBundle != null) {
      	    intent.putExtras(pBundle);
      	}
      	startActivity(intent);
    }
	/**.
	 * ��ʾtoast��Ϣ
	 * @param text ��ʾ�ı�
	 */
    protected void showToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
	
}
