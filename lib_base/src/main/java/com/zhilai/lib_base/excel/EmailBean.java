package com.zhilai.lib_base.excel;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

public class EmailBean {

    private String subject;//标题
    private String content;//内容
    private String attachment;//附件
    private String toEmail;//收件人邮箱
    private String fileName;//附件名称

    public String getToEmail() {
        return null == toEmail ? "534837240@qq.com" : toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getFileName() {
        try {
            return MimeUtility.encodeText(fileName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
