package com.example.easyioclibrary.CommonDialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/06/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CommDialogActivity extends Activity implements ICommonDialog{

    private DialogViewHelper viewHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewHelper.getContentView());
    }



    @Override
    public void setText(int viewid, CharSequence text) {

    }

    @Override
    public void setClickListener(int i, View.OnClickListener listener) {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismissDialog() {

    }

}
