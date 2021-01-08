package com.zhilai.lib_base.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhilai.lib_base.R;
import com.zhilai.lib_base.utils.SpCache;

public class PromptDialogView extends Dialog implements Handler.Callback{
    private static final String TAG = "PromptDialogView";

    public static final int POPUPWINDOW_TEXT = 0;
    public static final int POPUPWINDOW_LOADING_DIALOG = 1;
    public static final int POPUPWINDOW_UPDATE = 2;
    public static final int POPUPWINDOW_EDITTEXT = 3;
    public static final int POPUPWINDOW_EDITTEXT_TEXT = 4;

    private int dialogType = -1;
    private LinearLayout showLinearLayout = null;
    private LinearLayout allLl;
    private String content;
    public TextView cancelTv;
    public TextView confirmTv;
    public EditText content_et;
    public TextView title_tv;
    private boolean isSetTimeOut = true;
    private AVLoadingIndicatorView avi;

    public void setSetTimeOut(boolean setTimeOut) {
        isSetTimeOut = setTimeOut;
    }

    public Handler handler = new Handler(this);

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public PromptDialogView(Context context) {
        super(context, R.style.dialog);
        this.setCanceledOnTouchOutside(false);
    }

    public String getContent() {
        return content;
    }

    public TextView getTitle_tv() {
        return title_tv;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private OnPopupDialogButtonClick rightButtonClick = null;

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case 0:
                if(avi != null){
                    avi.hide();
                }
                dismiss();
                break;
        }
        return false;
    }

    public interface OnPopupDialogButtonClick {
        void onButtonClick(Object arg1, Object arg2);

        void onButtonClick();
    }

    public void setRightButtonClick(OnPopupDialogButtonClick rightButtonClick) {
        this.rightButtonClick = rightButtonClick;
    }

    public OnPopupDialogButtonClick getRightButtonClick() {
        return rightButtonClick;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.email_dialog_layout);

        allLl = findViewById(R.id.all_ll);

        switch (dialogType) {
            case POPUPWINDOW_EDITTEXT_TEXT:
                showLinearLayout = findViewById(R.id.ll_popupwindow_email_et);
                showLinearLayout.setVisibility(View.VISIBLE);
                confirmTv = findViewById(R.id.email_confirm_tv);
                cancelTv = findViewById(R.id.email_cancel_tv);
                title_tv = findViewById(R.id.email_title_tv);
                content_et = findViewById(R.id.email_content_et);
                if(!TextUtils.isEmpty(SpCache.getEmail())){
                    content_et.setText(SpCache.getEmail());
                    content_et.setSelection(SpCache.getEmail().length());
                }
                confirmTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(getRightButtonClick() != null){
                            getRightButtonClick()
                                    .onButtonClick(content_et.getText()
                                            .toString().trim(), content_et);
                        }
                    }
                });
                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case POPUPWINDOW_LOADING_DIALOG:
                allLl.setVisibility(View.GONE);
                avi = findViewById(R.id.avi);
                avi.setVisibility(View.VISIBLE);

                if(isSetTimeOut){
                    Message msg = Message.obtain();
                    msg.what = 0;
                    handler.sendMessageDelayed(msg, 5000);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        Window window = this.getWindow();
        //弹出系统软键盘
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }
}
