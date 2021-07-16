package com.reSipWebRTC.service;

public interface RegistrationManager {
     Account createAccount();
     int makeRegister(AccountConfig accConfig);
     void modifyRegister(int accId, AccountConfig accConfig);
     void makeDeRegister(int accId);
     void refreshRegistration(int accId);
     Account getAccountByAccId(int accId);
     void setNetworkReachable(boolean yesno);
     boolean profileIsRegistered();
     void registerAccount(Account account);
	 void unregisterAccount(Account account);
     void registerRegistrationEventListener(RegistrationEventListener observer);
}