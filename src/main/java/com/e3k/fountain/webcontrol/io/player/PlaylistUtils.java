package com.e3k.fountain.webcontrol.io.player;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

import static java.util.Objects.requireNonNull;

@Slf4j
public final class PlaylistUtils {

    public static final int PLAYLIST_SIZE = 20;

    private PlaylistUtils() {
        throw new AssertionError();
    }

    public static boolean isValidMusicNum(int techNum) {
        return techNum >= 0 && techNum < PLAYLIST_SIZE;
    }

    public static PlaylistItem[] getPlaylist() {
        String musicFolderPath = "music";
        File folder = new File(musicFolderPath);
        if (!folder.exists()) {
            throw new IllegalStateException("Folder " + musicFolderPath + " not found");
        }
        boolean isEmpty = true;
        final PlaylistItem[] result = new PlaylistItem[PLAYLIST_SIZE];
        File[] fileList = requireNonNull(folder.listFiles());
        for (File fileItem : fileList) {
            if (!fileItem.isDirectory()) {
                continue;
            }
            int techNum = Integer.parseInt(fileItem.getName()) - 1;
            if (!isValidMusicNum(techNum)) {
                continue;
            }
            String song = findSongInFolder(fileItem);
            if (song != null) {
                String fullPathToSong = mkFullPath(musicFolderPath, fileItem.getName(), song);
                result[techNum] = new PlaylistItem(techNum, song, fullPathToSong);
                isEmpty = false;
            }
        }
        if (isEmpty) {
            String fullPath = PlaylistUtils.class.getClassLoader().getResource("fountain_sound.mp3").getFile();
            result[0] = new PlaylistItem(0, "Звук фонтана", fullPath);
        }
        return result;
    }

    static PlaylistItem getPlaylistItem(int techNum) {
        if (!isValidMusicNum(techNum)) {
            return null;
        }
        String musicFolderPath = "music";
        int realNum = techNum + 1;
        File songFolder = new File(musicFolderPath, String.valueOf(realNum));
        if (!songFolder.exists()) {
            throw new IllegalStateException("Folder " + musicFolderPath + File.separatorChar + realNum + " not found");
        }
        String song = findSongInFolder(songFolder);
        if (song == null) {
            return null;
        }
        String fullPathToSong = mkFullPath(musicFolderPath, String.valueOf(realNum), song);
        return new PlaylistItem(techNum, song, fullPathToSong);
    }

    private static String findSongInFolder(File fileItem) {
        File[] listOfFilesInside = requireNonNull(fileItem).listFiles();
        for (File fileItemInside : requireNonNull(listOfFilesInside)) {
            if (!fileItemInside.isFile()) {
                continue;
            }
            String loweredName = fileItemInside.getName().toLowerCase();
            if (loweredName.endsWith(".mp3")) {
                return fileItemInside.getName();
            }
        }
        return null;
    }

    private static String mkFullPath(String... pathEntries) {
        StringBuilder sb = new StringBuilder();
        for (String pathEntry : pathEntries) {
            sb.append(pathEntry).append(File.separatorChar);
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
