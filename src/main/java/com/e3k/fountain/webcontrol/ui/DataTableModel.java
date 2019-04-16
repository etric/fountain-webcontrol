package com.e3k.fountain.webcontrol.ui;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import com.e3k.fountain.webcontrol.player.PlaylistItem;

import java.util.Collections;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public class DataTableModel extends AbstractTableModel {

    private static final String[] columnNames = { "♪", "Номер", "Музыка" };
    private static final String nowPlayingSymbol = "<html><b><font size=4>&nbsp;♪<font></b></html>";
    
    private List<PlaylistItem> data;
    private int nowPlaying = -1;

    public DataTableModel() {
        this.data = Collections.EMPTY_LIST;
    }
    
    public DataTableModel(List<PlaylistItem> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public PlaylistItem getPlaylistItem(int rowIndex) {
        return data.get(rowIndex);
    }
    
    public String getPlayingSongName() {
        if (nowPlaying < 0 || nowPlaying >= data.size()) return null;
        return data.get(nowPlaying).songName;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PlaylistItem item = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return rowIndex == nowPlaying ? nowPlayingSymbol : "";
            case 1: return item.order;
            case 2: return item.songName;
            default: return "";
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void loadData(List<PlaylistItem> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public int getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(int index) {
        clearNowPlaying(index);
        nowPlaying = index;
        fireTableCellUpdated(index, 0);
    }
    
    public void clearNowPlaying(int index) {
        this.nowPlaying = -1;
        fireTableCellUpdated(index, 0);
    }
    
    public List<PlaylistItem> getPlaylist() {
        return data;
    }
}