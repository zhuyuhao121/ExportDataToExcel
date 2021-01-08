package com.zhilai.lib_base.excel;

import android.os.Handler;
import android.text.TextUtils;

import com.zhilai.lib_base.utils.HandlerUtil;
import com.zhilai.lib_base.utils.L;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class TestEmail {

    private static final String TAG = "TestEmail";
//    private static final String USERNAME = "zhuyuhao@chinawebox.com";
//    private static final String PWD = "zhilai.com";

    private static Properties getProperties(String mailHost) {
        Properties props = new Properties();
        props.put("mail.debug", "true"); // 开启debug调试
        props.put("mail.smtp.auth", "true");
        props.put("mail.transport.protocol", "smtp");
        if (TextUtils.isEmpty(mailHost)) {
            mailHost = "smtp.ym.163.com";
        }
        props.put("mail.host", mailHost);
        return props;
    }

    /**
     * 从资源文件获取值
     *
     * @param bundle 如emailpractice
     * @param key    　如password
     * @return
     */
    private static String getResourceValue(String bundle, String key) {
        try {
            return ResourceBundle.getBundle(bundle).getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 带附件的邮件发送 注意《的转义 和上传文件的过滤
//     *
//     * @param eBean
//     * @return
//     * @throws Exception
//     */
//    public static boolean sendEmail2(EmailBean eBean) throws Exception {
//        if (null == eBean) {
//            return false;
//        }
//        Session session = Session.getInstance(getProperties(), new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(USERNAME, PWD);
//            }
//        });
//        session.setDebug(true);
//        Message message = new MimeMessage(session);
//        message.setSubject(eBean.getSubject());
//        message.setFrom(new InternetAddress("\"" + MimeUtility.encodeText("zhuyuhao") + "\"<" + USERNAME + ">"));
//        message.setRecipients(MimeMessage.RecipientType.TO, new Address[]{new InternetAddress(eBean.getToEmail())});
//
//        MimeBodyPart attch1 = null;
//        if (null != eBean.getAttachment() && !"".equals(eBean.getAttachment().trim())) {
//            // 设置附件1
//            File file1 = null;
//            try {
//                file1 = new File(eBean.getAttachment());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (null != file1 && file1.isFile()) {
//                DataSource ds1 = new FileDataSource(file1);
//                DataHandler dh1 = new DataHandler(ds1);
//
//                attch1 = new MimeBodyPart(); // 附件1
////                multipart.addBodyPart(attch1);
//                attch1.setDataHandler(dh1);
//                attch1.setFileName("record.xls");
////                attch1.setFileName("测试.xls");
//            }
//        }
//
//        // 邮件正文
//        MimeMultipart multipart = new MimeMultipart("mixed");
//        message.setContent(multipart);
//
//        //创建邮件的内容 包括一个邮件正文和一个附件
//        MimeBodyPart content = new MimeBodyPart(); // 邮件内容
//        // 将邮件内容添加到multipart中
//        multipart.addBodyPart(content);
//        if (null != attch1) {
//            multipart.addBodyPart(attch1);
//        }
//
//        //设置内容（正文）---是一个复杂体 包括HTML正文
//        MimeMultipart bodyMultipart = new MimeMultipart("related");
//        content.setContent(bodyMultipart);
//        //构造正文
//        MimeBodyPart htmlBody = new MimeBodyPart();
//        bodyMultipart.addBodyPart(htmlBody);
//        //设置HTML正文
//        htmlBody.setContent(eBean.getContent(), "text/html;charset=UTF-8");
//        message.saveChanges(); // 生成邮件
//        Transport.send(message);
//        return true;
//    }

    public static void sendEmail(final Handler handler, final String emailAccount
            , final String emailPwd, String mailHost
            , EmailBean eBean, Address[] addresses) {
        Session session = Session.getInstance(getProperties(mailHost), new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, emailPwd);
            }
        });
        // 设置为DEBUG模式
        session.setDebug(true);

        // 邮件内容对象组装
        MimeMessage message = new MimeMessage(session);
        try {
            message.setSubject(eBean.getSubject());
            String username = emailAccount.split("@")[0];
            message.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(username) + "\"<" + emailAccount + ">"));
//            message.setRecipients(MimeMessage.RecipientType.TO
//                    , new Address[]{new InternetAddress(eBean.getToEmail())});
            message.setRecipients(MimeMessage.RecipientType.TO
                    , addresses);

            // 邮件文本/HTML内容
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            L.d(TAG, "eBean.getContent()===" + eBean.getContent());
            messageBodyPart.setContent(eBean.getContent(), "text/html;charset=UTF-8");
            multipart.addBodyPart(messageBodyPart);

            // 添加邮件附件
//            if (images != null && images.length > 0) {
//                for (String filePath : images) {
//                    MimeBodyPart attachPart = new MimeBodyPart();
//                    DataSource source = new FileDataSource(filePath);
//                    attachPart.setDataHandler(new DataHandler(source));
//                    attachPart.setFileName(filePath);
//                    multipart.addBodyPart(attachPart);
//                }
//            }
            MimeBodyPart attachPart = new MimeBodyPart();
            DataSource source = new FileDataSource(eBean.getAttachment());
            attachPart.setDataHandler(new DataHandler(source));
            L.d(TAG, "eBean.getFileName()===" + eBean.getFileName());
            attachPart.setFileName(eBean.getFileName());
//            attachPart.setFileName("record.xls");
            multipart.addBodyPart(attachPart);

            // 保存邮件内容
            message.setContent(multipart);

//            // 获取SMTP协议客户端对象，连接到指定SMPT服务器
//            Transport transport = session.getTransport("smtp");
//            transport.connect(host, Integer.parseInt(port), userName, password);
//            L.d(TAG,"connet it success!!!!");
//
//            // 发送邮件到SMTP服务器
//            Thread.currentThread().setContextClassLoader(getClass().getClassLoader() );
            Transport.send(message);
            L.d(TAG, "send it success!!!!");

//            // 关闭连接
//            transport.close();
            HandlerUtil.sendMsg(handler, 0);
        } catch (Exception e) {
            e.printStackTrace();
            L.d(TAG, "e.getMessage()===" + e.getMessage());
            HandlerUtil.sendMsg(handler, 6, "发送失败");
        }
    }
}