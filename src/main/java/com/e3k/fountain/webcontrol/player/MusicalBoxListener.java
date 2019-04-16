package com.e3k.fountain.webcontrol.player;

public interface MusicalBoxListener {
    void errorOccurred(String errorMessage);
    void playlistItemEnded(int itemIndex);
    void playlistItemStarted(int itemIndex, String songName);
}