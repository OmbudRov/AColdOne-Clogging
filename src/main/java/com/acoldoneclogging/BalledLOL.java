package com.acoldoneclogging;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.DrawManager;
import static net.runelite.http.api.RuneLiteAPI.GSON;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Data
class DiscordWebhookBody {
	private String content;
	private Embed embed;

	@Data
	static class Embed {
		final UrlEmbed image;
	}

	@Data
	static class UrlEmbed {
		final String url;
	}
}


@Slf4j
public class BalledLOL
{
	@Inject
	private Client client;
	@Inject
	private ScheduledExecutorService executorService;
	@Inject
	private AColdOneCloggingConfig config;
	@Inject
	private SoundEngine soundEngine;
	@Inject
	private DrawManager drawManager;
	@Inject
	private OkHttpClient okHttpClient;

	private int lastBalledTick = -1;

	private boolean gettingBalled = false;

	//Getting Balled LOL
	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileMoved)
	{
		int currentTick = client.getTickCount();
		if (currentTick - lastBalledTick > 500)
		{
			gettingBalled = false;
		}
		if (gettingBalled)
		{
			return;
		}
		Projectile projectile = projectileMoved.getProjectile();
		if (projectile.getId() != 55)
		{
			return;
		}
		Actor me = client.getLocalPlayer();
		if (me == null)
		{
			return;
		}
		Actor projectileInteraction = projectile.getInteracting();


		if (!me.equals(projectileInteraction))
		{
			return;
		}
		if (!config.Balled())
		{
			return;
		}
		gettingBalled = true;
		lastBalledTick = currentTick;
		me.setOverheadText("Oh no, i got balled");
		executorService.schedule(() -> {
			soundEngine.playClip(Sound.valueOf("Balled_1"));
			if (config.BalledScreenshot() && config.WebhookLink() != null)
			{
				sendScreenshot(me.getName());
			}
			client.getLocalPlayer().setOverheadText("");
		}, 1200, TimeUnit.MILLISECONDS);
	}
	private void sendScreenshot(String playerName)
	{
		String MessageString;
		MessageString = String.format("%s %s", playerName, "got balled <:x0r6ztlurk:948329913734275093>");
		DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
		discordWebhookBody.setContent(MessageString);

		String webhookLink = config.WebhookLink();
		HttpUrl url = HttpUrl.parse(webhookLink);
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart("payload_json", GSON.toJson(discordWebhookBody));

		drawManager.requestNextFrameListener(image ->
		{
			BufferedImage bufferedImage = (BufferedImage) image;
			byte[] imageBytes;
			try
			{
				imageBytes = toByteArray(bufferedImage);
			}
			catch (IOException e)
			{
				log.warn("Error converting image to byte array", e);
				return;
			}

			requestBodyBuilder.addFormDataPart("file", "image.png",
				RequestBody.create(MediaType.parse("image/png"), imageBytes));
			buildRequestAndSend(url, requestBodyBuilder);
		});
	}


	private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
	{
		RequestBody requestBody = requestBodyBuilder.build();
		Request request = new Request.Builder()
			.url(url)
			.post(requestBody)
			.build();

		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Error submitting webhook", e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				response.close();
			}
		});
	}
	private static byte[] toByteArray(BufferedImage bufferedImage) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}
}
