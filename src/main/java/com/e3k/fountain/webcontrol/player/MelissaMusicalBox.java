package com.e3k.fountain.webcontrol.player;

import com.e3k.fountain.webcontrol.MelissaSerialPortWriter;
import com.e3k.fountain.webcontrol.Static;
import javazoom.jlgui.basicplayer.*;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Map;


public enum MelissaMusicalBox implements BasicPlayerListener {
    
    ONE;
    
    private BasicPlayer audioPlayer;
    private List<PlaylistItem> playlist;
    private MusicalBoxListener listener;
    private int secsDelayBetweenSongs;
    
    private int nowPlaying = -1;
    
    public void init(
            List<PlaylistItem> playlist,
            MusicalBoxListener listener,
            int secsDelayBetweenSongs) {
        this.playlist = playlist;
        this.secsDelayBetweenSongs = secsDelayBetweenSongs;
        if (null == audioPlayer) {
            audioPlayer = new BasicPlayer();
        }
        this.listener = listener;
        audioPlayer.addBasicPlayerListener(this);
    }
    
    public void startPlaylistFromBegin() {
//        stopPlayingAndWriting(); //TODO check if was commented?????
        playAndWritePLI(0);
    }
    
    public void stopPlayingAndWriting() {
        stopPlaying();
        stopWriting();
    }
    
    public boolean isWorking() {
        return audioPlayer.getStatus() == BasicPlayer.PLAYING;
    }
    
    private void stopPlaying() {
        if (audioPlayer.getStatus() != BasicPlayer.STOPPED) {
            try {
                audioPlayer.stop();
            } catch (BasicPlayerException e) {
                listener.errorOccurred(Static.formatExceptionMessage(e));
            }
        }
        SwingUtilities.invokeLater(() -> listener.playlistItemEnded(nowPlaying));
    }
    
    private void stopWriting() {
        MelissaSerialPortWriter.ONE.stopWriting();
    }

    private void playAndWritePLI(final int pliIndex) {
        final PlaylistItem item = playlist.get(pliIndex);

        if (item.fullPathToSong != null) {
            try {
                System.out.println("PLAYING AUDIO");
                audioPlayer.open(new File(item.fullPathToSong));
//                MelissaSerialPortWriter.ONE.writeBytes(dataToWrite, null);
                audioPlayer.play();
                // Set Volume (0 to 1.0).
                audioPlayer.setGain(0.25);
                // Set Pan (-1.0 to 1.0).
                audioPlayer.setPan(0.0);
            } catch (BasicPlayerException e) {
                listener.errorOccurred(Static.formatExceptionMessage(e));
//                e.printStackTrace();
            }
        } else {
//            MelissaSerialPortWriter.ONE.writeBytes(dataToWrite, this);
        }
        
        nowPlaying = pliIndex;
        
        SwingUtilities.invokeLater(() -> listener.playlistItemStarted(pliIndex, item.songName));
    }
    
    private void endOfMedia() {
        stopWriting();
        SwingUtilities.invokeLater(() -> listener.playlistItemEnded(nowPlaying));
        Static.trySleep(secsDelayBetweenSongs * 1000);
        if (nowPlaying < playlist.size() - 1) {
            playAndWritePLI(nowPlaying + 1);
        } else {
            startPlaylistFromBegin();
        }     
    }

    @Override
    public void opened(Object stream, Map properties) {
        System.out.println("Opened: " + properties);
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        System.out.println("stateUpdated : " + event);
        if (event.getCode() == BasicPlayerEvent.EOM) {
            endOfMedia();
        }
    }

    @Override
    public void setController(BasicController controller) {
        System.out.println("setController : " + controller);
    }
}