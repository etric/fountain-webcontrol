package com.e3k.fountain.webcontrol.io;

import com.e3k.fountain.webcontrol.Utils;
import com.e3k.fountain.webcontrol.io.player.MusicPlayerListener;

import javax.swing.SwingUtilities;

import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import jssc.SerialPort;
import jssc.SerialPortException;

@Deprecated
public enum MelissaSerialPortWriter
//        implements Runnable
{
    
    ONE;
//
//    private SerialPort serialPort;
//    private int millisDelayBetweenPackets;
//    private int uDelayBetweenBits;
//    private int bytesInPacket;
//    private boolean inverseBits;
//    private MusicPlayerListener listener;
//    private final Thread writingThread;
//    private BasicPlayerListener pel;
//
//    private volatile int[] bytes2write = null;
//
//    private MelissaSerialPortWriter() {
//        writingThread = new Thread(this);
//        writingThread.start();
//    }
//
//
//    public void init(
//            String serialPortName,
//            int bytesInPacket,
//            int millisDelayBetweenPackets,
//            int uDelayBetweenBits,
//            boolean inverseBits,
//            MusicPlayerListener listener) {
//
//        if (serialPort != null) {
//            tryClosePort();
//            if (!serialPort.getPortName().equals(serialPortName)) {
//                serialPort = new SerialPort(serialPortName);
//            }
//        } else {
//            serialPort = new SerialPort(serialPortName);
//        }
//        this.listener = listener;
//        this.millisDelayBetweenPackets = millisDelayBetweenPackets;
//        this.bytesInPacket = bytesInPacket;
//        this.inverseBits = inverseBits;
//        this.uDelayBetweenBits = uDelayBetweenBits;
//    }
//
//    private void writeBytes() throws SerialPortException {
//        reset();
//        if (1==1) return;
//        try {
//            int bytesWithinPacketWritten = 0;
//            for (int i = 0; i < bytes2write.length; i++) {
//                for (int k = 0x0001; k <= 0x0080; k <<= 1) {
//                    if (bytes2write == null) return;
//                    boolean bit = inverseBits ^ ((bytes2write[i] & k) != 0);
//                    /* data bit */
//                    if (bit) {
//                        serialPort.setDTR(false);
//                    } else {
//                        serialPort.setRTS(false);
//                    }
//                    uDelay();
//                    /* strobe */
//                    serialPort.setDTR(true);
//                    serialPort.setRTS(true);
//                    uDelay();
//                }
//
//                if (++bytesWithinPacketWritten == bytesInPacket) {
//                    Utils.trySleep(millisDelayBetweenPackets);
//                    bytesWithinPacketWritten = 0;
//                }
//            }
//            bytes2write = null;
//            if (pel != null) {
//                pel.stateUpdated(new BasicPlayerEvent(null, BasicPlayerEvent.EOM, 0, 0, null));
//                pel = null;
//            }
//        } catch (NullPointerException npe) {
//            // ignore
//        }
//    }
//
//    public synchronized void writeBytes(int[] data, BasicPlayerListener pel)  {
//        bytes2write = data;
//        this.pel = pel;
//        notify();
//    }
//
//    public void stopWriting() {
//        bytes2write = null;
//    }
//
//    private void reset() {
//        if (serialPort == null) return;
//        try {
//            if (!serialPort.isOpened()) {
//                serialPort.openPort();
//                Utils.trySleep(1);
//            }
//
//            /* reset bit */
//            serialPort.setDTR(false);
//            serialPort.setRTS(false);
//            uDelay();
//            /* strobe */
//            serialPort.setDTR(true);
//            serialPort.setRTS(true);
//            uDelay();
//        } catch (SerialPortException spe) {}
//    }
//
//    public void tryClosePort() {
//        try {
//            if (serialPort != null && serialPort.isOpened()) {
//                serialPort.closePort();
//            }
//        } catch (SerialPortException spe) {
//            Utils.error(spe);
//        }
//    }
//
//    @Override
//    public synchronized void run() {
//        while (true) {
//            try {
//                if (bytes2write == null) {
//                    reset();
//                    wait();
//                } else writeBytes();
//            } catch (final Exception e) {
//                e.printStackTrace();
//                bytes2write = null;
//                pel = null;
//                SwingUtilities.invokeLater(() -> listener.errorOccurred(Utils.formatExceptionMessage(e)));
//            }
//        }
//    }
//
//    /////////////////////////////////////////////////////////////////////////
//
//    public synchronized void testWriteBytes(int[] data) throws SerialPortException {
//        if (data == null) {
//            return;
//        }
//        if (!serialPort.isOpened()) {
//            serialPort.openPort();
//        }
//
//        Utils.trySleep(1);
//
//        /* reset bit */
//        serialPort.setDTR(false);
//        serialPort.setRTS(false);
//        uDelay();
//        /* strobe */
//        serialPort.setDTR(true);
//        serialPort.setRTS(true);
//        uDelay();
//
//        int bytesWithinPacketWritten = 0;
//        for (int i = 0; i < data.length; i++) {
//            for (int k = 0x0001; k <= 0x0080; k <<= 1) {
//                boolean bit = inverseBits ^ ((data[i] & k) != 0);
//                /* data bit */
//                if (bit) {
//                    serialPort.setDTR(false);
//                } else {
//                    serialPort.setRTS(false);
//                }
//                uDelay();
//                /* strobe */
//                serialPort.setDTR(true);
//                serialPort.setRTS(true);
//                uDelay();
//
////                System.out.print(bit ? "1" : "0");
//            }
////            System.out.print("\t");
//            if (++bytesWithinPacketWritten == bytesInPacket) {
//                Utils.trySleep(millisDelayBetweenPackets);
//                bytesWithinPacketWritten = 0;
//            }
////            System.out.println();
//        }
//    }
//
//    private void uDelay() {
//        for (int z = 0; z < uDelayBetweenBits * 1000; z++) {
//            // do nothing
//        }
//    }
}