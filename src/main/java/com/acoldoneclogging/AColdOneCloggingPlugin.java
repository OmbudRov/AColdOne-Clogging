package com.acoldoneclogging;


import com.acoldoneclogging.Overlays.GIFTriggers;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(name = "AColdOne Clogging")
public class AColdOneCloggingPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private AColdOneCloggingConfig config;

	@Inject
	private AudioTriggers audioTriggers;
	@Inject
	private BalledLOL balledLOL;
	@Inject
	private GIFTriggers gifOverlays;

    @Inject
    private SoundEngine soundEngine;
	@Inject
    private ScheduledExecutorService executorService;
    @Inject
    private OverlayManager overlayManager;


	@Inject
	private EventBus eventBus;


    @Override
    protected void startUp() throws Exception {


		eventBus.register(audioTriggers); //Audio Clips
		eventBus.register(balledLOL); //Getting balled lol
		eventBus.register(gifOverlays); //Leo and Sky GIFs
		gifOverlays.initialise();
    }

    @Override
    protected void shutDown() throws Exception {
        soundEngine.close();
		eventBus.unregister(audioTriggers);
		eventBus.unregister(balledLOL);
		eventBus.unregister(gifOverlays);
		gifOverlays.shutDown();
    }

	@Provides
    AColdOneCloggingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AColdOneCloggingConfig.class);
    }
}
