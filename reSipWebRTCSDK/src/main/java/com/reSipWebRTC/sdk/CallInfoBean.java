package com.reSipWebRTC.sdk;

public class CallInfoBean {
    private String localUsername;           //本机注册的号码
    private String localDisplayName;        //本机显示名
    private String remoteUsername;          //对方注册的号码
    private String remoteDisplayName;       //对方显示名
    private int type;                       //通话类型，1来电 还是 0去电
    private boolean video;                  //是否开启视频画面
    private boolean mute;                   //是否静音
    private boolean hf;                     //是否免提

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

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isHf() {
        return hf;
    }

    public void setHf(boolean hf) {
        this.hf = hf;
    }


}
