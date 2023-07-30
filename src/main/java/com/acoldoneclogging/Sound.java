package com.acoldoneclogging;

public enum Sound
{
	CollectionLog_1("/Clogging_1.wav"),
	CollectionLog_2("/Clogging_2.wav"),
	CollectionLog_3("/Clogging_3.wav"),
	CollectionLog_4("/Clogging_4.wav"),
	CollectionLog_5("/Clogging_5.wav"),
	CollectionLog_6("/Clogging_6.wav"),
	CollectionLog_7("/Clogging_7.wav"),
	CollectionLog_8("/Clogging_8.wav"),

	Balled_1("/Balled_1.wav");
	private final String ResourceName;

	Sound(String resName)
	{
		ResourceName=resName;

	}
	String getResourceName()
	{
		return ResourceName;
	}
}
