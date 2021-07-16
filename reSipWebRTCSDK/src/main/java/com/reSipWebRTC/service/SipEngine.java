package com.reSipWebRTC.service;

public interface SipEngine {
		
    public boolean SipEngineInitialize();

    public boolean dispose();

   // public boolean RunEventLoop();

   // public SipProfileManager GetSipProfileManager();

    public CallManager GetCallManager();

    public RegistrationManager GetRegistrationManager();

    public TransportInfo GetTransportInfo();

   // public MediaEngine GetMediaEngine();
    
    //public Config GetDefaultConfig();
}
