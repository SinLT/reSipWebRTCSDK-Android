package com.reSipWebRTC.sipengine;

public interface RTCCallStateObserver {
    @CalledByNative void OnIncomingCall(final int accId, final int callId,
                                        final String callerDisplayName, final String callerUri);
    @CalledByNative void OnCallStateChange(final int callId, final int stateCode, final int reasonCode);
    @CalledByNative void OnCallOffer(final int callId, final String offerSdp,
                                     final boolean audioCall, final boolean videoCall);
    @CalledByNative void OnCallReceiveReinvite(final int callId, final String sdp);
    @CalledByNative void OnCallAnswer(final int callId, final String answerSdp,
                                      final boolean audioCall, final boolean videoCall);
    @CalledByNative void OnInfoEvent(final int callId, final String info);
    @CalledByNative void OnMediaStateChange(final int callId, final String remoteSdp, final boolean audio, final boolean video);
}
