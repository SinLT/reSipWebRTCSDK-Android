package com.reSipWebRTC.service;

public interface RegistrationEventListener {

	void onRegistrationProgress(int acc_id);

	void onRegistrationSuccess(int acc_id);

	void onRegistrationCleared(int acc_id);

	void onRegistrationFailed(int acc_id, int code, String reason);
}
