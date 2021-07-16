package com.reSipWebRTC.service;

import java.util.Vector;

public class CallState {
	static private Vector<CallState> values = new Vector<CallState>();
	public static CallState Unknown = new CallState("Unknown",-1);
	public static CallState NewCall = new CallState("NewCall",0);
	public static CallState Cancel = new CallState("Cancel",1);
	public static CallState Failed = new CallState("Failed",2);
	public static CallState Rejected = new CallState("Rejected",3);
	public static CallState EarlyMedia = new CallState("EarlyMedia",4);
	public static CallState Ringing = new CallState("Ringing",5);
	public static CallState Answered = new CallState("Answered",6);
	public static CallState Hangup = new CallState("Hangup",7);
	public static CallState Pausing = new CallState("Pausing",8);
	public static CallState Paused = new CallState("Paused",9);
	public static CallState Resuming = new CallState("Resuming",10);
	public static CallState Resumed = new CallState("Resumed",11);
	public static CallState Updating = new CallState("Updating",12);
	public static CallState Updated = new CallState("Updated",13);
  
	private String mStringValue;
	private int mIntgerValue;
	private CallState(String aStringValue,int aIntgerValue) {
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
	
	public static CallState fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			CallState mtype = (CallState) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("CallState not found [" + value + "]");
	}

}