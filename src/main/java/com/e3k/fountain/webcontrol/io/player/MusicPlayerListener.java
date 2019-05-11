package com.e3k.fountain.webcontrol.io.player;

@Deprecated
public interface MusicPlayerListener {
    void errorOccurred(String errorMessage);
    void playlistItemEnded(int itemIndex);
    void playlistItemStarted(int itemIndex, String songName);
}