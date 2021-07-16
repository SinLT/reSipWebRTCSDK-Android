package com.reSipWebRTC.service;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.reSipWebRTC.sipengine.RTCRegistrationManager;
import com.reSipWebRTC.sipengine.RTCRegistrationStateObserver;

public class RegistrationManagerImpl implements RegistrationManager, RTCRegistrationStateObserver {
	//implement for RegistrationManager_JNI.cpp
	private RTCRegistrationManager nativeRegistrationManager = null;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Account> mAccountMap = new HashMap<Integer, Account>();

    private RegistrationEventListener mRegistrationEventListener;

    public Account createAccount()
	{
	   return new AccountImpl(this);
	}

	public RegistrationManagerImpl(RTCRegistrationManager registrationManager)
	{
        nativeRegistrationManager = registrationManager;
        nativeRegistrationManager.registerRegistrationObserver(this);
	}
	
	@Override
	public int makeRegister(AccountConfig accConfig) {
		return nativeRegistrationManager.makeRegister(accConfig.username,
                accConfig.password, accConfig.server);
	}

	@Override
	public void modifyRegister(int accId, AccountConfig accConfig) {
        nativeRegistrationManager.modifyRegister(accId,
                accConfig.username, accConfig.password, accConfig.server);
	}

	@Override
	public void makeDeRegister(int accId) {
        nativeRegistrationManager.makeDeRegister(accId);
	}

	@Override
	public void refreshRegistration(int accId) {
        nativeRegistrationManager.refreshRegistration(accId);
	}

	@Override
	public Account getAccountByAccId(int accId) {
		synchronized(mAccountMap) {
			if(!mAccountMap.isEmpty()) {
				Account mAccount = mAccountMap.get(accId);
				if(mAccount != null)
					return mAccount;
				else return null;
			} else return null;
		}
	}

	@Override
	public void setNetworkReachable(boolean yesno) {
        nativeRegistrationManager.setNetworkReachable(yesno);
	}
	
	@Override
	public boolean profileIsRegistered()
	{
		return true;
	}
	
	
	public void registerAccount(Account account)
	{
        synchronized(mAccountMap) {
			mAccountMap.put(account.getAccountId(), account);
        }
	}
	
	public void unregisterAccount(Account account)
	{
        synchronized(mAccountMap) {
			if(!mAccountMap.isEmpty()) {
				if(account != null)
					mAccountMap.remove(account.getAccountId());
			}
        }
	}
	
	@Override
	public void OnRegistrationProgress(int acc_id) {
		// TODO Auto-generated method stub

		if(!mAccountMap.isEmpty()) {
			Account account = mAccountMap.get(acc_id);
			if(account != null)
			   account.setRegistrationState(RegistrationState.Progress, 0, "Progress");
		}

        if(mRegistrationEventListener != null)
            mRegistrationEventListener.onRegistrationProgress(acc_id);
	}
	
	@Override
	public void OnRegistrationSuccess(int acc_id) {
		// TODO Auto-generated method stub
		synchronized(mAccountMap) {
		   if(!mAccountMap.isEmpty()) {
			Account account = mAccountMap.get(acc_id);
			if(account != null)
			   account.setRegistrationState(RegistrationState.Sucess, 1, "Sucess");
		  }

          if(mRegistrationEventListener != null) {
			System.out.println("OnRegistrationSuccess");
			mRegistrationEventListener.onRegistrationSuccess(acc_id);
          } else {
		  }
		}
	}
	
	@Override
	public void OnRegistrationCleared(int acc_id) {
		// TODO Auto-generated method stub
		synchronized(mAccountMap) {
			if(!mAccountMap.isEmpty()) {
			  Account account = mAccountMap.get(acc_id);
			  if(account != null)
			    account.setRegistrationState(RegistrationState.Cleared, 2, "Cleard");
		    }

           if(mRegistrationEventListener != null)
              mRegistrationEventListener.onRegistrationCleared(acc_id);
		}
	}
	@Override
	public void OnRegistrationFailed(int acc_id, int code, String reason) {
		// TODO Auto-generated method stub
		synchronized(mAccountMap) {
		    if(!mAccountMap.isEmpty()) {
			  Account account = mAccountMap.get(acc_id);
			  if(account != null)
			   account.setRegistrationState(RegistrationState.Failed, code, reason);
		    }

            if(mRegistrationEventListener != null)
              mRegistrationEventListener.onRegistrationFailed(acc_id, 3, reason);
		}
	}

    @Override
    public void registerRegistrationEventListener(RegistrationEventListener observer) {
        mRegistrationEventListener = observer;
    }
}
