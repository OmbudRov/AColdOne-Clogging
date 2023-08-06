package com.acoldoneclogging;

import com.acoldoneclogging.Overlays.WideLeoOverlay;
import com.google.inject.Provides;

import java.io.File;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

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
    @Inject
    private OverlayManager overlayManager;

    private final WideLeoOverlay wideLeoOverlay =new WideLeoOverlay();
    private static final Pattern ClogRegex = Pattern.compile("New item added to your collection log:.*");
    private static final Pattern TaskRegex = Pattern.compile("Congratulations, you've completed an? (?:\\\\w+) combat task:.*");
    private static final Set<Integer> BadClogSettings = new HashSet<>() {{
        add(0);
        add(2);
    }};
    private int LastClogWarning = -1;
    private int LastLoginTick = -1;
    private int lastBalledTick = -1;
    private boolean functionRunning = false;
    private final String[] wideLeoIcons = new String[52];


    @Override
    protected void startUp() throws Exception {
        LastLoginTick = -1;
        LeoWidenSetup();
    }

    @Override
    protected void shutDown() throws Exception {
        soundEngine.close();
    }

    @Subscribe
    protected void onGameStateChanged(GameStateChanged stateChanged) {
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
        if (chatMessage.getType() == ChatMessageType.PUBLICCHAT) {
            String Message = chatMessage.getMessage();
            if (config.WideLeo() && Message.equalsIgnoreCase("!Leo") && chatMessage.getName().equalsIgnoreCase(client.getLocalPlayer().getName())) {
                overlayManager.add(wideLeoOverlay);
                LeoWiden();
            }

        } else if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {

            if (config.AnnounceClog() && ClogRegex.matcher(chatMessage.getMessage()).matches()) {
                Random random = new Random();
                int logNumber = random.nextInt(9) + 1;
                Sound selectedLog = Sound.valueOf("CollectionLog_" + logNumber);
                soundEngine.playClip(selectedLog);
            } else if (config.AnnounceCombatTasks() && TaskRegex.matcher(chatMessage.getMessage()).matches()) {
                soundEngine.playClip(Sound.valueOf("TaskCompletion"));
            }
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

    public void LeoWiden() {
        final Timer timer = new Timer();
        long interval = 40; // One second in milliseconds

        TimerTask task = new TimerTask() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < wideLeoIcons.length) {
                    String image = wideLeoIcons[currentIndex];
                    wideLeoOverlay.setImage(image);
                    currentIndex++;
                } else {
                    // All iterations completed, cancel the timer
                    timer.cancel();
                    executorService.schedule(()->{
                        overlayManager.remove(wideLeoOverlay);
                    },250,TimeUnit.MILLISECONDS);
                }
            }
        };

        // Schedule the task to run at fixed intervals
        timer.scheduleAtFixedRate(task, 0, interval);
    }

    public void LeoWidenSetup() {
        int i = 0;
        File folder = new File("src/main/resources/WideLeo");
        File[] ImageFiles = folder.listFiles();
        if (ImageFiles != null) {
            for (File file : ImageFiles) {
                ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
                if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) {
                    wideLeoIcons[i] = "/WideLeo/" + file.getName();
                    i++;
                }
            }
        }
    }

    @Provides
    AColdOneCloggingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AColdOneCloggingConfig.class);
    }
}
