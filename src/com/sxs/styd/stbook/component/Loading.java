package com.sxs.styd.stbook.component;

import android.content.Context;

/**
 * loading
 */
public class Loading {
    
    private static CustomProgressDialog progressDialog = null;
        
    public static void show(Context myContext, String message) {
        if (!isShowing()){
            try {
                progressDialog = CustomProgressDialog.createDialog(myContext);
                progressDialog.setMessage(message);
                progressDialog.show();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public static void dismiss() {
        try {
            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static Boolean isShowing() {
        try {
            return progressDialog != null && progressDialog.isShowing();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
