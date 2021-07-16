package com.reSipWebRTC.service;

public class AccountImpl implements Account {

	private RegistrationManager mRegistrationManager;
    private int accId = -1;
	private RegistrationState mRegistrationState;
	private int code = -1;
	private String reason = null;
	private AccountConfig mAccountConfig;

	public AccountImpl(RegistrationManager registrationManager){
		mRegistrationManager = registrationManager;
	}

	@Override
	public int getAccountId()
	{
		return accId;
	}
	
	@Override
	public void makeRegister(AccountConfig accConfig) {
		this.mAccountConfig = accConfig;
		accId = mRegistrationManager.makeRegister(accConfig);
		mRegistrationManager.registerAccount(this);
	}

    @Override
    public void modifyRegister(int accId, AccountConfig accConfig) {
		this.mAccountConfig = accConfig;
        mRegistrationManager.modifyRegister(accId, accConfig);
        mRegistrationManager.registerAccount(this);
    }

	@Override
	public void makeDeRegister() {
		mRegistrationManager.makeDeRegister(accId);
		mRegistrationManager.unregisterAccount(this);
	}

	@Override
	public void setRegistrationState(RegistrationState registrationState, int code, String reason) {
		this.mRegistrationState = registrationState;
		this.code = code;
		this.reason = reason;
	}

	@Override
	public AccountConfig getAccountConfig()
	{
		return this.mAccountConfig;
	}

	@Override
	public RegistrationState getRegistrationState() {
		return mRegistrationState;
	}

	@Override
	public String getRegistrationReason() {
		return this.reason;
	}

	@Override
	public int getRegistrationCode() {
		return this.code;
	}

	@Override
	public void refreshRegistration() {
		mRegistrationManager.refreshRegistration(this.accId);
	}

	@Override
	public void deRegisterRegistrationEventListener() {
		//DeRegisterRegistrationObserver(nativePtr);
	}

	@Override
	public void setNetworkReachable(boolean yesno) {
		mRegistrationManager.setNetworkReachable(yesno);
	}
	
	@Override
	public boolean accountIsRegistered()
	{
		return mRegistrationState == RegistrationState.Sucess;
	}
}
