package com.example.easyioclibrary.CommonDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.example.easyioclibrary.R;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/06/04
 *     desc   :抽象出来
 *     version: 1.0
 * </pre>
 */

public class EasyDialog implements ICommonDialog {

    private CommonController mCommon;
    private ICommonDialog mDialog;

    public EasyDialog(ICommonDialog dialog){
        this.mDialog = dialog;
        mCommon = new CommonController((CommonDialog)dialog,((CommonDialog) dialog).getWindow());
    }

    public EasyDialog(){

    }

    @Override
    public void setText(int viewid, CharSequence text) {
        mDialog.setText(viewid,text);
    }

    @Override
    public void setClickListener(int i, View.OnClickListener listener) {
        mDialog.setClickListener(i,listener);
    }

    @Override
    public void showDialog() {
        mDialog.showDialog();
    }

    @Override
    public void dismissDialog() {
        mDialog.dismissDialog();
    }


    public static class Builder{

        private final CommonController.CommonParams P;
        private Context mContext;

        public Builder(Context context){
            this(context, R.style.dialog);
        }

        /**
         * Set a custom view to be the contents of the Dialog. If the supplied view is an instance
         * of a {@link Dialog} the light background will be used.
         *
         * @param view The view to use as the contents of the Dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public EasyDialog.Builder setContentView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }

        //设置内容id
        public EasyDialog.Builder setContentView(int resid) {
            P.mView = null;
            P.mViewLayoutResId = resid;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public EasyDialog.Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         *
         * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
         * being canceled or one of the supplied choices being selected.
         * If you are interested in listening for all cases where the dialog is dismissed
         * and not just when it is canceled, see
         * {@link #setOnDismissListener(DialogInterface.OnDismissListener) setOnDismissListener}.</p>
         * @see #setCancelable(boolean)
         * @see #setOnDismissListener(DialogInterface.OnDismissListener)
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public EasyDialog.Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public EasyDialog.Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public EasyDialog.Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder asActivity(){
            P.isActivity = true;
            return this;
        }

        //设置文本
        public EasyDialog.Builder setText(int resid, CharSequence text){
            P.textMap.put(resid,text);
            return this;
        }

        //设置点击事件
        public EasyDialog.Builder setOnClickListener(int resid, WeakReference<View.OnClickListener> listener){
            P.clickMap.put(resid,listener);
            return this;
        }

        //配置一些通用参数
        //设置满屏幕
        public EasyDialog.Builder fullWidth(){
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        //从底部
        public EasyDialog.Builder fromBottom(boolean isAnimation){
            if(isAnimation){
                P.mAniamtion = R.style.dialog_from_bottom;
            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }

        //设置宽高
        public EasyDialog.Builder setWidthAndHeight(int width, int height){
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }

        //添加默认动画
        public EasyDialog.Builder addDefaultAnimation(){
            P.mAniamtion = R.style.default_animation;
            return this;
        }

        //添加动画
        public EasyDialog.Builder addAnimation(int styleAnimation){
            P.mAniamtion = styleAnimation;
            return this;
        }


        public Builder(Context context,int themid){
            P =  new CommonController.CommonParams(context,themid);
            mContext = context;
        }

        public ICommonDialog create() {

            EasyDialog easyDialog = null;
            if(!P.isActivity){
                final CommonDialog dialog = new CommonDialog(P.mContext,P.thmeid);
                easyDialog = new EasyDialog(dialog);
                P.apply(easyDialog.mCommon);

                dialog.setCancelable(P.mCancelable);
                if (P.mCancelable) {
                    dialog.setCanceledOnTouchOutside(true);
                }
                dialog.setOnCancelListener(P.mOnCancelListener);
                dialog.setOnDismissListener(P.mOnDismissListener);
                if (P.mOnKeyListener != null) {
                    dialog.setOnKeyListener(P.mOnKeyListener);
                }
            }else {
                easyDialog = new EasyDialog();
                P.apply(easyDialog.mCommon);
            }



            return easyDialog;
        }

        public ICommonDialog show() {

            return null;
        }
    }
}
