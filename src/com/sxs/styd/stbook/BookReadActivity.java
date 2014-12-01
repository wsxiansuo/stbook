package com.sxs.styd.stbook;

import android.content.Intent;
import android.os.Bundle;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sxs.styd.stbook.base.BaseActionBar;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.vo.BookVO;
/**
 * ‘ƒ∂¡¿‡
 * @author user
 *
 */
public class BookReadActivity extends BaseActivity {
    
    @ViewInject(R.id.ab_topbar)     private BaseActionBar  abTopBar;
    
    private BookVO        currItem;
    
    /* (non-Javadoc)
     * @see com.sxs.styd.stbook.base.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_read);
        ViewUtils.inject(this);
    }
    /* (non-Javadoc)
     * @see com.sxs.styd.stbook.base.BaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra("itemVO")){
            currItem = (BookVO) intent.getExtras().getSerializable("itemVO");
            intent.removeExtra("itemVO");
            abTopBar.setTitle(currItem.name);
            
            
        }
    }
    
}
