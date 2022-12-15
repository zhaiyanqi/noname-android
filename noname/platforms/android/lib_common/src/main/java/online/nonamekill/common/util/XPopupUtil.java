package online.nonamekill.common.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.impl.AttachListPopupView;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;

/**
 * XPopup 弹窗工具类
 */
public class XPopupUtil {

    // 获取弹窗实例
    public static XPopup.Builder getXPopup(Context context) {
        return new XPopup.Builder(context);
    }

    // 获取弹窗实例 浅浅封装一下
    public static XPopup.Builder getXPopupBuilder(Activity activity) {
        XPopup.Builder builder = new XPopup.Builder(activity);
        builder.isDestroyOnDismiss(true)        // 设置使用完后就销毁SystemWebViewClient
                .isDarkTheme(false)             // 使用暗色主题
                .hasStatusBar(false)            // 设置是否显示状态栏
                .dismissOnTouchOutside(false)   // 设置点击弹窗外面是否关闭弹窗，默认为true
                .dismissOnBackPressed(false);   // 设置按下返回键是否关闭弹窗，默认为true
        return builder;
    }


    /**
     * 显示选择框
     *
     * @param title
     * @param content
     * @param onSelectListener
     * @return
     */
    public static XPopup.Builder asCenterList(Activity activity, String title, String[] content, OnSelectListener onSelectListener) {
        XPopup.Builder xPopupBuilder = getXPopupBuilder(activity);
        xPopupBuilder.maxHeight(600)
                .asCenterList(title, content, onSelectListener)
                .show();
        return xPopupBuilder;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------//

    /**
     * 有确认和取消的按钮
     *
     * @param title
     * @param content
     * @param confirmListener
     * @param cancelListener
     * @return
     */
    public static XPopup.Builder asConfirm(Activity activity, CharSequence title, CharSequence content, OnConfirmListener confirmListener, OnCancelListener cancelListener) {
        return asConfirm(activity, title, content, null, null, confirmListener, cancelListener, false);
    }

    /**
     * 只有确认按钮的框
     *
     * @param title
     * @param content
     * @param confirmListener
     * @return
     */
    public static XPopup.Builder asConfirm(Activity activity, CharSequence title, CharSequence content, OnConfirmListener confirmListener, boolean isHideCancel) {
        return asConfirm(activity, title, content, null, null, confirmListener, null, isHideCancel);
    }

    public static XPopup.Builder asConfirm(Activity activity, CharSequence title, CharSequence content, OnConfirmListener confirmListener) {
        return asConfirm(activity, title, content, null, null, confirmListener, null, false);
    }

    public static XPopup.Builder asConfirm(Activity activity, CharSequence title, CharSequence content, boolean isHideCancel, OnConfirmListener confirmListener) {
        return asConfirm(activity, title, content, null, null, confirmListener, null, isHideCancel);
    }

    public static XPopup.Builder asConfirm(Activity activity, CharSequence title, CharSequence content, CharSequence confirmBtnText,
                                           CharSequence cancelBtnText, OnConfirmListener confirmListener,
                                           OnCancelListener cancelListener, boolean isHideCancel) {
        XPopup.Builder xPopupBuilder = getXPopupBuilder(activity);
        xPopupBuilder
                .autoFocusEditText(false)
                .autoOpenSoftInput(false)
                .asConfirm(title, content, confirmBtnText, cancelBtnText, confirmListener, cancelListener, isHideCancel, 0)
                .show();
        return xPopupBuilder;
    }


    /**
     * 隐藏掉取消框
     *
     * @param title
     * @param content
     * @param inputContent
     * @param hint
     * @param confirmListener
     * @return
     */
    public static XPopup.Builder asInput2Confirm(Activity activity, CharSequence title, CharSequence content, CharSequence inputContent, CharSequence hint, OnInputConfirmListener confirmListener) {
        XPopup.Builder xPopupBuilder = getXPopupBuilder(activity);
        InputConfirmPopupView inputConfirm = xPopupBuilder
                .isViewMode(true)
                .popupAnimation(PopupAnimation.TranslateAlphaFromLeft)
                .asInputConfirm(title, content, inputContent, hint, confirmListener);
        inputConfirm.isHideCancel = true;
        inputConfirm.setConfirmText("确定");
        inputConfirm.setCancelText("取消");
        inputConfirm.show();
        return xPopupBuilder;
    }

    public static XPopup.Builder asInput2Confirm(Activity activity, CharSequence title, OnInputConfirmListener confirmListener) {
        return asInput2Confirm(activity, title, null, null, null, confirmListener);
    }

    public static XPopup.Builder asInput2Confirm(Activity activity, CharSequence title, CharSequence inputContent, OnInputConfirmListener confirmListener) {
        return asInput2Confirm(activity, title, null, inputContent, null, confirmListener);
    }

    public static XPopup.Builder asInput2Confirm(Activity activity, CharSequence title, CharSequence content, CharSequence inputContent, OnInputConfirmListener confirmListener) {
        return asInput2Confirm(activity, title, content, inputContent, null, confirmListener);
    }

    /**
     * 弹出选择框
     *
     * @return
     */
    public static AttachListPopupView asAttachList(Activity activity, String[] data, View view, OnSelectListener selectListener) {
        return XPopupUtil.getXPopupBuilder(activity)
                .dismissOnTouchOutside(true)
                .dismissOnBackPressed(true)
                .hasStatusBar(false)
                .animationDuration(120)
                .hasShadowBg(false)
                .isViewMode(true)
                .atView(view)
                .asAttachList(data, null, selectListener);
    }


    public static BasePopupView loading(Activity activity) {
        return loading(null);
    }

    public static BasePopupView loading(Activity activity, String title) {
        return getXPopupBuilder(activity).asLoading(title).show();
    }

    public static XPopup.Builder asCustom(Activity activity, BasePopupView popupView) {
        XPopup.Builder builder = XPopupUtil.getXPopupBuilder(activity);
        builder.dismissOnTouchOutside(true)
                .dismissOnBackPressed(true);
        builder.asCustom(popupView).show();
        return builder;
    }
}
