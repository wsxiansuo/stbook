package com.sxs.styd.stbook.base;

import com.sxs.styd.stbook.data.DBManager;

import android.app.Application;

/**.
 * ϵͳ������application
 * @author user
 *
 */
public class BaseApplication extends Application {

  /* (non-Javadoc)
   * @see android.app.Application#onCreate()
   */
    @Override
    public void onCreate() {
        super.onCreate();
        DBManager.getInstance().setContext(getApplicationContext()); //ϵͳ����ʱ��ʼ�����ݿ�
    }

}
