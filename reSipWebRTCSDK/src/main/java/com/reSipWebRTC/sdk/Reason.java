package com.reSipWebRTC.sdk;

public class Reason {
    private static  final  int WebrtcErr = 1;//WebRTC内部错误
    private static  final  int LocalBye = 3;
    private static  final  int RemoteBye = 4;
    private static  final  int LocalCancel = 5;//本地取消通话
    private static  final  int RemoteCancel = 6;//对方远程取消通话
    private static  final  int LocalRejected = 7;//对方拨号，自己拒接
    private static  final  int UserBUSY = 17;//对方忙，对方挂号
    private static  final  int NoAnswer = 19;//对方没有接听(一直响铃到挂断)
    private static  final  int CallRejected = 21;//对方拒绝接听(603)
    private static  final  int NormalTemporaryFailure= 41;//网络等异常导致没有正常挂断

    /*public static enum CallEndReason
    {
        Error(0), //拨号错误，错误原因有TemporarilyUnavailable(对方不在线)，RemoteBusy(对方忙),Decline(对方拒接)
        Timeout,//拨号超时
        Replaced,
        LocalBye,//本地挂断
        RemoteBye,//远程挂断
        LocalCancel,//本地拨号挂断(不拒听，不接听，SDK自己挂断)，这里的作用没体现出来，这个功能看TemporarilyUnavailable
        RemoteCancel,//远程拨号挂断(不拒听，不接听，SDK自己挂断)
        LocalRejected,//本地拒接
        Referred,
        NO_ANSWER(19),
        TemporarilyUnavailable,//480 对方不在线，本地拨号挂断(不拒听，不接听，SDK自己挂断)
        RemoteBusy,//对方忙(对应SIP 486信令)
        RemoteDecline //对方拒接(对应SIP 603信令)
    }*/
}
