package com.acoldoneclogging;

import com.google.inject.Provides;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "AColdOne Clogging")
public class AColdOneCloggingPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private AColdOneCloggingConfig config;
    @Inject
    private SoundEngine soundEngine;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private ScheduledExecutorService executorService;
    private static final Pattern ClogRegex = Pattern.compile("New item added to your collection log:.*");
    private static final Set<Integer> BadClogSettings = new HashSet<>() {{
        add(0);
        add(2);
    }};
    private int LastClogWarning = -1;
    private int LastLoginTick = -1;
    private int lastBalledTick = -1;
    private boolean LoggedIn = false;
    private boolean functionRunning = false;

    @Override
    protected void startUp() throws Exception {
        LastLoginTick = -1;
    }

    @Override
    protected void shutDown() throws Exception {
        soundEngine.close();
    }

    @Subscribe
    protected void onGameStateChanged(GameStateChanged stateChanged) {
        LoggedIn = stateChanged.getGameState() == GameState.LOGGED_IN;
        switch (stateChanged.getGameState()) {
            case LOGIN_SCREEN:
            case LOGGING_IN:
            case HOPPING:
            case CONNECTION_LOST:
                LastLoginTick = -1;
                LastClogWarning = client.getTickCount();
                break;
            case LOGGED_IN:
                LastLoginTick = client.getTickCount();
                break;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (ClogRegex.matcher(chatMessage.getMessage()).matches()) {
            Random random = new Random();
            int logNumber = random.nextInt(8) + 1;
            Sound selectedLog = Sound.valueOf("CollectionLog_" + logNumber);
            soundEngine.playClip(selectedLog);
        }
    }

    private void WarnForClogSettings(int newVarbitValue) {
        if (BadClogSettings.contains(newVarbitValue)) {
            if (LastClogWarning == -1 || client.getTickCount() - LastClogWarning > 10) {
                LastClogWarning = client.getTickCount();
                SendMessage("Please enable \"Collection log - New addition notification\" in your game settings or switch off the \"AColdOne Clogging\" plugin to switch off this warning");
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarbitId() == Varbits.COLLECTION_LOG_NOTIFICATION) {
            WarnForClogSettings(varbitChanged.getValue());
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved projectileMoved) {
        if (functionRunning) {
            return;
        }
        functionRunning = true;
        Projectile projectile = projectileMoved.getProjectile();
        if (projectile.getId() != 55) {
            return;
        }
        int currentTick = client.getTickCount();
        if (currentTick - lastBalledTick < 1) {
            return;
        }
        Actor Me = client.getLocalPlayer();
        if (Me == null) {
            return;
        }
        Actor projectileInteraction = projectile.getInteracting();
        if (!Me.equals(projectileInteraction)) {
            return;
        }
        if (!config.Balled()) {
            return;
        }
        lastBalledTick = currentTick;
        executorService.schedule(() -> {
            soundEngine.playClip(Sound.valueOf("Balled_1"));
        }, 0, TimeUnit.SECONDS);
    }

    public void SendMessage(String Message) {
        String HighlightedMessage = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(Message).build();

        chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(HighlightedMessage).build());
    }

    @Provides
    AColdOneCloggingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AColdOneCloggingConfig.class);
    }
}
