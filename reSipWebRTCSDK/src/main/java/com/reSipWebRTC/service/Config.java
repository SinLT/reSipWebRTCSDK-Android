package com.reSipWebRTC.service;

public abstract class Config
{
	/*Save Config*/
	public abstract void Save();
	/*SIP*/
	public String user_agent = "Android Client v2.0";
	public int udp_port = 5060;
	public int tcp_port = 5060;
	public int tls_port = 5061;
	
	/*Media*/
	public String audio_codecs = "opus,isac,g729,pcma,pcmu";
	public String video_codecs = "vp8,vp9,h264,red,ulpfec,rtx";
	public int rtp_port_start = 10000;
	public int rtp_port_end = 65535;
	public int mtu = 1200;
	
	/*ICE*/
	public String stun_server = "112.124.62.164";
	public int stun_server_port = 19302;
	public String turn_server = "";
	public int turn_server_port = 19302;
	public String turn_username = "";
	public String turn_password = "";
	

	/*Debug*/
	public boolean log_on = false;
	public String sip_trac_file = "";
	public String voe_trac_file = "";
	public String vie_trac_file = "";
}
