package com.e3k.fountain.webcontrol.io.player;

import com.e3k.fountain.webcontrol.config.PropertiesManager;
import javazoom.jlgui.basicplayer.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public enum MusicPlayer implements BasicPlayerListener {

    ONE;

    private final BasicPlayer audioPlayer;
    private final PlaylistItem[] playlist;

    private volatile int nowPlaying;
    private volatile int volume;
    private volatile int pauseBetweenTracks = 0;

    MusicPlayer() {
        audioPlayer = new BasicPlayer();
        audioPlayer.addBasicPlayerListener(this);
        nowPlaying = -1;
        playlist = PlaylistUtils.getPlaylist();
        volume = PropertiesManager.ONE.getVolume();
        pauseBetweenTracks = PropertiesManager.ONE.getPauseBetweenTracks();
    }

    public synchronized void startPlaylistWhereLeft() {
        int lastPlayedItem = PropertiesManager.ONE.getLastPlayedItem();
        playItem(lastPlayedItem + 1);
    }

    public synchronized void reloadPlaylistItem(int techNum) {
        final int realNum = techNum + 1;
        log.info("Reloading {} playlist item", realNum);
        playlist[techNum] = PlaylistUtils.getPlaylistItem(techNum);
    }

    public List<String> getPlaylistItems() {
        //TODO cache?
        log.info("Getting playlist");
        return Arrays.stream(playlist)
                .map(i -> (i == null) ? null : i.songName)
                .collect(Collectors.toList());
    }

    public int getCurrentPlayingItem() {
        return nowPlaying;
    }

    public boolean isPlaying() {
        return audioPlayer.getStatus() == BasicPlayer.PLAYING;
    }

    public synchronized void changeVolume(int volume) {
        log.info("Changing volume {}", volume);
        PropertiesManager.ONE.setVolume(volume);
        try {
            if (isPlaying()) {
                audioPlayer.setGain((double) volume / 100);
            }
            this.volume = volume;
        } catch (BasicPlayerException e) {
            log.error("Failed changing volume", e);
        }
    }

    public int getVolume() {
        return volume;
    }

    public synchronized void changePauseBetweenTracks(int pauseBetweenTracks) {
        log.info("Changing pause between tracks {}", pauseBetweenTracks);
        PropertiesManager.ONE.setPauseBetweenTracks(pauseBetweenTracks);
        this.pauseBetweenTracks = pauseBetweenTracks;
    }

    public int getPauseBetweenTracks() {
        return pauseBetweenTracks;
    }

    public synchronized void stopPlaying() {
        if (audioPlayer.getStatus() != BasicPlayer.STOPPED) {
            log.info("Stopping player");
            try {
                audioPlayer.stop();
                nowPlaying = -1;
            } catch (BasicPlayerException e) {
                log.error("Failed stopping player", e);
            }
        } else {
            log.warn("Cannot stop player - already stopped!");
        }
    }

    private void playItem(int pliIndex) {
        pliIndex = pliIndex % PlaylistUtils.PLAYLIST_SIZE;
        log.debug("Switched to item {}", pliIndex);
        final PlaylistItem item = playlist[pliIndex];
        if (item != null && item.songFile != null) {
            try {
                log.debug("Started playing {}", item);
                if (item.isExternal) {
                    audioPlayer.open(item.songFile);
                } else {
                    ClassLoader classLoader = getClass().getClassLoader();
                    audioPlayer.open(classLoader.getResourceAsStream(item.songFile.getName()));
                }
                audioPlayer.play();
                audioPlayer.setGain((double) volume / 100);
                // Set Pan (-1.0 to 1.0).
                audioPlayer.setPan(0.0);
                nowPlaying = pliIndex;
                PropertiesManager.ONE.setLastPlayedItem(nowPlaying);
            } catch (BasicPlayerException e) {
                log.error("Failed playing item " + pliIndex, e);
                playItem(pliIndex + 1);
            }
        } else {
            playItem(pliIndex + 1);
        }
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        if (event.getCode() == BasicPlayerEvent.EOM) {
            if (pauseBetweenTracks > 0) {
                //TODO switch devices according to music state!
                try {
                    Thread.sleep(pauseBetweenTracks);
                } catch (InterruptedException e) {
                    log.error("Failed pausing between tracks", e);
                }
            }
            playItem(nowPlaying + 1);
        }
    }

    @Override
    public void opened(Object stream, Map properties) {
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
    }

    @Override
    public void setController(BasicController controller) {
    }
}
