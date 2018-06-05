package com.example.easyioclibrary.CommonDialog;

import android.content.DialogInterface;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/06/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface ICommonParams {

    void apply(CommonController commonController);

    int getHeight();

    int getWidth();

    int getGravity();

    boolean getCancelable();

    DialogInterface.OnCancelListener getCancelListener();

    DialogInterface.OnDismissListener getDismissListener();

    DialogInterface.OnKeyListener getKeyListener();

}
