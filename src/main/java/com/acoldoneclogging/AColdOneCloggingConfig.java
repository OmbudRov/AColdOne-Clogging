package com.acoldoneclogging;

import net.runelite.client.config.*;

@ConfigGroup("AColdOneClogging")
public interface AColdOneCloggingConfig extends Config
{
	@ConfigSection(
		name = "GIFs",
		description = "Cause Leo & Sky deserve their own section :",
		position = 1
	)
	String GIFs = "GIFs";

	@ConfigSection(
		name = "Misc Sounds",
		description = "Any Extra funny sounds that i may add",
		position = 2
	)
	String Misc = "misc";

	@Range(
		min = 0,
		max = 100
	)
	@ConfigItem(
		keyName = "Volume",
		name = "Volume",
		description = "Control how loud the audio should be"
	)
	default int Volume()
	{
		return 73;
	}

	@ConfigItem(
		keyName = "AnnounceClog",
		name = "Collection Logs",
		description = "Announces Collection Logs with an Audio Clip"
	)
	default boolean AnnounceClog()
	{
		return true;
	}

	@ConfigItem(
		keyName = "AnnounceCombatTasks",
		name = "Combat Achievements",
		description = "Announces Combat Task Completions with an Audio Clip"
	)
	default boolean AnnounceCombatTasks()
	{
		return true;
	}

//	@ConfigItem(
//		keyName = "AnnounceLeaguesTasks",
//		name = "Leagues Tasks",
//		description = "Announces Leagues Task Completions with an Audio Clip"
//	)
//	default boolean AnnounceLeaguesTasks(){
//		return true;
//	}

	@ConfigItem(
		keyName = "AnnounceDeath",
		name = "Death",
		description = "Announces Deaths with an Audio Clip"
	)
	default boolean AnnounceDeath()
	{
		return true;
	}

	@ConfigItem(
		keyName = "KEBAB",
		name = "Kebab Drops",
		description = "Celebrate Kebab drops with an Audio Clip"
	)
	default boolean KEBAB()
	{
		return true;
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
		keyName = "BalledScreenshot",
		name = "Screenshot of getting Balled",
		description = "Screenshot of you getting balled being shared in the #[INSERT CURRENT CHANNEL NAME] of the AColdOne Discord Server",
		section = Misc
	)
	default boolean BalledScreenshot()
	{
		return true;
	}

	@ConfigItem(
		keyName = "WebhookLink",
		name = "Discord Webhook Link",
		description = "Discord Webhook Link to send screenshot to",
		section = Misc
	)
	default String WebhookLink()
	{
		return "";
	}

	@ConfigItem(
		keyName = "leoEmotes",
		name = "Leo Emotes",
		description = "Overlay emotes of Leo(\"!leo\",\"!leoSpin\") ",
		position = 1,
		section = GIFs
	)
	default boolean leoEmotes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "skyEmotes",
		name = "Sky Emotes",
		description = "Overlay emotes of Sky (\"!gottaGo\",\"!skyArrive\")",
		position = 2,
		section = GIFs
	)
	default boolean skyEmotes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "LoopAmount",
		name = "Loop Amount",
		description = "Number of times to loop the gif on the screen when saying \"!LeoSpin\" ",
		position = 3,
		section = GIFs
	)
	default int LoopAmount()
	{
		return 1;
	}
}
