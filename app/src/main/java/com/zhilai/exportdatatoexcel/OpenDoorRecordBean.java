package com.zhilai.exportdatatoexcel;

import org.litepal.crud.LitePalSupport;
import java.io.Serializable;

public class OpenDoorRecordBean extends LitePalSupport implements Serializable, Cloneable {

    //序号
    private int id;

    //测试员Id
    private String testerId;

    //测试员Id
    private String partName;

    //箱号
    private int boxId;

    //操作时间
    private String operationTime;

    //回复时间
    private String replyTime;

    //回复数据
    private String replyData;

    //行为
    private String behavior;

    //是否成功
    private String resultMsg;

    private String bleAddress;

    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTesterId() {
        return testerId;
    }

    public void setTesterId(String testerId) {
        this.testerId = testerId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyData() {
        return replyData;
    }

    public void setReplyData(String replyData) {
        this.replyData = replyData;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }
}
