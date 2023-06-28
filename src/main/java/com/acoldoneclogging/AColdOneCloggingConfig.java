package com.acoldoneclogging;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("example")
public interface AColdOneCloggingConfig extends Config
{
	@Range(
		min = 0,
		max = 200
	)
	@ConfigItem(
		keyName = "Volume",
		name = "Volume",
		description = "Control how loud the audio should be"
	)
	default int Volume()
	{
		return 100;
	}
}
