package com.sxs.styd.stbook.base;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**.
 * 应用程序Activity管理类
 * @author user
 */
public class AppManager {
    private static Stack<Activity> mActivityStack;
    private static AppManager mAppManager;
    /**.
     * 构造函数
     */
    public AppManager(){
    	
    }
	
  	/**.
  	 * 单一实例
  	 * @return 返回app实例
  	 */
    public static AppManager getInstance(){
    	if (mAppManager == null){
    	    mAppManager = new AppManager();
    	}
    	return mAppManager;
    }
	
    /**.
     * 添加Activity到堆栈
     * @param activity 啊啊啊
     */
    public void addActivity(Activity activity){
    	if (mActivityStack == null){
    	    mActivityStack = new Stack<Activity>();
    	}
    	mActivityStack.add(activity);
    }
	
	/**.
	 * 获取堆栈中的Activity（最后一个压入的）
	 * @return Activity
	 */
    public Activity getTopActivity(){
        Activity activity = mActivityStack.lastElement();
        return activity;
    }
	
	/**.
	 * 
	 * 结束栈顶Activity（堆栈中最后一个压入的）
	 */
    public void killTopActivity(){
        Activity activity = mActivityStack.lastElement();
        killActivity(activity);
    }
	/**.
	 * 结束指定的activity
	 * @param activity 啊 啊
	 */
    public void killActivity(Activity activity) {
        if (activity != null){
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    /**.
     * 结束指定类名的Activity
     * @param cls 泛型类选择器
     */
    public void killActivity(Class<?> cls){
        for (Activity activity : mActivityStack){
            if (activity.getClass().equals(cls)){
                killActivity(activity);
            }
        }
    }
	
	/**.
	 * 结束所有的Activity
	 */
    public void killAllActivity(){
        for (int i = 0, size = mActivityStack.size(); i < size; i++){
            if (null != mActivityStack.get(i)){
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }
	
	/**.
	 * 退出应用程序
	 * @param context 上下文
	 */
    public void appExit(Context context){
        try {
            killAllActivity();
            ActivityManager activityMgr = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
