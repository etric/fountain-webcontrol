package com.e3k.fountain.webcontrol.io.player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlaylistItem implements Comparable<PlaylistItem> {
    
    public final int order;
    public final String songName;
    public final String fullPathToSong;

    @Override
    public String toString() {
        return order + "# " + songName;
    }

    @Override
    public int compareTo(PlaylistItem o) {
        return Integer.compare(this.order, o.order);
    }
}