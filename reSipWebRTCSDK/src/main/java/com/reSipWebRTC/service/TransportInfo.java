package com.reSipWebRTC.service;

/**
 * Created by xuban on 2017/6/29.
 */

public class TransportInfo {
    public final String localhost_ip;
    public final int localhost_port;

    private TransportInfo(String localhost_ip, int localhost_port)
    {
        this.localhost_ip = localhost_ip;
        this.localhost_port = localhost_port;
    }
}


