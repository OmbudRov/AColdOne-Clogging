package com.acoldoneclogging;

import net.runelite.client.config.*;

@ConfigGroup("AColdOneClogging")
public interface AColdOneCloggingConfig extends Config
{
	@ConfigSection(
			name = "Misc Sounds",
			description = "Any Extra funny sounds that i may add",
			position = 1
	)
	String Misc="misc";
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
	@ConfigItem(
			keyName = "Balled",
			name = "Getting Balled",
			description = "Get Balled Lmao",
			section = Misc
	)
	default boolean Balled()
	{
		return true;
	}
	@ConfigItem(
			keyName = "WideLeo",
			name = "Wide Leo Emote",
			description = "Overlay the 7TV emote \"LeoWidenUp\" on the screen when saying \"!Leo\" ",
			section = Misc
	)
	default boolean WideLeo()
	{
		return true;
	}
}
