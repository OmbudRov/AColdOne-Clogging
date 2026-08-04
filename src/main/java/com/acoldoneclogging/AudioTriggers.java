package com.acoldoneclogging;

import com.acoldoneclogging.Sounds.Sound;
import com.acoldoneclogging.Sounds.SoundEngine;
import java.util.Random;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
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
	private static final Pattern taskRegex = Pattern.compile("CA_ID:\\d+\\|Congratulations, you've completed an? \\w+ combat task:.*");
	private static final Pattern leaguesTaskRegex = Pattern.compile("Congratulations, you've completed an? \\w+ task:.*");

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
				Sound selectedLog = Sound.valueOf("CollectionLog_" + (random.nextInt(13) + 1));
				soundEngine.playClip(selectedLog);
			}
//			Leagues Hijinks
			/*else if (config.AnnounceLeaguesTasks() && leaguesTaskRegex.matcher(Message).matches())
			{
				Sound selectedLog = Sound.valueOf("LeaguesTask_" + (random.nextInt(7) + 1));
				soundEngine.playClip(selectedLog);
			}*/
			else if (config.AnnounceCombatTasks() && taskRegex.matcher(Message).matches())
			{
				Sound selectedLog = Sound.valueOf("TaskCompletion_" + (random.nextInt(3) + 1));
				soundEngine.playClip(selectedLog);
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

	//ZCB Ruby Spec Clip
	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event) {
		int soundId = event.getSoundId();

		if (config.ZCBRuby()) {
			if (soundId == 2911 && isZCBEquipped()) { //2911 is the Ruby Spec Sound ID
				event.consume();
				soundEngine.playClip(Sound.valueOf("ZCB_Ruby"));
			}
		}
	}
	//Function to check if the player is wearing a ZCB
	private boolean isZCBEquipped()
	{
		final var worn = client.getItemContainer(InventoryID.WORN);
		return worn != null && worn.contains(ItemID.ZARYTE_XBOW);
	}

}
