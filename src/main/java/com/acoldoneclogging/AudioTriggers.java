package com.acoldoneclogging;

import java.util.Random;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;

public class AudioTriggers
{
	@Inject
	private AColdOneCloggingConfig config;
	@Inject
	private SoundEngine soundEngine;
	@Inject
	private Client client;


	private static final Pattern clogRegex = Pattern.compile("New item added to your collection log:.*");
	private static final Pattern taskRegex = Pattern.compile("Congratulations, you've completed an? (?:\\w+) combat task:.*");
	private static final Pattern KEBAB = Pattern.compile("Your reward*Kebab*");
	//private static final Pattern leaguesTaskRegex = Pattern.compile("Congratulations, you've completed an? \\w+ task:.*");

	private final Random random = new Random();



	//CAs, Clogs and leagues
	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{

		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String Message = chatMessage.getMessage();
			if (config.AnnounceClog() && clogRegex.matcher(Message).matches())
			{
				Sound selectedLog = Sound.valueOf("CollectionLog_" + (random.nextInt(16) + 1));
				soundEngine.playClip(selectedLog);
			}
//			Leagues Hijinks
//			else if (config.AnnounceLeaguesTasks() && leaguesTaskRegex.matcher(Message).matches())
//			{
//				Sound selectedLog = Sound.valueOf("LeaguesTask_" + (random.nextInt(3) + 1));
//				soundEngine.playClip(selectedLog);
//			}
			else if (config.AnnounceCombatTasks() && taskRegex.matcher(Message).matches())
			{
				Sound selectedLog = Sound.valueOf("TaskCompletion_" + (random.nextInt(3) + 1));
				soundEngine.playClip(selectedLog);
			}
			else if (config.KEBAB() && KEBAB.matcher(Message).matches())
			{
				soundEngine.playClip(Sound.valueOf("KEBAB"));
			}
		}
	}

	//Death Audio Clip
	@Subscribe
	public void onActorDeath(ActorDeath actorDeath)
	{
		if (actorDeath.getActor() != client.getLocalPlayer())
		{
			return;
		}
		if (config.AnnounceDeath())
		{
			soundEngine.playClip(Sound.valueOf("Death"));
		}
	}
}