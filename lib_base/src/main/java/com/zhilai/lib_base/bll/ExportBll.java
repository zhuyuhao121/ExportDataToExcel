package com.zhilai.lib_base.bll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.zhilai.lib_base.BaseBean;
import com.zhilai.lib_base.R;
import com.zhilai.lib_base.excel.EmailBean;
import com.zhilai.lib_base.excel.TestEmail;
import com.zhilai.lib_base.utils.HandlerUtil;
import com.zhilai.lib_base.utils.L;
import com.zhilai.lib_base.utils.RegexUtil;
import com.zhilai.lib_base.utils.SpCache;
import com.zhilai.lib_base.utils.ToastUtil;
import com.zhilai.lib_base.view.PromptDialogView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExportBll implements Handler.Callback {

    private static final String TAG = "ExportBll";

    @SuppressLint("StaticFieldLeak")
    private volatile static ExportBll exportBll;

    private String email;
    private String[] topic = {"序号", "测试员Id", "部件名称", "Mac地址", "箱号", "测试点", "操作时间"
            , "回复时间", "回复数据", "是否成功"};

    private String path;
    private boolean isShowDialogView = false;
    private Context mContext;
    private String fileName;
    private String content;
    private String subject;
    private String bottomName;
    private String emailAccount;
    private String emailPwd;
    private String mailHost;
    private List<BaseBean> baseBeans;
    private String filePath;

    private ExportBll() {

    }

    public static ExportBll getInstance() {
        if (exportBll == null) {
            synchronized (ExportBll.class) {
                if (exportBll == null) {
                    exportBll = new ExportBll();
                }
            }
        }
        return exportBll;
    }

    private String getResString(int resId) {
        return mContext.getResources().getString(resId);
    }

    /**
     * 初始化
     *
     * @param fileName     文件名称
     * @param content      邮件内容
     * @param subject      邮件主题
     * @param bottomName   Excel底部名称
     * @param topic        标题栏
     * @param emailAccount 发件人邮箱账号（最好使用公司账号）
     * @param emailPwd     发件人邮箱密码
     * @param mailHost     发件服务器地址
     */
    public void init(Context mContext, String fileName, String content
            , String subject, String bottomName, String[] topic, String emailAccount
            , String emailPwd, String mailHost) {
        this.mContext = mContext;
        this.fileName = fileName + ".xls";
        this.content = content;
        this.subject = subject;
        this.bottomName = bottomName;
        this.topic = topic;
        this.emailAccount = emailAccount;
        this.emailPwd = emailPwd;
        this.mailHost = mailHost;
        if (mContext != null) {
            SpCache.init(mContext);

            filePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath().concat(File.separator)
                    .concat(mContext.getString(mContext.getApplicationInfo().labelRes));
            path = filePath.concat(File.separator)
                    .concat("xls")
                    .concat(File.separator).concat(this.fileName);
            L.d(TAG, "path===" + path);

//            if (TextUtils.isEmpty(fileName)) {
//                return;
//            }
//            String[] msg = fileName.split("/");
//            L.d(TAG, "msg.length===" + msg.length);
//            StringBuilder sb = new StringBuilder();
//            if (msg.length > 1) {
//                sb.append(filePath.concat(File.separator)
//                        .concat("xls"));
//                for (int i = 0; i < msg.length; i++) {
//                    sb.append("".concat(File.separator)
//                            .concat(msg[i]));
//                }
//                path = sb.toString();
//            } else {
//                path = filePath.concat(File.separator)
//                        .concat("xls").concat(File.separator).concat(fileName);
//            }
//            L.d(TAG, "path===" + path);
        }
    }

    /**
     * 是否显示输入邮箱的弹框
     *
     * @param isShowDialogView
     * @return
     */
    public ExportBll showDialogView(boolean isShowDialogView) {
        this.isShowDialogView = isShowDialogView;
        return this;
    }

    /**
     * 设置收件人邮箱
     *
     * @param email 收件人邮箱
     * @return
     */
    public ExportBll setRecipientsEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     * 导出数据到excel逻辑判断
     *
     * @param isSendEmail 是否发送邮件，true 发送， false 不发送
     * @param baseBeans
     */
    public void excelExportData(boolean isSendEmail, final List<BaseBean> baseBeans) {
        this.baseBeans = baseBeans;
        if (baseBeans == null || baseBeans.size() == 0) {
            HandlerUtil.sendMsg(handler, TOAST_INT_MSG, R.string.no_data_can_be_derived);
            dismissLoadingDialog();
            return;
        }
        if (isSendEmail) {
            if (isShowDialogView) {
                HandlerUtil.sendMsg(handler, SHOW_DIALOG);
            } else {
                exportData(true);
            }
        } else {
            exportData(false);
        }
    }

    private void sendEmail(String path) {
        try {
            EmailBean eBean = new EmailBean();
            eBean.setContent(content);
            eBean.setSubject(subject);
            if (!TextUtils.isEmpty(fileName)) {
                String[] msg = fileName.split("/");
                if (msg.length > 1) {
                    eBean.setFileName(fileName.substring(fileName.lastIndexOf('/') + 1));
                } else {
                    eBean.setFileName(fileName);
                }
            }
            eBean.setAttachment(path);

            if (TextUtils.isEmpty(email)) {
                HandlerUtil.sendMsg(handler, TOAST_INT_MSG, "收件人地址不能为空");
                return;
            }

            String[] address = email.split(";");
            ArrayList<Address> stringArrayList = new ArrayList<>();
            for (String s : address) {
                if (RegexUtil.checkEmail(s)) {
                    stringArrayList.add(new InternetAddress(s));
                } else {
                    HandlerUtil.sendMsg(handler, 1, getResString(R.string.email_format_is_incorrect));
                    return;
                }
            }
//          Address[] addresses = stringArrayList.toArray(new Address[stringArrayList.size()]);
            Address[] addresses = stringArrayList.toArray(new Address[0]);
            TestEmail.sendEmail(handler, emailAccount, emailPwd, mailHost, eBean, addresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断导出数据是否成功
     *
     * @param isSendEmail
     */
    private void exportData(final boolean isSendEmail) {
        new Thread() {
            @Override
            public void run() {
                String rust = exportData(topic, bottomName, baseBeans);
                L.d(TAG, "rust===" + rust);
                if ("create excel success".equals(rust)) {
                    HandlerUtil.sendMsg(handler, EXPORT_DATA_SUCCESS);
                    if (!isSendEmail) {
                        HandlerUtil.sendMsg(handler, DISMISS_LOADING_DIALOG);
                    } else {
                        sendEmail(path);
                    }
                }
            }
        }.start();
    }

    /**
     * 真正实现导出数据的方法
     */
    private synchronized String exportData(String[] topic, String bottomName
            , List<BaseBean> baseBeans) {
        if (null == baseBeans || baseBeans.size() < 1) {
            return "lists is null";
        }

        //需要导出的excel文件的文件名
//        File file = new File(filePath.concat(File.separator).concat("xls"));
        if (TextUtils.isEmpty(path)) {
            return "path is null";
        }
        String pathname = path.substring(0, path.lastIndexOf('/'));
        L.d(TAG, "pathname===" + pathname);
        File file = new File(pathname);

        if (!file.exists()) {
            L.d(TAG, "没有文件。。。创建文件");
            file.mkdirs();
        }
        L.d(TAG, path);
        //操作excel的对象
        WritableWorkbook wwb = null;
        try {
            //根据当前的文件路径创建统计的文件并且实例化出一个操作excel的对象
            wwb = Workbook.createWorkbook(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return "create excel fail";
        }
        //创建底部的选项卡  传参是选项卡的名称  和  选型卡的索引
        WritableSheet writableSheet = wwb.createSheet(bottomName, 0);
        //创建excel的表头的信息
        for (int i = 0; i < topic.length; i++) {
            //横向的在单元格中填写数据
            Label labelC = new Label(i, 0, topic[i]);
            try {
                writableSheet.addCell(labelC);
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
        //从实体中遍历数据并将数据写入excel文件中
        ArrayList<String> li;
        for (int j = 0; j < baseBeans.size(); j++) {
            //将数据源列表中的数据整合成 一个个的字符串列表
            li = new ArrayList<>();
            for (int k = 0; k < topic.length; k++) {
                li.add(baseBeans.get(j).getList().get(k));
            }
            int k = 0;
            for (String l : li) {
                //将单个的字符串列表横向的填入到excel表中
                Label labelC = new Label(k, j + 1, l);
                k++;
                try {
                    writableSheet.addCell(labelC);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            li = null;
        }
        //将文件从内存写入到文件当中
        try {
            wwb.write();
            wwb.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "create excel fail";
        } catch (WriteException e) {
            e.printStackTrace();
            return "create excel fail";
        }
        return "create excel success";
    }

    private Handler handler = new Handler(this);

    private static final int EMAIL_SEND_SUCCESS = 0;
    private static final int EXPORT_DATA_SUCCESS = 1;
    private static final int TOAST_INT_MSG = 2;
    private static final int TOAST_STRING_MSG = 3;
    private static final int SHOW_DIALOG = 4;
    private static final int DISMISS_LOADING_DIALOG = 5;
    private static final int EMAIL_SEND_FAILED = 6;

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case EMAIL_SEND_SUCCESS:
                dismissLoadingDialog();
                SpCache.putString(SpCache.EMAIL, email);
                ToastUtil.setToastMsg(mContext
                        , getResString(R.string.email_sent_successfully));
                break;
            case EXPORT_DATA_SUCCESS:
                ToastUtil.setToastMsg(mContext
                        , getResString(R.string.export_data_successfully) + "\r\n"
                                + getResString(R.string.path_for) + path);
                break;
            case TOAST_INT_MSG:
                int msg = (int) message.obj;
                ToastUtil.setToastMsg(mContext, getResString(msg));
                break;
            case TOAST_STRING_MSG:
                String content = (String) message.obj;
                ToastUtil.setToastMsg(mContext, content);
                break;
            case SHOW_DIALOG:
                showEmailDialog();
                break;
            case DISMISS_LOADING_DIALOG:
                dismissLoadingDialog();
                break;
            case EMAIL_SEND_FAILED:
                ToastUtil.setToastMsg(mContext, (String) message.obj);
                dismissLoadingDialog();
                break;
        }
        return false;
    }

    private PromptDialogView pd = null;

    /**
     * 发送邮件
     */
    private void showEmailDialog() {
        pd = new PromptDialogView(mContext);
        pd.setDialogType(PromptDialogView.POPUPWINDOW_EDITTEXT_TEXT);
        pd.setCancelable(false);
        if (!pd.isShowing())
            pd.show();
        if (pd.getTitle_tv() != null) {
            pd.getTitle_tv().setText(getResString(R.string.please_enter_email));
        }
        pd.setRightButtonClick(new PromptDialogView.OnPopupDialogButtonClick() {
            @Override
            public void onButtonClick(Object arg1, Object arg2) {
                email = String.valueOf(arg1);
                L.d(TAG, "email======" + email);
                if (!TextUtils.isEmpty(email)) {
                    String[] address = email.split(";");
                    for (String s : address) {
                        if (!RegexUtil.checkEmail(s)) {
                            HandlerUtil.sendMsg(handler, TOAST_INT_MSG, R.string.email_format_is_incorrect);
                            return;
                        }
                    }
                    showLoadingDialogToCycle();
                    pd.dismiss();
                    HandlerUtil.sendMsg(handler, TOAST_INT_MSG, R.string.large_data);
                    exportData(true);
                } else {
                    ToastUtil.setToastMsg(mContext
                            , getResString(R.string.name_cannot_be_empty));
                }
            }

            @Override
            public void onButtonClick() {

            }
        });
    }

    private PromptDialogView pdv = null;

    /**
     * loading
     */
    private void showLoadingDialogToCycle() {
        dismissLoadingDialog();
        if (pdv == null) {
            pdv = new PromptDialogView(mContext);
            pdv.setDialogType(PromptDialogView.POPUPWINDOW_LOADING_DIALOG);
            pdv.setCancelable(false);
            pdv.setSetTimeOut(false);
            pdv.show();
        }
    }

    private void dismissLoadingDialog() {
        if (pdv != null) {
            pdv.dismiss();
            pdv = null;
        }
    }
}
