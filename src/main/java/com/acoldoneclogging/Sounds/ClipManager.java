package com.acoldoneclogging.Sounds;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Set;
import static java.util.function.Predicate.not;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class ClipManager
{
	private static final Path filesDirectory = Path.of(RuneLite.RUNELITE_DIR.getPath(), "acoldone poopoopeepee42rentfree"); // why hells? https://imgur.com/a/3iB5xiV
	private static final HttpUrl fileSource = HttpUrl.parse("https://raw.githubusercontent.com/OmbudRov/AColdOne-Clogging/clips");

	public static void filePrep(OkHttpClient okHttpClient)
	{
		filesDirectoryExists();
		deleteExtraFiles();
		downloadNewFiles(okHttpClient);
	}

	public static File getClip(Sound sound){
		return filesDirectory.resolve(sound.getResourceName()).toFile();
	}

	private static void filesDirectoryExists()
	{
		try
		{
			if (!Files.exists(filesDirectory))
			{
				Files.createDirectory(filesDirectory);
			}

		}
		catch (FileAlreadyExistsException e)
		{
			//:ignore:
		}
		catch (IOException e)
		{
			log.error("Cant make directory");
		}
	}

	private static void deleteExtraFiles()
	{
		Set<String> filesToKeep = Arrays.stream(Sound.values()).map(Sound::getResourceName).collect(Collectors.toSet());
		Set<Path> toDelete = getAllFilesFromDirectory().stream().filter(not(filesToKeep::contains)).map(filesDirectory::resolve).collect(Collectors.toSet());
		try
		{
			for(Path path : toDelete){
				Files.delete(path);
			}
		}
		catch (IOException e){
			log.error("Failed to delete unused sound files", e);
		}
	}

	private static void downloadNewFiles(OkHttpClient okHttpClient)
	{
		filesToDownload().forEach(file -> downloadFile(okHttpClient, file));
	}

	private static void downloadFile(OkHttpClient okHttpClient, String file)
	{
		if(fileSource == null){
			log.error("Please contact Ombud in the ColdOne discord, he probably fucked something up in the AColdOne Clogging Plugin");
			return;
		}
		HttpUrl clipURL = fileSource.newBuilder().addPathSegment(file).build();
		Request request = new Request.Builder().url(clipURL).build();
		try(Response response = okHttpClient.newCall(request).execute()){
			if(response.body() != null){
				Files.copy(new BufferedInputStream(response.body().byteStream()), filesDirectory.resolve(file), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e)
		{
			log.error("Acoldone Clogging couldn't download file",e);
		}

	}

	private static Stream<String> filesToDownload()
	{
		Set<String> files = getAllFilesFromDirectory();
		return Arrays.stream(Sound.values()).map(Sound::getResourceName).filter(not(files::contains));
	}

	private static Set<String> getAllFilesFromDirectory()
	{
		try (Stream<Path> paths = Files.list(filesDirectory))
		{
			return paths.filter(path -> !Files.isDirectory(path)).map(Path::toFile).map(File::getName).collect(Collectors.toSet());
		}
		catch (IOException e)
		{
			log.warn("Couldnt find files in {}, is empty", filesDirectory);
			return Set.of();
		}
	}
}
