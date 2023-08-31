package com.acoldoneclogging.Overlays;

import com.acoldoneclogging.AColdOneCloggingPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WideLeoOverlay extends OverlayPanel {
    @Inject
    private Client client;
    private BufferedImage bufferedImage;
    public WideLeoOverlay() {
        bufferedImage = ImageUtil.loadImageResource(WideLeoOverlay.class, "/WideLeo/0.gif");
        setPosition(OverlayPosition.DYNAMIC);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        graphics.setComposite(ac);
        graphics.drawImage(bufferedImage.getScaledInstance(384, 128, Image.SCALE_SMOOTH), 100, 100, null);
        return null;
    }
    public void setImage(String image) {
        bufferedImage = ImageUtil.loadImageResource(AColdOneCloggingPlugin.class, image);
    }
}