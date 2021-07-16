package com.reSipWebRTC.sdk;

public interface ReSipRtcEvent extends SipRegisterListener,
                  SipIncomingCallListener,
                  SipOutgoingCallListener,
                  SipCallConnectedListener,
                  SipCallEndListener,
                  CallStreamListener {
}
