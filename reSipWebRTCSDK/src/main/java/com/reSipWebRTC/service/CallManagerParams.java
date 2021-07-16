package com.reSipWebRTC.service;

/**
 * Created by xuban on 2017/6/28.
 */

public class CallManagerParams {
    public final boolean videoCallEnabled;
    public final boolean tracing;
    public final boolean videoCodecHwAcceleration;
    public final boolean useOpenSLES;
    public final boolean disableBuiltInAEC;
    public final boolean disableBuiltInAGC;
    public final boolean disableBuiltInNS;
    public final boolean audioProcessing;
    public final int agcControlLevel;
    public final int agcControlGain;

    public CallManagerParams(boolean videoCallEnabled, boolean tracing, boolean videoCodecHwAcceleration, boolean useOpenSLES,
                                    boolean disableBuiltInAEC, boolean disableBuiltInAGC, boolean disableBuiltInNS,
                                    boolean audioProcessing, int agcControlLevel, int agcControlGain) {
        this.videoCallEnabled = videoCallEnabled;
        this.tracing = tracing;
        this.videoCodecHwAcceleration = videoCodecHwAcceleration;
        this.useOpenSLES = useOpenSLES;
        this.disableBuiltInAEC = disableBuiltInAEC;
        this.disableBuiltInAGC = disableBuiltInAGC;
        this.disableBuiltInNS = disableBuiltInNS;
        this.audioProcessing = audioProcessing;
        this.agcControlLevel = agcControlLevel;
        this.agcControlGain = agcControlGain;
    }
}
