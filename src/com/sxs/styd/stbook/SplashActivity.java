package com.sxs.styd.stbook;

import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.config.Constants;
/**.
 * ��������
 * @author user
 *
 */
public class SplashActivity extends BaseActivity {
    
    public static final String TAG = SplashActivity.class.getSimpleName();
    
    @ViewInject(R.id.splash_loading_item)  ImageView mSplashItem_iv;
    
    /* (non-Javadoc)
     * @see com.sxs.styd.stbook.base.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ViewUtils.inject(this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Constants.SCREEN_DENSITY = metrics.density;
        Constants.SCREEN_WIDTH = metrics.widthPixels;
        Constants.SCREEN_HEIGHT = metrics.heightPixels;
        Constants.SCREEN_LIGHT = getScreenBrightness();
        initView();
    }
    /**
     * ��õ�ǰ��Ļ����ֵ 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {
            Log.i("error", localException.getLocalizedMessage());
        }
        return screenBrightness;
    }
    /**.
     * ��ʼ��view
     */
    private void initView(){
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
        translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                openActivity(MainActivity.class);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                SplashActivity.this.finish();
            }
        });
        mSplashItem_iv.setAnimation(translate);
    }
    
}
