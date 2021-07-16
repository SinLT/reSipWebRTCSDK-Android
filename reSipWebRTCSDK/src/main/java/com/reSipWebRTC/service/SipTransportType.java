package com.reSipWebRTC.service;

import java.util.Vector;

public class SipTransportType {
	private static Vector<SipTransportType> values = new Vector<SipTransportType>();
	public static SipTransportType UDP = new SipTransportType("UDP",0);
	public static SipTransportType TCP = new SipTransportType("TCP",1);
	public static SipTransportType TLS = new SipTransportType("TLS",2);
	private String mStringValue;
	private int mIntgerValue;
	private SipTransportType(String aStringValue,int aIntgerValue) {
		mStringValue = aStringValue;
		mIntgerValue = aIntgerValue;
		values.addElement(this);
	}
	public String toString() {
		return mStringValue;
	}
	public int IntgerValue()
	{
		return mIntgerValue;
	}
	
	public static SipTransportType fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			SipTransportType mtype = (SipTransportType) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("SipTransportType not found [" + value + "]");
	}
	
	public static SipTransportType fromString(String value) {
		for (int i = 0; i < values.size(); i++) {
			SipTransportType mtype = (SipTransportType) values.elementAt(i);
			if (mtype.mStringValue.equals(value.toUpperCase())) return mtype;
		}
		throw new RuntimeException("SipTransportType not found [" + value + "]");
	}
}