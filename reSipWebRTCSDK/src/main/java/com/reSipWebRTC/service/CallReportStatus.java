package com.reSipWebRTC.service;

import java.util.Vector;

public class CallReportStatus {
	private static Vector<CallReportStatus> values = new Vector<CallReportStatus>();
	public static CallReportStatus Answered = new CallReportStatus("Answered",0);
	public static CallReportStatus Rejected = new CallReportStatus("Rejected",1);
	public static CallReportStatus Cancel = new CallReportStatus("Cancel",2);
	public static CallReportStatus Failed = new CallReportStatus("Failed",3);
	public static CallReportStatus Unknown = new CallReportStatus("Unknown",-1);
	private String mStringValue;
	private int mIntgerValue;
	private CallReportStatus(String aStringValue,int aIntgerValue) {
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
	
	public static CallReportStatus fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			CallReportStatus mtype = (CallReportStatus) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("CallReportStatus not found [" + value + "]");
	}
}