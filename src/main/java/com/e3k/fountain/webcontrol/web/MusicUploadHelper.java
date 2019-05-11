package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.io.player.MusicPlayer;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

@Slf4j
class MusicUploadHelper {

    public MusicUploadHelper() {
        throw new AssertionError();
    }

    static void upload(Part filePart, int realNum) throws IOException {
        final String fileName = filePart.getSubmittedFileName();
        log.info("Uploading new music file #{}: {}", realNum, fileName);

        final Path musicFile = prepareTargetFile(realNum, fileName);
        log.debug("New music file path {}", musicFile.toAbsolutePath().toString());

        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, musicFile, StandardCopyOption.REPLACE_EXISTING);
        }

        //clear other music files in the folder
        final File musicNumFolder = musicFile.getParent().toFile();
        final File[] musicOldFiles = requireNonNull(
                musicNumFolder.listFiles((dir, name) -> !name.equals(fileName)));
        Arrays.stream(musicOldFiles).forEach(File::delete);

        final int techNum = realNum - 1;
        MusicPlayer.ONE.reloadPlaylistItem(techNum);
    }

    private static Path prepareTargetFile(int musicNum, String fileName) throws IOException {
        File musicDir = new File(System.getProperty("user.dir"), "music");
        musicDir.mkdir();
        File musicNumDir = new File(musicDir, String.valueOf(musicNum));
        musicNumDir.mkdir();
        String fileFullPath = System.getProperty("user.dir") + "/music/" + musicNum + "/" + fileName;
        Path path = Paths.get(fileFullPath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        return path;
    }
}
