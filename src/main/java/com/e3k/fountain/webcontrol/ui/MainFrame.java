package com.e3k.fountain.webcontrol.ui;

import com.e3k.fountain.webcontrol.MelissaSerialPortWriter;
import com.e3k.fountain.webcontrol.alarm.AlarmClockListener;
import com.e3k.fountain.webcontrol.player.MelissaMusicalBox;
import com.e3k.fountain.webcontrol.player.MusicalBoxListener;
import com.e3k.fountain.webcontrol.player.PlaylistItem;

import java.awt.AWTException;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;

import jssc.SerialPortException;
import jssc.SerialPortList;
import com.e3k.fountain.webcontrol.PropertiesManager;
import com.e3k.fountain.webcontrol.alarm.MelissaAlarmClock;
import com.e3k.fountain.webcontrol.Static;

import java.awt.GridBagConstraints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.SystemTray;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import static com.e3k.fountain.webcontrol.PropertiesManager.*;


public class MainFrame extends JFrame implements WindowListener, MusicalBoxListener, AlarmClockListener {
    
    private TestTextArea testTextArea;
    private TimePicker timePicker;
    private DataTableModel playlistTableModel;
    private String pathToDataFolder;
    private TrayIcon trayIcon;
    
    private boolean isVisibleExtraSettings;
    private boolean isBusy = false;
    
    public MainFrame() {
        initComponents();
        initComponents2();
        
        setPreferredSize(new Dimension(1000, 550));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        
        addWindowListener(MainFrame.this);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        panelSettings = new javax.swing.JPanel();
        btnSaveSettings = new javax.swing.JButton();
        labelScheduler = new javax.swing.JLabel();
        panelDelays = new javax.swing.JPanel();
        panelExtraDelaysSettings = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        spinnerBytesInPacket = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        spinnerMillisDelayBetweenPackets = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        spinnerUDelayBetweenBits = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        spinnerSecDelayBetweenSongs = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelTestWriting = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stubTextArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        btnTest = new javax.swing.JButton();
        panelStub = new javax.swing.JPanel();
        panelPortAndDataExtraSettings = new javax.swing.JPanel();
        panelSerialPorts = new javax.swing.JPanel();
        comboSerialPorts = new javax.swing.JComboBox();
        btnFindAllSerialPorts = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        chkBitsInverse = new javax.swing.JCheckBox();
        panelPlaylist = new javax.swing.JPanel();
        scrollPanePlaylist = new javax.swing.JScrollPane();
        tablePlaylist = new javax.swing.JTable();
        labelPlaylist = new javax.swing.JLabel();
        panelMainButtons = new javax.swing.JPanel();
        btnRefreshPlaylist = new javax.swing.JButton();
        btnStartStopSystem = new javax.swing.JButton();
        chkPlayImmediately = new javax.swing.JCheckBox();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelSettings.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelSettings.setLayout(new java.awt.GridBagLayout());

        btnSaveSettings.setFont(new java.awt.Font("Tahoma", 1, 12));
        btnSaveSettings.setForeground(new java.awt.Color(75, 75, 75));
        btnSaveSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Button-Download-icon.png"))); // NOI18N
        btnSaveSettings.setText("Сохранить настройки");
        btnSaveSettings.setMaximumSize(new java.awt.Dimension(222, 50));
        btnSaveSettings.setMinimumSize(new java.awt.Dimension(222, 50));
        btnSaveSettings.setPreferredSize(new java.awt.Dimension(222, 50));
        btnSaveSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSettings.add(btnSaveSettings, gridBagConstraints);

        labelScheduler.setFont(new java.awt.Font("Tahoma", 1, 18));
        labelScheduler.setForeground(new java.awt.Color(75, 75, 75));
        labelScheduler.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gear-icon.png"))); // NOI18N
        labelScheduler.setText("Настройки");
        labelScheduler.setIconTextGap(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 5);
        panelSettings.add(labelScheduler, gridBagConstraints);

        panelDelays.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Задержки", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(75, 75, 75))); // NOI18N
        panelDelays.setLayout(new java.awt.GridBagLayout());

        panelExtraDelaysSettings.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Каждые");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelExtraDelaysSettings.add(jLabel4, gridBagConstraints);

        spinnerBytesInPacket.setModel(new javax.swing.SpinnerNumberModel(1, 1, 256, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panelExtraDelaysSettings.add(spinnerBytesInPacket, gridBagConstraints);

        jLabel5.setText("байт пауза");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelExtraDelaysSettings.add(jLabel5, gridBagConstraints);

        spinnerMillisDelayBetweenPackets.setModel(new javax.swing.SpinnerNumberModel(500, 200, 5000, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panelExtraDelaysSettings.add(spinnerMillisDelayBetweenPackets, gridBagConstraints);

        jLabel6.setText("мс");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelExtraDelaysSettings.add(jLabel6, gridBagConstraints);

        jLabel1.setText("Коэф. задержки между битами");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelExtraDelaysSettings.add(jLabel1, gridBagConstraints);

        spinnerUDelayBetweenBits.setModel(new javax.swing.SpinnerNumberModel(6, 5, 999, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelExtraDelaysSettings.add(spinnerUDelayBetweenBits, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        panelDelays.add(panelExtraDelaysSettings, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        spinnerSecDelayBetweenSongs.setModel(new javax.swing.SpinnerNumberModel(1, 1, 60, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(spinnerSecDelayBetweenSongs, gridBagConstraints);

        jLabel2.setText("Между песнями задержка");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabel7.setText("секунд(а)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panelDelays.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSettings.add(panelDelays, gridBagConstraints);

        panelTestWriting.setBackground(new java.awt.Color(222, 222, 222));
        panelTestWriting.setBorder(javax.swing.BorderFactory.createTitledBorder("Тест записи данных"));
        panelTestWriting.setLayout(new java.awt.GridBagLayout());

        stubTextArea.setColumns(20);
        stubTextArea.setRows(5);
        jScrollPane1.setViewportView(stubTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelTestWriting.add(jScrollPane1, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        btnTest.setText("Тест");
        btnTest.setOpaque(false);
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestActionPerformed(evt);
            }
        });
        jPanel3.add(btnTest);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelTestWriting.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSettings.add(panelTestWriting, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        panelSettings.add(panelStub, gridBagConstraints);

        panelPortAndDataExtraSettings.setLayout(new java.awt.GridBagLayout());

        panelSerialPorts.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "COM-порт", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(75, 75, 75))); // NOI18N
        panelSerialPorts.setLayout(new java.awt.GridBagLayout());

        comboSerialPorts.setMinimumSize(new java.awt.Dimension(60, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSerialPorts.add(comboSerialPorts, gridBagConstraints);

        btnFindAllSerialPorts.setText("Найти");
        btnFindAllSerialPorts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findAllSerialPorts(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSerialPorts.add(btnFindAllSerialPorts, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panelPortAndDataExtraSettings.add(panelSerialPorts, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Данные", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(75, 75, 75))); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        chkBitsInverse.setText("Инверсия бит");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(chkBitsInverse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        panelPortAndDataExtraSettings.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSettings.add(panelPortAndDataExtraSettings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        getContentPane().add(panelSettings, gridBagConstraints);

        panelPlaylist.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelPlaylist.setLayout(new java.awt.GridBagLayout());

        tablePlaylist.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrollPanePlaylist.setViewportView(tablePlaylist);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelPlaylist.add(scrollPanePlaylist, gridBagConstraints);

        labelPlaylist.setFont(new java.awt.Font("Tahoma", 1, 18));
        labelPlaylist.setForeground(new java.awt.Color(75, 75, 75));
        labelPlaylist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Document-icon.png"))); // NOI18N
        labelPlaylist.setText("Плейлист");
        labelPlaylist.setIconTextGap(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panelPlaylist.add(labelPlaylist, gridBagConstraints);

        panelMainButtons.setLayout(new java.awt.GridBagLayout());

        btnRefreshPlaylist.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnRefreshPlaylist.setForeground(new java.awt.Color(75, 75, 75));
        btnRefreshPlaylist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Button-Reload-icon.png"))); // NOI18N
        btnRefreshPlaylist.setText("Обновить список");
        btnRefreshPlaylist.setIconTextGap(10);
        btnRefreshPlaylist.setMaximumSize(new java.awt.Dimension(200, 50));
        btnRefreshPlaylist.setMinimumSize(new java.awt.Dimension(200, 50));
        btnRefreshPlaylist.setPreferredSize(new java.awt.Dimension(200, 50));
        btnRefreshPlaylist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reLoadPlaylistToTable(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelMainButtons.add(btnRefreshPlaylist, gridBagConstraints);

        btnStartStopSystem.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnStartStopSystem.setForeground(new java.awt.Color(75, 75, 75));
        btnStartStopSystem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Button-Turn-Off-icon.png"))); // NOI18N
        btnStartStopSystem.setText("СТАРТ");
        btnStartStopSystem.setIconTextGap(10);
        btnStartStopSystem.setMinimumSize(new java.awt.Dimension(200, 50));
        btnStartStopSystem.setPreferredSize(new java.awt.Dimension(200, 50));
        btnStartStopSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartStopSystemActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelMainButtons.add(btnStartStopSystem, gridBagConstraints);

        chkPlayImmediately.setText("Играть сразу");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panelMainButtons.add(chkPlayImmediately, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        panelPlaylist.add(panelMainButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(panelPlaylist, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initComponents2() {
        timePicker = new TimePicker();
        timePicker.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Будильник", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(75, 75, 75))); // NOI18N
        panelSettings.add(timePicker, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new java.awt.Insets(5, 5, 5, 5), 0, 0));
        
        Date timeStart = Static.numStr2Date(PropertiesManager.ONE.getProp(TIME_START));
        Date timeEnd = Static.numStr2Date(PropertiesManager.ONE.getProp(TIME_END));
        timePicker.timeStart(timeStart).setTimeEnd(timeEnd);
        
        Integer bytesInPacket = Static.tryParseInt(PropertiesManager.ONE.getProp(BYTES_IN_PACKET));
        if (bytesInPacket != null) spinnerBytesInPacket.setValue(bytesInPacket);
        
        Integer delayBetweenPacketsMillis = Static.tryParseInt(PropertiesManager.ONE.getProp(DELAY_BETWEEN_PACKETS_MILLIS));
        if (delayBetweenPacketsMillis != null) spinnerMillisDelayBetweenPackets.setValue(delayBetweenPacketsMillis);        
        
        Integer delayBetweenSongsSecs = Static.tryParseInt(PropertiesManager.ONE.getProp(DELAY_BETWEEN_SONGS_SECS));
        if (delayBetweenSongsSecs != null) spinnerSecDelayBetweenSongs.setValue(delayBetweenSongsSecs);
        
        Integer delayBetweenBitsUs = Static.tryParseInt(PropertiesManager.ONE.getProp(DELAY_BETWEEN_BITS_US));
        if (delayBetweenBitsUs != null) spinnerUDelayBetweenBits.setValue(delayBetweenBitsUs);
        
        pathToDataFolder = PropertiesManager.ONE.getProp(PropertiesManager.PATH_TO_DATA, "data");
        
        chkBitsInverse.setSelected(Boolean.parseBoolean(PropertiesManager.ONE.getProp(PropertiesManager.BITS_INVERSE)));
        
        playlistTableModel = new DataTableModel();
        tablePlaylist.setModel(playlistTableModel);
//        tablePlaylist.getColumnModel().getColumn(3).setPreferredWidth(300);
        tablePlaylist.getColumnModel().getColumn(2).setPreferredWidth(300);
        tablePlaylist.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                reLoadPlaylistToTable(null);
                findAllSerialPorts(null);
            }
        });
        
        testTextArea = new TestTextArea();
        jScrollPane1.setViewportView(testTextArea);

        if (SystemTray.isSupported()) {
            trayIcon = new TrayIcon(Static.ICON_STOPPED, Static.APP_NAME);
            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException ex) {
                Static.debug(ex);
            }
        }
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon_frame.gif")));
        setTitle(Static.APP_NAME);
        
        setVisibleExtraSettings(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            @Override
            public boolean postProcessKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_F12) {
                   setVisibleExtraSettings(!isVisibleExtraSettings);
                }
                return true;
            }
        });
        
        Static.debug("*** Initializing completed\n\n");
    }
    
    private void btnSaveSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveSettingsActionPerformed
        PropertiesManager.ONE
                .setProp(BITS_INVERSE, Boolean.toString(isInverseBits()))
                .setProp(BYTES_IN_PACKET, Integer.toString(getBytesInPacket()))
                .setProp(DELAY_BETWEEN_PACKETS_MILLIS, Integer.toString(getMillisDelayBetweenPackets()))
                .setProp(DELAY_BETWEEN_SONGS_SECS, Integer.toString(getSecDelayBetweenSongs()))
                .setProp(DELAY_BETWEEN_BITS_US, Integer.toString(getUDelayBetweenBits()))
                .setProp(TIME_START, Static.numDate2Str(timePicker.getStart()))
                .setProp(TIME_END, Static.numDate2Str(timePicker.getEnd()));
        
        if (PropertiesManager.ONE.storeProperties()) {
            Static.showInfoMessageBox("Настройки сохранены");
        } else {
            Static.showErrorMessageBox("Ошибка сохранения настроек");
        }        
    }//GEN-LAST:event_btnSaveSettingsActionPerformed

    private void btnStartStopSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartStopSystemActionPerformed
        if (MelissaAlarmClock.ONE.isActive()) {
            Static.debug("Stopping system...");
            
            MelissaAlarmClock.ONE.turnOff();
            Static.setTrayIconStopped(trayIcon);
            setUiEnabled(true);
            btnStartStopSystem.setText("СТАРТ");
        } else {
            Static.debug("Starting system...");
            
            List<PlaylistItem> playlist = playlistTableModel.getPlaylist();
            if (playlist.isEmpty()) {
                Static.showErrorMessageBox("Плейлист пуст.");
                Static.debug("Playlist is empty. Abort");
                return;
            }
            
            MelissaSerialPortWriter.ONE.init(getSelectedSerialPort(), getBytesInPacket(), getMillisDelayBetweenPackets(), getUDelayBetweenBits(), isInverseBits(), MainFrame.this);
            Static.debug("*** MelissaSerialPortWriter initialized");
            MelissaMusicalBox.ONE.init(playlist, MainFrame.this, getSecDelayBetweenSongs());
            Static.debug("*** MelissaMusicalBox initialized");
            MelissaAlarmClock.ONE.turnOn(MainFrame.this, timePicker.getStart(), timePicker.getEnd(), chkPlayImmediately.isSelected());
            Static.debug("*** MelissaAlarmClock turned on");

            setUiEnabled(false);
            btnStartStopSystem.setText("СТОП");
            btnStartStopSystem.setEnabled(true);
            Static.setTrayIconWaiting(trayIcon);
        }
    }//GEN-LAST:event_btnStartStopSystemActionPerformed

    private void findAllSerialPorts(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findAllSerialPorts
        comboSerialPorts.removeAllItems();
        String[] serialPortNames = SerialPortList.getPortNames();
        if (serialPortNames.length == 0) {
            Static.showErrorMessageBox("Нет доступных COM-портов");
        } else
        for (String serialPortName : serialPortNames) {
            comboSerialPorts.addItem(serialPortName);
        }
    }//GEN-LAST:event_findAllSerialPorts

    private void reLoadPlaylistToTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reLoadPlaylistToTable
        List<PlaylistItem> playlist = null;
        try {
            playlist = Static.getPlaylist(pathToDataFolder);
        } catch (IOException ex) {
            Static.showErrorMessageBox(Static.formatExceptionMessage(ex));
            return;
        }
        if (playlist == null || playlist.isEmpty()) {
            Static.showErrorMessageBox("В каталоге 'data' нет данных или они неверно сгруппированы.");
        } else {
            playlistTableModel.loadData(playlist);    
        }        
    }//GEN-LAST:event_reLoadPlaylistToTable

    private void btnTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestActionPerformed
        final int[] data = Static.parseData(testTextArea.getText());
        if (data.length == 0) {
            Static.debug("No data to test writing");
            return;
        }
        
        MelissaSerialPortWriter.ONE.init(getSelectedSerialPort(), getBytesInPacket(), getMillisDelayBetweenPackets(), getUDelayBetweenBits(), isInverseBits(), MainFrame.this);
        try {
            MelissaSerialPortWriter.ONE.testWriteBytes(data);
        } catch (SerialPortException spe) {
            Static.showErrorMessageBox(Static.formatExceptionMessage(spe));
        }
    }//GEN-LAST:event_btnTestActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFindAllSerialPorts;
    private javax.swing.JButton btnRefreshPlaylist;
    private javax.swing.JButton btnSaveSettings;
    private javax.swing.JButton btnStartStopSystem;
    private javax.swing.JButton btnTest;
    private javax.swing.JCheckBox chkBitsInverse;
    private javax.swing.JCheckBox chkPlayImmediately;
    private javax.swing.JComboBox comboSerialPorts;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelPlaylist;
    private javax.swing.JLabel labelScheduler;
    private javax.swing.JPanel panelDelays;
    private javax.swing.JPanel panelExtraDelaysSettings;
    private javax.swing.JPanel panelMainButtons;
    private javax.swing.JPanel panelPlaylist;
    private javax.swing.JPanel panelPortAndDataExtraSettings;
    private javax.swing.JPanel panelSerialPorts;
    private javax.swing.JPanel panelSettings;
    private javax.swing.JPanel panelStub;
    private javax.swing.JPanel panelTestWriting;
    private javax.swing.JScrollPane scrollPanePlaylist;
    private javax.swing.JSpinner spinnerBytesInPacket;
    private javax.swing.JSpinner spinnerMillisDelayBetweenPackets;
    private javax.swing.JSpinner spinnerSecDelayBetweenSongs;
    private javax.swing.JSpinner spinnerUDelayBetweenBits;
    private javax.swing.JTextArea stubTextArea;
    private javax.swing.JTable tablePlaylist;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void windowClosing(WindowEvent e) {
        if (isBusy) return;
        if (Static.showClosingDialog()) {
            PropertiesManager.ONE.storeProperties();
            System.exit(0);
        }
    }

    private void setUiEnabled(boolean isEnabled) {
        isBusy = !isEnabled;
        testTextArea.setEditable(isEnabled); //little fucking workaround
        Static.setUiEnabled(getContentPane(), isEnabled);
    }
    
    private void setVisibleExtraSettings(boolean flag) {
        panelExtraDelaysSettings.setVisible(flag);
        panelPortAndDataExtraSettings.setVisible(flag);
        panelTestWriting.setVisible(flag);
        isVisibleExtraSettings = flag;
    }
    
    private boolean isInverseBits() {
        return chkBitsInverse.isSelected();
    }
    
    private String getSelectedSerialPort() {
        return (String) comboSerialPorts.getSelectedItem();
    }
    
    private int getBytesInPacket() {
        return (Integer)spinnerBytesInPacket.getValue();
    }
    
    private int getMillisDelayBetweenPackets() {
        return (Integer)spinnerMillisDelayBetweenPackets.getValue();
    }
    
    private int getSecDelayBetweenSongs() {
        return (Integer)spinnerSecDelayBetweenSongs.getValue();
    }
    
    private int getUDelayBetweenBits() {
        return (Integer)spinnerUDelayBetweenBits.getValue();
    }

    // MusicalBoxListener
    @Override
    public void errorOccurred(final String errorMessage) {
        MelissaAlarmClock.ONE.turnOff();
        setUiEnabled(true);
        btnStartStopSystem.setText("СТАРТ");
        Static.setTrayIconStopped(trayIcon);
        Static.showErrorMessageBox(errorMessage);
    }
    
    @Override
    public void playlistItemEnded(int itemIndex) {
        playlistTableModel.clearNowPlaying(itemIndex);
    }

    @Override
    public void playlistItemStarted(int itemIndex, String songName) {
        playlistTableModel.setNowPlaying(itemIndex);
        Static.debug("playlistItemStarted: " + songName);
    }
    
    //AlarmClockListener
    @Override
    public void alarmStartHandled() {
        Static.setTrayIconActive(trayIcon);
    }

    @Override
    public void alarmEndHandled() {
        Static.setTrayIconWaiting(trayIcon);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Unused window listener`s methods">
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    //</editor-fold>
}