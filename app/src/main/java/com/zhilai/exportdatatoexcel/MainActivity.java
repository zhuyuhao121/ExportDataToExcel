package com.zhilai.exportdatatoexcel;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.zhilai.exportdatatoexcel.permission.Acp;
import com.zhilai.exportdatatoexcel.permission.AcpListener;
import com.zhilai.exportdatatoexcel.permission.AcpOptions;
import com.zhilai.lib_base.BaseBean;
import com.zhilai.lib_base.bll.ExportBll;
import com.zhilai.lib_base.utils.DataUtils;
import com.zhilai.lib_base.utils.L;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button add_bt;
    private Button excel_bt;
    private Button excel_email_bt;
    private List<OpenDoorRecordBean> allLists;
    private String[] topic = {"序号", "测试员Id", "部件名称", "Mac地址", "箱号", "测试点", "操作时间"
            , "回复时间", "回复数据", "是否成功"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions();

        add_bt = findViewById(R.id.add_bt);
        excel_bt = findViewById(R.id.excel_bt);
        excel_email_bt = findViewById(R.id.excel_email_bt);
        add_bt.setOnClickListener(this);
        excel_bt.setOnClickListener(this);
        excel_email_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.excel_bt) {
            exportData(false);
        } else if (i == R.id.excel_email_bt) {
            exportData(true);
        } else if (i == R.id.add_bt) {
            addOpenDoorRecord();
        }
    }

    public void verifyStoragePermissions() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION)
                        /*以下为自定义提示语、按钮文字
                        .setDeniedMessage()
                        .setDeniedCloseBtn()
                        .setDeniedSettingBtn()
                        .setRationalMessage()
                        .setRationalBtn()*/
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        L.d(TAG, "已申请到权限");
                        // 初始化
                        LitePal.initialize(MainActivity.this);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Log.d(TAG, "权限已被拒绝");
                    }
                });
    }

    private void exportData(final boolean isSendEmail) {
        LitePal.where("type = ?", "1")
                .findAsync(OpenDoorRecordBean.class)
                .listen(new FindMultiCallback<OpenDoorRecordBean>() {
                    @Override
                    public void onFinish(List<OpenDoorRecordBean> list) {
                        allLists = list;
                        if (allLists != null) {
                            L.d(TAG, "allLists.size()===" + allLists.size());
                            init(isSendEmail);
                        }
                    }
                });
    }

    private static final String pathName = "zhilai_ceshi"; //存储路径名称
    private static final String fileName = "record"; //文件名称，由于中文名称会乱码，所以最好不要命名为中文
    private static final String content = "请输入邮件内容"; //邮件内容
    private static final String subject = "请输入邮件主题"; //邮件主题
    private static final String bottomName = "Excel底部名称"; //Excel底部名称
    private static final String emailAccount = "xxx@chinawebox.com"; //发件人邮箱账号（最好使用公司账号）
    private static final String emailPwd = "xxxxxx"; //发件人邮箱密码
    private static final String mailHost = "smtp.ym.163.com"; //发件服务器地址

    /**
     * 初始化
     *
     * @param isSendEmail 是否发送邮件，true 发送邮件， false 不发送，只把数据保存在本地
     *                    邮箱支持批量添加，每个邮箱之间用英文分号";"相隔
     *                    比如：534837240@qq.com;joyce@163.com
     */
    private void init(boolean isSendEmail) {
        ExportBll.getInstance().init(MainActivity.this
                , fileName + DataUtils.DateStr(), content, subject
                , bottomName, topic, emailAccount, emailPwd, mailHost);
        if (isSendEmail) {
            ExportBll.getInstance()
                    .showDialogView(true)//是否显示自带的dialog，显示后提示用户输入收件人邮箱
                    .excelExportData(true, getBaseLists());
        } else {
            ExportBll.getInstance()
                    .setRecipientsEmail("534837240@qq.com")//设置收件人邮箱
                    .excelExportData(false, getBaseLists());
        }
    }

    /**
     * 将需要导出的数据按照以下格式转换成字符串
     * @param allLists： 数据库数据
     * @return
     */
    private List<BaseBean> getBaseLists() {
        List<BaseBean> baseBeans = new ArrayList<>();
        for (int i = 0; i < allLists.size(); i++) {
            OpenDoorRecordBean bean = allLists.get(i);
            BaseBean baseBean = new BaseBean();
            List<String> strings = new ArrayList<>();
            strings.add(bean.getId() + "");
            strings.add(bean.getTesterId());
            strings.add(bean.getPartName());
            strings.add(bean.getBleAddress());
            strings.add(bean.getBoxId() + "");
            strings.add(bean.getBehavior());
            strings.add(bean.getOperationTime());
            strings.add(bean.getReplyTime());
            strings.add(bean.getReplyData());
            strings.add(bean.getResultMsg());
            baseBean.setList(strings);
            baseBeans.add(baseBean);
        }
        return baseBeans;
    }

    String partName = "";

    /**
     * 模拟数据
     */
    private void addOpenDoorRecord() {
        partName = "测试";
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    L.d(TAG, "i===" + i);
                    OpenDoorRecordBean openDoorRecordBean = new OpenDoorRecordBean();
                    openDoorRecordBean.setTesterId("zyh");
                    openDoorRecordBean.setPartName(partName);
                    openDoorRecordBean.setBleAddress("11:22:33:44:55:66");
                    openDoorRecordBean.setBoxId(i + 1);
                    openDoorRecordBean.setBehavior("开门检测");
                    openDoorRecordBean.setType("1");
                    openDoorRecordBean.setOperationTime(DataUtils.getSSSToString());
                    openDoorRecordBean.setReplyData(i + "");
                    openDoorRecordBean.setResultMsg("成功");
                    openDoorRecordBean.setReplyTime(DataUtils.getSSSToString());
                    openDoorRecordBean.save();
                }
            }
        }.start();
    }
}
