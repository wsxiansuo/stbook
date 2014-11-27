package com.sxs.styd.stbook.base;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**.
 * Ӧ�ó���Activity������
 * @author user
 */
public class AppManager {
    private static Stack<Activity> mActivityStack;
    private static AppManager mAppManager;
    /**.
     * ���캯��
     */
    public AppManager(){
    	
    }
	
  	/**.
  	 * ��һʵ��
  	 * @return ����appʵ��
  	 */
    public static AppManager getInstance(){
    	if (mAppManager == null){
    	    mAppManager = new AppManager();
    	}
    	return mAppManager;
    }
	
    /**.
     * ���Activity����ջ
     * @param activity ������
     */
    public void addActivity(Activity activity){
    	if (mActivityStack == null){
    	    mActivityStack = new Stack<Activity>();
    	}
    	mActivityStack.add(activity);
    }
	
	/**.
	 * ��ȡ��ջ�е�Activity�����һ��ѹ��ģ�
	 * @return Activity
	 */
    public Activity getTopActivity(){
        Activity activity = mActivityStack.lastElement();
        return activity;
    }
	
	/**.
	 * 
	 * ����ջ��Activity����ջ�����һ��ѹ��ģ�
	 */
    public void killTopActivity(){
        Activity activity = mActivityStack.lastElement();
        killActivity(activity);
    }
	/**.
	 * ����ָ����activity
	 * @param activity �� ��
	 */
    public void killActivity(Activity activity) {
        if (activity != null){
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    /**.
     * ����ָ��������Activity
     * @param cls ������ѡ����
     */
    public void killActivity(Class<?> cls){
        for (Activity activity : mActivityStack){
            if (activity.getClass().equals(cls)){
                killActivity(activity);
            }
        }
    }
	
	/**.
	 * �������е�Activity
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
	 * �˳�Ӧ�ó���
	 * @param context ������
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
