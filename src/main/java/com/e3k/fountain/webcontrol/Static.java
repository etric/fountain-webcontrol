package com.e3k.fountain.webcontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.e3k.fountain.webcontrol.player.PlaylistItem;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.FileReader;
import javax.swing.JOptionPane;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public class Static {

    public static boolean D = false;
    public static final String APP_NAME = "Scheduled Musical Serial Port Writer";
    
    public static final Image ICON_STOPPED = Toolkit.getDefaultToolkit().getImage(Static.class.getResource("/icon_stopped.gif"));
    public static final Image ICON_WAITING = Toolkit.getDefaultToolkit().getImage(Static.class.getResource("/icon_waiting.gif"));
    public static final Image ICON_ACTIVE = Toolkit.getDefaultToolkit().getImage(Static.class.getResource("/icon_active.gif"));
    
    private static StringBuilder sb = new StringBuilder();

    public static List<PlaylistItem> getPlaylist(String dataFolderPath) throws IOException {
        List<PlaylistItem> result = new ArrayList<PlaylistItem>();
        File folder = new File(dataFolderPath);
        if (!folder.exists()) throw new IOException("Отсутствует каталог 'data'.");
        for (File fileItem : folder.listFiles()) {
            if (!fileItem.isDirectory()) continue;
            File[] listOfFilesInside = fileItem.listFiles();
            String song = null;
            for (File fileItemInside : listOfFilesInside) {
                if (!fileItemInside.isFile()) continue;
                String loweredName = fileItemInside.getName().toLowerCase();
                if (loweredName.endsWith(".mp3")) song = fileItemInside.getName();
            }
            String fullPathToSong = song == null ? null : mkFullPath(dataFolderPath, fileItem.getName(), song);
            result.add(new PlaylistItem(Integer.parseInt(fileItem.getName()), song, fullPathToSong));
        }
        return result;
    }
    
    public static int[] readSceneryFile(String sceneryFileName) throws IOException, NumberFormatException {
        File f = new File(sceneryFileName);
        long actualLastMod = f.lastModified();
        long storedLastMod = SceneriesCache.ONE.getSceneryLastModified(sceneryFileName);
        if (actualLastMod > storedLastMod) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            sb.setLength(0);
            String strLine;
            while ((strLine = br.readLine()) != null) sb.append(strLine).append(' ');
            br.close();
            int[] data = parseData(sb.toString());
            SceneriesCache.ONE.store(sceneryFileName, actualLastMod, data);
            return data;
        } else {
            return SceneriesCache.ONE.getDataFor(sceneryFileName);
        }
    }
    
    public static int[] parseData(String stringData) {
        StringTokenizer st = new StringTokenizer(stringData);
        int[] data = new int[st.countTokens()];
        int i = 0;
        while (st.hasMoreElements()) data[i++] = Math.min(255, Math.abs(Integer.parseInt((String)st.nextElement())));
        return data;
    }
    
    private static String mkFullPath(String...pathEntries) {
        sb.setLength(0);
        for (String pathEntry : pathEntries) sb.append(pathEntry).append(File.separatorChar);
        String result = sb.deleteCharAt(sb.length() - 1).toString();
        debug("mkFullPath = " + result);
        return result;
    }
    
    public static void showErrorMessageBox(String text) {
//        if (text == null) return;
        javax.swing.JOptionPane.showMessageDialog(null, text, "Ошибка", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        Toolkit.getDefaultToolkit().beep();
    }
    
    public static void showInfoMessageBox(String text) {
        javax.swing.JOptionPane.showMessageDialog(null, text, "Информация", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showClosingDialog() {
        String[] options = new String[] {"Да", "Нет"};
        int answer = JOptionPane.showOptionDialog(null,
                "Вы уверены, что хотите выйти?", "Выход", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        return (answer == 0);
    }

    //////////////////////////////////////////////////////////////////////////

    public static Date numStr2Date(String stringLong) {
        long numValue;
        try {
            numValue = Long.parseLong(stringLong);
        } catch (NumberFormatException nfe) {
            numValue = System.currentTimeMillis();
        }
        return new Date(numValue);
    }
    
    public static String numDate2Str(Date date) {
        return Long.toString(date.getTime());
    }
    
    public static Integer tryParseInt(String string) {
        Integer value = null;
        try {
            value = Integer.parseInt(string);
            debug("tryParseInt: "+string+"->"+value);
        } catch (NumberFormatException nfe) {
        }
        return value;
    }
    
    public static void setUiEnabled(Component component, boolean isEnabled) {
        if (component instanceof Container)
            for (Component c : ((Container)component).getComponents()) {
                setUiEnabled(c, isEnabled);
                c.setEnabled(isEnabled);
            }
        else component.setEnabled(isEnabled);
    }
    
    public synchronized static void debug(String string) {
        if (D) System.out.println("+ ".concat(string));
    }
    
    public synchronized static void error(Exception ex) {
        ex.printStackTrace(System.err);
    }
    
    @Deprecated
    public synchronized static void debug(Exception exception) {
        debug("- ".concat(formatExceptionMessage(exception)));
    }
    
    public static String formatExceptionMessage(Exception ex) {
        return /*ex.getClass().getSimpleName() + ": " + */ ex.getMessage();
    }
    
    public static void trySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
        }
    }
    
    public static void setTrayIconStopped(TrayIcon icon) {
        if (icon == null) return;
        icon.setImage(ICON_STOPPED);
    }
    
    public static void setTrayIconActive(TrayIcon icon) {
        if (icon == null) return;
        icon.setImage(ICON_ACTIVE);
    }
    
    public static void setTrayIconWaiting(TrayIcon icon) {
        if (icon == null) return;
        icon.setImage(ICON_WAITING);
    }
}