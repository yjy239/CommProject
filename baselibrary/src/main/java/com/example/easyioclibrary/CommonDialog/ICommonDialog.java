package com.example.easyioclibrary.CommonDialog;

import android.view.View;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/06/04
 *     desc   :暴露出来的dialog操作
 *     version: 1.0
 * </pre>
 */

public interface ICommonDialog {

    /**
     * 设置text
     * @param viewid
     * @param text
     */
    void setText(int viewid, CharSequence text);


    /**
     * 设置点击事件
     * @param i
     * @param listener
     */
    void setClickListener(int i, View.OnClickListener listener);

    /**
     * 显示
     */
    void showDialog();

    /**
     * 销毁
     */
    void dismissDialog();

}
