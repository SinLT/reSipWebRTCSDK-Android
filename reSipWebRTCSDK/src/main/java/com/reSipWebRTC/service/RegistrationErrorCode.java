package com.reSipWebRTC.service;

import java.util.Vector;

public class RegistrationErrorCode {
	private static Vector<RegistrationErrorCode> values = new Vector<RegistrationErrorCode>();
	public static RegistrationErrorCode None = new RegistrationErrorCode("",0);
	public static RegistrationErrorCode PaymentRequired = new RegistrationErrorCode("Payment Required",402);
	public static RegistrationErrorCode Forbidden = new RegistrationErrorCode("Forbidden",403);
	public static RegistrationErrorCode NotFound = new RegistrationErrorCode("Not Found",404);
	public static RegistrationErrorCode RequestTimeout = new RegistrationErrorCode("Request Timeout",408);
	public static RegistrationErrorCode NetworkUnreachable  = new RegistrationErrorCode("Network Unreachable",477);
	public static RegistrationErrorCode ServiceUnavailable  = new RegistrationErrorCode("Service Unavailable",503);
	private String mStringValue;
	private int mIntgerValue;
	public RegistrationErrorCode(String aStringValue,int aIntgerValue) {
		mStringValue = aStringValue;
		mIntgerValue = aIntgerValue;
		values.addElement(this);
	}
	public String getReason() {
		return mStringValue;
	}
	public int IntgerValue()
	{
		return mIntgerValue;
	}
	public static RegistrationErrorCode fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			RegistrationErrorCode mtype = (RegistrationErrorCode) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("RegistrationErrorCode not found [" + value + "]");
	}
}