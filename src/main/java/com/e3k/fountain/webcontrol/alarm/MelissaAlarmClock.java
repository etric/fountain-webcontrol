package com.e3k.fountain.webcontrol.alarm;

import com.e3k.fountain.webcontrol.Static;
import com.e3k.fountain.webcontrol.player.MelissaMusicalBox;
import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;
import fr.dyade.jdring.AlarmManager;
import fr.dyade.jdring.PastDateException;

import java.util.Calendar;
import java.util.Date;
import javax.swing.SwingUtilities;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public enum MelissaAlarmClock {

    ONE;
    
    private AlarmManager alarmManager;
    private AlarmClockListener listener;
    private boolean isWorking;

    public void turnOn(AlarmClockListener listener, Date timeStart, Date timeEnd, boolean startImmediately) {
        
        if (timeStart == null || timeEnd == null || listener == null)
            throw new IllegalArgumentException("Parameters can't be null");

        this.listener = listener;
        this.isWorking = false;
        alarmManager = new AlarmManager();
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeStart);
        int startMinutes = cal.get(Calendar.MINUTE);
        int startHours = cal.get(Calendar.HOUR_OF_DAY);
        cal.setTime(timeEnd);
        int endMinutes = cal.get(Calendar.MINUTE);
        int endHours = cal.get(Calendar.HOUR_OF_DAY);
        try {
            alarmManager.addAlarm(startMinutes, startHours, -1, -1, -1, -1, startAlarmListener);
            Static.debug("time_start alarm added");
            alarmManager.addAlarm(endMinutes, endHours, -1, -1, -1, -1, endAlarmListener);
            Static.debug("time_end alarm added");
        } catch (PastDateException pde) {
            Static.debug(pde);
        }
        if (startImmediately) startAlarmListener.handleAlarm(null);
    }
    
    public void turnOff() {
        if (alarmManager == null) return;
        alarmManager.removeAllAlarms();
        alarmManager = null;
        MelissaMusicalBox.ONE.stopPlayingAndWriting();
    }
    
    public boolean isActive() {
        return (alarmManager != null);
    }
    
    private final AlarmListener startAlarmListener = new AlarmListener() {
        @Override
        public void handleAlarm(AlarmEntry entry) {
            if (isWorking) return;
            isWorking = true;
            Static.debug("ALARM HANDLED: time_start");
            MelissaMusicalBox.ONE.startPlaylistFromBegin();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.alarmStartHandled();
                }
            });
        }
    };
    private final AlarmListener endAlarmListener = new AlarmListener() {
        @Override
        public void handleAlarm(AlarmEntry entry) {
            isWorking = false;
            Static.debug("ALARM HANDLED: time_end");
            MelissaMusicalBox.ONE.stopPlayingAndWriting();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.alarmEndHandled();
                }
            });
        }
    };
}