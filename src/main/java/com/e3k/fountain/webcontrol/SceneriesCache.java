package com.e3k.fountain.webcontrol;

import java.util.HashMap;
import java.util.Map;


public enum SceneriesCache {
    
    ONE;
    
    private final Map<String, SceneryCachable> cache = new HashMap<String, SceneryCachable>();
    
    public long getSceneryLastModified(String fileName) {
        if (fileName == null) return -1;
        SceneryCachable sceneryCachable = cache.get(fileName);
        if (sceneryCachable == null) return -1;
        return sceneryCachable.lastModified;
    }
    
    public void store(String fileName, long lastModified, int[] data) {
        if (fileName == null || data == null) return;
        SceneryCachable sceneryCachable = cache.get(fileName);
        if (sceneryCachable != null) {
            sceneryCachable.lastModified = lastModified;
            sceneryCachable.data = data;
        } else {
            cache.put(fileName, new SceneryCachable(lastModified, data));
        }
    }
    
    public int[] getDataFor(String sceneryFileName) {
        if (sceneryFileName == null) return null;
        SceneryCachable sceneryCachable = cache.get(sceneryFileName);
        return sceneryCachable.data;
    }
    
    static class SceneryCachable {
        private long lastModified;
        private int[] data;

        public SceneryCachable(long lastModified, int[] data) {
            this.lastModified = lastModified;
            this.data = data;
        }
    }
}