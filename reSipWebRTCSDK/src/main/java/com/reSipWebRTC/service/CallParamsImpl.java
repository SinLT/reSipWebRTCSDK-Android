package com.reSipWebRTC.service;


import com.reSipWebRTC.util.Direction;

public class CallParamsImpl implements CallParams {
	private String remoteDisplayName;
	private String remoteUri;
	private String localDisplayName;
	private String localUri;
	private boolean enableAudioCall;
	private boolean enableVideoCall;
	private Direction mDirection;
	private boolean videoCallEnabled;
	private int videoWidth;
	private int videoHeight;
	private int videoFps;
	private String videoCodec;
	private int videoMaxBitrate;
	private String audioCodec;
	private int audioStartBitrate;
	private boolean audioProcessing;
	private int agcControlLevel;
	private int agcControlGain;
	private long startTime;
	private long endTime;
	private int state;
	private int reason;
	private boolean mute;                   //是否静音
	private boolean hf;                     //是否免提
    private int rejectReasonCode;           //拒接code

	public CallParamsImpl() {
		this.enableAudioCall = true;
		this.enableAudioCall = false;
		this.videoWidth = 640;
		this.videoHeight = 480;
		this.videoMaxBitrate = 512;
		this.videoCodec = "VP8";
		this.videoFps = 15;
		this.audioStartBitrate = 30;
		this.audioCodec = "opus";
		this.mute = false;
		this.hf = true;
		this.rejectReasonCode = -1;
		this.audioProcessing = true;
		this.agcControlLevel = 3;
		this.agcControlGain = 9;
	}

	@Override
	public Direction getDirection() {
		return mDirection;
	}

	@Override
	public void setDirection(Direction mDirection) {
       this.mDirection = mDirection;
	}

	@Override
	public String remoteDisplayName() {
		return this.remoteDisplayName;
	}

	@Override
	public void setRemoteDisplayName(String remoteDisplayName) {
        this.remoteDisplayName = remoteDisplayName;
	}

	@Override
	public String remoteUri() {
		return this.remoteUri;
	}

	@Override
	public void setRemoteUri(String remoteUri) {
        this.remoteUri = remoteUri;
	}

	@Override
	public String localDisplayName() {
		return localDisplayName;
	}

	@Override
	public void setLocalDisplayName(String localDisplayName) {
        this.localDisplayName = localDisplayName;
	}

	@Override
	public String localUri() {
		return localUri;
	}

	@Override
	public void setLocalUri(String localUri) {
          this.localUri = localUri;
	}

	@Override
	public void setStartTime(long startTime) {
           this.startTime =  startTime;
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}

	@Override
	public void setEndTime(long endTime) {
          this.endTime = endTime;
	}

	@Override
	public long getEndTime() {
		return this.endTime;
	}

	@Override
	public void setState(int state) {
        this.state = state;
	}

	@Override
	public int getState() {
		return this.state;
	}

	@Override
	public void setReason(int reason) {
        this.reason = reason;
	}

	@Override
	public int getReasion() {
		return this.reason;
	}

	@Override
	public void setRejectReasonCode(int rejectReasonCode) {
		this.rejectReasonCode = rejectReasonCode;
	}

	@Override
	public int getRejectReasonCode() {
		return this.rejectReasonCode;
	}

	@Override
	public void setCallReportStatus(CallReportStatus callReportStatus) {

	}

	@Override
	public CallReportStatus getCallReportStatus()
	{
		return CallReportStatus.fromInt(0);
	}

	@Override
	public boolean audioEnabled() {
		return this.enableAudioCall;
	}

	@Override
	public void enableAudio(boolean audioCall) {
         this.enableAudioCall = audioCall;
	}

	@Override
	public boolean audioProcessing() {
		return this.audioProcessing;
	}

	@Override
	public void enableaudioProcessing(boolean audioProcessing) {
		this.audioProcessing = audioProcessing;
	}

	@Override
	public void setAgcControlLevel(int level) {
           this.agcControlLevel = level;
	}

	@Override
	public int agcControlLevel() {
		return this.agcControlLevel;
	}

	@Override
	public void setAgcControlGain(int gain) {
           this.agcControlGain = gain;
	}

	@Override
	public int agcControlGain() {
		return this.agcControlGain;
	}

	@Override
	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	@Override
	public String audioCodec() {
		return this.audioCodec;
	}

	@Override
	public void setAudioStartBitrate(int audioStartBitrate) {
         this.audioStartBitrate = audioStartBitrate;
	}

	@Override
	public int audioStartBitrate() {
		return this.audioStartBitrate;
	}

	@Override
	public boolean videoEnabled() {
		return this.enableVideoCall;
	}

	@Override
	public void enableVideo(boolean videoCall) {
         this.enableVideoCall = videoCall;
	}

	@Override
	public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
	}

	@Override
	public int videoWidth() {
		return this.videoWidth;
	}

	@Override
	public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
	}

	@Override
	public int videoHeight() {
		return this.videoHeight;
	}

	@Override
	public void setVideoFps(int videoFps) {
        this.videoFps = videoFps;
	}

	@Override
	public int videoFps() {
		return videoFps;
	}

	@Override
	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}

	@Override
	public String videoCodec() {
		return videoCodec;
	}

	@Override
	public void setVideoMaxBitrate(int videoMaxBitrate) {
        this.videoMaxBitrate = videoMaxBitrate;
	}

	@Override
	public int videoMaxBitrate() {
		return videoMaxBitrate;
	}

	@Override
	public void setMute(boolean mute) {
		this.mute = mute;
	}

	@Override
	public boolean isMute() {
		return this.mute;
	}

	@Override
	public void setHf(boolean hf) {
        this.hf = hf;
	}

	@Override
	public boolean isHf() {
		return this.hf;
	}
}
