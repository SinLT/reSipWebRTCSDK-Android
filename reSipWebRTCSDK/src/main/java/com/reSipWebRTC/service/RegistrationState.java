package com.reSipWebRTC.service;

import java.util.Vector;

public class RegistrationState {
	private static Vector<RegistrationState> values = new Vector<RegistrationState>();
	public static RegistrationState None = new RegistrationState("None",-1);
	public static RegistrationState Progress = new RegistrationState("RegistrationProgress",0);
	public static RegistrationState Sucess = new RegistrationState("RegistrationSucess",1);
	public static RegistrationState Cleared = new RegistrationState("RegistrationCleared",2);
	public static RegistrationState Failed = new RegistrationState("RegistrationFailed",3);
	private String mStringValue;
	private int mIntgerValue;
	public String mFailedReason;
	private RegistrationState(String aStringValue,int aIntgerValue) {
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
	public static RegistrationState fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			RegistrationState mtype = (RegistrationState) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("RegistrationState not found [" + value + "]");
	}
};
