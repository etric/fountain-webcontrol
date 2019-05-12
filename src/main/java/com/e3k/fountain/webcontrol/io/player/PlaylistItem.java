package com.e3k.fountain.webcontrol.io.player;

import lombok.AllArgsConstructor;

import java.io.File;

@AllArgsConstructor
public class PlaylistItem implements Comparable<PlaylistItem> {
    
    public final int order;
    public final String songName;
    public final File songFile;
    public final boolean isExternal;

    public PlaylistItem(int order, String songName, File songFile) {
        this(order, songName, songFile, true);
    }

    @Override
    public String toString() {
        return order + "# " + songName;
    }

    @Override
    public int compareTo(PlaylistItem o) {
        return Integer.compare(this.order, o.order);
    }
}