package com.e3k.fountain.webcontrol.ui;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TimePicker extends javax.swing.JPanel implements ChangeListener {
    
    public static final long MIN_STEP_MINUTES = 5 * 60 * 1000;   // 5 MINUTES
    public static final String TIME_FORMAT = "HH:mm";
    
    public TimePicker() {
        initComponents();
        
        spinnerTimeStart.setModel(new SpinnerDateModel());
        spinnerTimeStart.setEditor(new JSpinner.DateEditor(spinnerTimeStart, TIME_FORMAT));
        
        spinnerTimeEnd.setModel(new SpinnerDateModel());
        spinnerTimeEnd.setEditor(new JSpinner.DateEditor(spinnerTimeEnd, TIME_FORMAT));
        
        spinnerTimeStart.setValue(new Date());
        spinnerTimeEnd.setValue(new Date());

        spinnerTimeStart.addChangeListener(TimePicker.this);
        spinnerTimeEnd.addChangeListener(TimePicker.this);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        spinnerTimeStart = new javax.swing.JSpinner();
        lblEnd = new javax.swing.JLabel();
        spinnerTimeEnd = new javax.swing.JSpinner();
        lblStart = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLayout(new java.awt.GridBagLayout());

        spinnerTimeStart.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1334926016291L), null, null, java.util.Calendar.MINUTE));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spinnerTimeStart, gridBagConstraints);

        lblEnd.setText("Конец");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        add(lblEnd, gridBagConstraints);

        spinnerTimeEnd.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1334926016291L), null, null, java.util.Calendar.MINUTE));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spinnerTimeEnd, gridBagConstraints);

        lblStart.setText("Начало");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        add(lblStart, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel lblEnd;
    javax.swing.JLabel lblStart;
    javax.swing.JSpinner spinnerTimeEnd;
    javax.swing.JSpinner spinnerTimeStart;
    // End of variables declaration//GEN-END:variables

    
    public Date getStart() {
        return (Date) spinnerTimeStart.getValue();
    }

    public TimePicker timeStart(Date time) {
        spinnerTimeStart.setValue(time);
        return this;
    }
    
    public Date getEnd() {
        return (Date) spinnerTimeEnd.getValue();
    }

    public TimePicker setTimeEnd(Date time) {
        spinnerTimeEnd.setValue(time);
        return this;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == spinnerTimeEnd) {
            Date beforeTime = clearFieldsExceptHHmm((Date) spinnerTimeStart.getValue());
            Date thisTime = clearFieldsExceptHHmm((Date) spinnerTimeEnd.getValue());
            long diff = thisTime.getTime() - beforeTime.getTime();
            if (diff < MIN_STEP_MINUTES) {
                spinnerTimeEnd.setValue(new Date(beforeTime.getTime() + MIN_STEP_MINUTES));
            }
        } else {
            Date thisTime = clearFieldsExceptHHmm((Date) spinnerTimeStart.getValue());
            Date afterTime = clearFieldsExceptHHmm((Date) spinnerTimeEnd.getValue());
            long diff = afterTime.getTime() - thisTime.getTime();
            if (diff < MIN_STEP_MINUTES) {
                spinnerTimeEnd.setValue(new Date(thisTime.getTime() + MIN_STEP_MINUTES));
            }
        }
    }
    
    private static final Calendar calendar = Calendar.getInstance();
    
    private Date clearFieldsExceptHHmm(Date d) {
        calendar.setTime(d);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.DAY_OF_MONTH);
        calendar.clear(Calendar.DAY_OF_WEEK);
        calendar.clear(Calendar.DAY_OF_WEEK_IN_MONTH);
        calendar.clear(Calendar.DAY_OF_YEAR);
        calendar.clear(Calendar.WEEK_OF_MONTH);
        calendar.clear(Calendar.WEEK_OF_YEAR);
        calendar.clear(Calendar.MONTH);
        calendar.clear(Calendar.YEAR);        
        d.setTime(calendar.getTimeInMillis());
        return d;
    }
}