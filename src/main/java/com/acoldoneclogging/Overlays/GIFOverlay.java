package com.acoldoneclogging.Overlays;

import com.acoldoneclogging.AColdOneCloggingPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GIFOverlay extends OverlayPanel {

	@Inject
    private BufferedImage bufferedImage;

	private final AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

    public GIFOverlay(String initialImage) {
        bufferedImage = ImageUtil.loadImageResource(GIFOverlay.class, initialImage);
        setPosition(OverlayPosition.DYNAMIC);
    }
	@Override
    public Dimension render(Graphics2D graphics) {
		graphics.setComposite(alphaComposite);
        graphics.drawImage(bufferedImage, 100, 100, null);
        return null;
    }
    public void setImage(String image) {
        bufferedImage = ImageUtil.loadImageResource(AColdOneCloggingPlugin.class, image);
    }
}