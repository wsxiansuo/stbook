package com.sxs.styd.stbook.base;

import com.sxs.styd.stbook.data.DBManager;

import android.app.Application;

/**.
 * 系统启动的application
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
        DBManager.getInstance().setContext(getApplicationContext()); //系统启动时初始化数据库
    }

}
