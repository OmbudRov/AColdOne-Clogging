package com.acoldoneclogging.Overlays;

import com.acoldoneclogging.AColdOneCloggingConfig;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.util.Text;

@Slf4j
public class GIFTriggers extends OverlayPanel
{
	@Inject
	private Client client;
	@Inject
	private AColdOneCloggingConfig config;
	@Inject
	private ScheduledExecutorService executorService;
	@Inject
	private OverlayManager overlayManager;


	private final GIFOverlay skyArriveOverlay = new GIFOverlay("/gottaGo/175.gif");
	private final GIFOverlay gottaGoOverlay = new GIFOverlay("/gottaGo/0.gif");

	private final GIFOverlay leoSpinOverlay = new GIFOverlay("/leoSpin/0.gif");
	private final GIFOverlay wideLeoOverlay = new GIFOverlay("/wideLeo/0.gif");

	//Leo GIFs
	private final String[] wideLeoIcons = new String[59];
	private final String[] leoSpinIcons = new String[66];

	//Sky GIFs
	private final String[] gottaGoIcons = new String[175];

	public void initialise()
	{
		setupGIFs();
	}

	public void shutDown()
	{
		overlayManager.remove(skyArriveOverlay);
		overlayManager.remove(gottaGoOverlay);
		overlayManager.remove(wideLeoOverlay);
		overlayManager.remove(leoSpinOverlay);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.PUBLICCHAT && Text.sanitize(chatMessage.getName()).equalsIgnoreCase(client.getLocalPlayer().getName()))
		{
			String message = chatMessage.getMessage();
			if (config.skyEmotes() && message.equalsIgnoreCase("!skyArrive"))
			{
				playGIF("skyArrive");
			}
			else if (config.skyEmotes() && message.equalsIgnoreCase("!gottaGo"))
			{
				playGIF("gottaGo");
			}
			else if (config.leoEmotes() && message.equalsIgnoreCase("!wideLeo"))
			{
				playGIF("wideLeo");
			}
			else if (config.leoEmotes() && message.equalsIgnoreCase("!leoSpin"))
			{
				playGIF("leoSpin");
			}

		}
	}

	private void setupGIFs()
	{
		for (int i = 0; i < 59; i++)
		{
			wideLeoIcons[i] = "/wideLeo/" + i + ".gif";
		}

		for (int i = 0; i < 66; i++)
		{
			leoSpinIcons[i] = "/leoSpin/" + i + ".gif";
		}
		for (int i = 0; i < 175; i++)
		{
			gottaGoIcons[i] = "/gottaGo/" + i + ".gif";
		}
	}

	private void playGIF(String emoteName)
	{
		switch (emoteName)
		{
			case "skyArrive":
				gifPlayer(skyArriveOverlay, gottaGoIcons, 30, TRUE);
				break;
			case "gottaGo":
				gifPlayer(gottaGoOverlay, gottaGoIcons, 30, FALSE);
				break;
			case "wideLeo":
				gifPlayer(wideLeoOverlay, wideLeoIcons, 40, FALSE);
				break;
			case "leoSpin":
				gifPlayer(leoSpinOverlay, leoSpinIcons, 40, FALSE);
				break;
		}
	}

	private void gifPlayer(GIFOverlay gifOverlay, String[] gifFrames, int frameDelay, boolean reversedGIF)
	{
		overlayManager.add(gifOverlay);
		final Timer timer = new Timer();
		TimerTask task = new TimerTask()
		{
			int currentIndex = reversedGIF ? gifFrames.length - 1 : 0;
			int loopAmount = config.LoopAmount();

			@Override
			public void run()
			{
				if (reversedGIF)
				{
					if (currentIndex >= 0)
					{
						gifOverlay.setImage(gifFrames[currentIndex]);
						currentIndex--;
					}
					else
					{
						loopAmount--;
						if (loopAmount != 0)
						{
							timer.cancel();
							executorService.schedule(() -> overlayManager.remove(gifOverlay), 60, TimeUnit.MILLISECONDS);
						}
						else
						{
							currentIndex = gifFrames.length - 1;
						}
					}
				}
				else
				{
					if (currentIndex < gifFrames.length)
					{
						gifOverlay.setImage(gifFrames[currentIndex]);
						currentIndex++;
					}
					else
					{
						loopAmount--;
						if (loopAmount != 0)
						{
							timer.cancel();
							executorService.schedule(() -> overlayManager.remove(gifOverlay), 60, TimeUnit.MILLISECONDS);
						}
						else
						{
							currentIndex = 0;
						}
					}
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, frameDelay);
	}

}

