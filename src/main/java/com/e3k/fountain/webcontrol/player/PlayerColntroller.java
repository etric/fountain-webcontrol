package com.e3k.fountain.webcontrol.player;

import java.util.List;

public interface PlayerColntroller {

    void reloadPlaylistItem(int num);

    List<String> getPlaylistItems();

    void getCurrentPlayingItem();
}
