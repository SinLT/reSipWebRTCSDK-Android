package com.reSipWebRTC.sdk;

public class CallLogBean {
    private String localUsername;           //本机注册的号码
    private String localDisplayName;        //本机显示名
    private String remoteUsername;          //对方注册的号码
    private String remoteDisplayName;       //对方显示名
    private int type;                       //通话类型，1来电 还是 0去电
    private int status;                     //状态 无应答 用户忙 拒接 ，正常
    private int reasonCode;                  //原因code
    private int rejectReasonCode;            //拒绝拨号原因code
    private String reason;                  //原因
    private String startTime;               //开始时间
    private String endTime;                 //结束时间
    private boolean isVideoCall;

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    private boolean isReply;                //是否已经回复

    public String getLocalUsername() {
        return localUsername;
    }

    public void setLocalUsername(String localUsername) {
        this.localUsername = localUsername;
    }

    public String getLocalDisplayName() {
        return localDisplayName;
    }

    public void setLocalDisplayName(String localDisplayName) {
        this.localDisplayName = localDisplayName;
    }

    public String getRemoteUsername() {
        return remoteUsername;
    }

    public void setRemoteUsername(String remoteUsername) {
        this.remoteUsername = remoteUsername;
    }

    public String getRemoteDisplayName() {
        return remoteDisplayName;
    }

    public void setRemoteDisplayName(String remoteDisplayName) {
        this.remoteDisplayName = remoteDisplayName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRejectReasonCode() {
        return rejectReasonCode;
    }

    public void setRejectReasonCode(int rejectReasonCode) {
        this.rejectReasonCode = rejectReasonCode;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setVideoCall(boolean isVideoCall) {
        this.isVideoCall = isVideoCall;
    }
    public boolean isVideoCall(){
        return this.isVideoCall;
    }
}
