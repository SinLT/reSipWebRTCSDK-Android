package com.reSipWebRTC.sdk;

public interface SipRegisterListener {
	    //虚方法回调
		//注册/注销通知
	public void onRegistrationProgress(int acc_id);
	public void onRegistrationSuccess(int acc_id);
	public void onRegistrationFailed(int acc_id, int code, String reason);
	public void onRegistrationCleared(int acc_id);
}
