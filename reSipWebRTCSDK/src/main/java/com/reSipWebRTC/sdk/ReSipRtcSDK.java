package com.reSipWebRTC.sdk;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.reSipWebRTC.service.AccountConfig;
import com.reSipWebRTC.service.CallParams;
import com.reSipWebRTC.service.CallParamsImpl;
import com.reSipWebRTC.service.PhoneService;
import com.reSipWebRTC.util.Direction;

import java.text.SimpleDateFormat;

public class ReSipRtcSDK {

    //启动WebRtcService  必须
    public static void init(Context context) {
        if (!PhoneService.isready()) {
            PhoneService.instance().initSDK(context);
        }
    }

    //停止WebRtcService
    public static void uninit(Context context) {
        if (PhoneService.isready()) {
            PhoneService.instance().uninitSDK();
        }
    }

    public static void setReSipRtcEvent(ReSipRtcEvent reSipRtcEvent) {
        if(PhoneService.isready()) {
            PhoneService.instance().setSipRegisterListener(reSipRtcEvent);
            PhoneService.instance().setSipIncomingListener(reSipRtcEvent);
            PhoneService.instance().setSipOutgoingListener(reSipRtcEvent);
            PhoneService.instance().setSipCallConnectedListener(reSipRtcEvent);
            PhoneService.instance().setSipCallEndListener(reSipRtcEvent);
            PhoneService.instance().setCallStreamListener(reSipRtcEvent);
        }
    }

    //是否注册
    public static boolean isRegister(int accountId) {
        return PhoneService.instance().isRegistered(accountId);
    }

    //注册
    public static void register(String serverIp, String username, String password, String displayname) {
        Log.d(ReSipRtcSDK.class.getSimpleName(), "register IP:" + serverIp + "--password:" + password+"--username:"+username);

        AccountConfig accountConfig = new AccountConfig();
        accountConfig.username = username;
        accountConfig.password = password;
        accountConfig.server = serverIp;
        accountConfig.trans_type = "tcp";
        accountConfig.displayname = displayname;

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(serverIp)) {
            PhoneService.instance().registerSipAccount(accountConfig);
        } else {
            Log.d(ReSipRtcSDK.class.getSimpleName(), "有参数为空");
        }
    }

    //注销
    public static void unregister(int accountId) {
        PhoneService.instance().unRegisterSipAccount(accountId);
    }

    public static RegisterInfo getRegisterInfo(int accId) {
         RegisterInfo mRegisterInfo = new RegisterInfo();
         AccountConfig mAccountConfig = PhoneService.instance().getRegistrationInfo(accId);
         mRegisterInfo.displayname = mAccountConfig.username;
         mRegisterInfo.username = mAccountConfig.username;
         mRegisterInfo.password = mAccountConfig.password;
         return mRegisterInfo;
    }

    public static RegisterStatus getRegisterStatus(int accId) {
        RegisterStatus mRegisterStatus = new RegisterStatus();
        mRegisterStatus.state = PhoneService.instance().getAccountRegistrationState(accId);
        mRegisterStatus.code = PhoneService.instance().getAccountRegistrationCode(accId);
        mRegisterStatus.reason = PhoneService.instance().getAccountRegistrationReason(accId);
        return mRegisterStatus;
    }

    //是否正在通话
    public static boolean isCallActive(int callId) {
        return PhoneService.instance().isCallActive(callId);
    }

    public static boolean videoEnableForLocal = false;

    //拨号决定不开视频画面
    //audioProcessing 是否开启声音处理
    //agcControlLevel 值越小，声音越大,取值范围[0,30].默认取值为3
    //agcControlGain 值越大,声音越大,取值范围[0, 90],默认取值为9
    public static int call(String callee, boolean isVideo, boolean audioProcessing,
                           int agcControlLevel, int agcControlGain) {
        ReSipRtcSDK.videoEnableForLocal = isVideo;
        CallParams callParams = new CallParamsImpl();
        callParams.enableVideo(isVideo);
        callParams.setAudioCodec("PCMA");
        callParams.enableaudioProcessing(audioProcessing);
        callParams.setAgcControlLevel(agcControlLevel);
        callParams.setAgcControlGain(agcControlGain);
        return PhoneService.instance().call(callee, callParams);
    }

    //拨打号码，再主动挂断
    public static void hangup(int callId) {
        PhoneService.instance().hangupCall(callId);
    }

    //拒听来电 reasonCode 拒绝原因code
    public static void reject(int callId, int reasonCode) {
        PhoneService.instance().rejectCall(callId, reasonCode);
    }

    //挂断所有通话
    public static void hangupAll() {
        PhoneService.instance().hangupAllCall();
    }

    //接听电话
    public static void answer(int callId, boolean isVideo) {
        if (callId <= 0) {
            return;
        }
        //PhoneService.instance().getCallParams(callId).enableVideo(isVideo);
        PhoneService.instance().answerCall(callId, isVideo);
    }

    //是否免提
    public static void setHf(int callId, boolean isHf) {
        PhoneService.instance().setSpeakerphoneOn(callId, isHf);
    }


    //是否静音
    public static void setMute(int callId, boolean isMute) {
        if (isMute) {
            PhoneService.instance().stopVoiceChannel(callId);
        } else {
            PhoneService.instance().startVoiceChannel(callId);
        }
    }

    //是否显示视频画面
    public static void update(int callId, boolean video) {
        PhoneService.instance().updateCall(callId, video);
    }

    //切换摄像头
    public static void switchCamera(int callId) {
        PhoneService.instance().switchCamera(callId);
    }

    public static void setLocalVideoRender(int callId, FrameLayout videoLocalLayout) {
        PhoneService.instance().setLocalVideoRender(callId, videoLocalLayout);
    }

    public static void setRemoteVideoRender(int callId, FrameLayout videoRemoteLayout) {
        PhoneService.instance().setRemoteVideoRender(callId, videoRemoteLayout);
    }

    public static String getWebRtcErrMessage(int callId)
    {
       return  PhoneService.instance().getWebRtcErrMessage(callId);
    }

    public static CallInfoBean getCallInfo(int accId, int callId) {
        CallInfoBean mCallInfoBean = new CallInfoBean();
        CallParams mCallParams = PhoneService.instance().getCallParams(callId);
        if(mCallParams != null) {
            mCallInfoBean.setLocalUsername(mCallParams.localUri());
            mCallInfoBean.setLocalDisplayName(mCallParams.localDisplayName());
            mCallInfoBean.setRemoteUsername(mCallParams.remoteUri());
            mCallInfoBean.setRemoteDisplayName(mCallParams.remoteDisplayName());
            int type = 0;
            if(mCallParams.getDirection() == Direction.Incoming)
                type = 1;
            mCallInfoBean.setType(type);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mCallInfoBean.setVideo(mCallParams.videoEnabled());
            mCallInfoBean.setMute(mCallParams.isMute());
            mCallInfoBean.setHf(mCallParams.isHf());
        }
        return mCallInfoBean;
    }

    public static CallLogBean getCallLog(int accId, int callId) {
         CallLogBean mCallLogBean = new CallLogBean();
         CallParams mCallParams = PhoneService.instance().getCallParams(callId);
         if(mCallParams != null) {
             mCallLogBean.setLocalUsername(mCallParams.localUri());
             mCallLogBean.setLocalDisplayName(mCallParams.localDisplayName());
             mCallLogBean.setRemoteUsername(mCallParams.remoteUri());
             mCallLogBean.setRemoteDisplayName(mCallParams.remoteDisplayName());
             int type = 0;
             if(mCallParams.getDirection() == Direction.Incoming)
                 type = 1;
             mCallLogBean.setType(type);
             mCallLogBean.setStatus(mCallParams.getState());
             mCallLogBean.setReasonCode(mCallParams.getReasion());
             SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             mCallLogBean.setStartTime(format.format(mCallParams.getStartTime()));
             mCallLogBean.setEndTime(format.format(mCallParams.getEndTime()));
             mCallLogBean.setVideoCall(mCallParams.videoEnabled());
         }
         return mCallLogBean;
    }
}
