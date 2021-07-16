package com.reSipWebRTC.service;

public interface Account {
	void makeRegister(AccountConfig accConfig);
	void modifyRegister(int accId, AccountConfig accConfig);
	void makeDeRegister();
	void setRegistrationState(RegistrationState registrationState, int code, String reason);
	RegistrationState getRegistrationState();
	String getRegistrationReason();
	int getRegistrationCode();
	AccountConfig getAccountConfig();
	void refreshRegistration();
	void deRegisterRegistrationEventListener();
	void setNetworkReachable(boolean yesno);
	boolean accountIsRegistered();
	int getAccountId();
}
