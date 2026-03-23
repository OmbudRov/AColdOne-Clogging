package com.acoldoneclogging.Sounds;

import com.acoldoneclogging.AColdOneCloggingConfig;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

@Singleton
@Slf4j
public class SoundEngine
{
	@Inject
	private AColdOneCloggingConfig config;
	@Inject
	private AudioPlayer audioPlayer;

	public void playClip(Sound sound)
	{
		float gain = 20f * (float) Math.log10(config.Volume() / 100f);
		try
		{

			audioPlayer.play(ClipManager.getClip(sound),gain);
		}
		catch (Exception e)
		{
			log.info("Error Playing Clip: {}", sound,e);
		}
	}
}
