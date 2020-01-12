package com.e3k.fountain.webcontrol.uart;

import com.e3k.fountain.webcontrol.Initializable;
import com.e3k.fountain.webcontrol.CommonUtils;
import com.e3k.fountain.webcontrol.config.PropertiesManager;
import com.e3k.fountain.webcontrol.config.UmfBulbConfig;
import com.e3k.fountain.webcontrol.constant.BulbState;
import com.e3k.fountain.webcontrol.io.SoundDevice;
import com.e3k.fountain.webcontrol.notification.NotificationSender;
import com.e3k.fountain.webcontrol.uart.UartMessage;
import com.pi4j.io.serial.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public enum UartDevice implements Initializable {

    ONE;

    private final Lock handleEventLock = new ReentrantLock();
    private final BulbState[] bulbStates = new BulbState[16];
    private List<UmfBulbConfig> umfBulbs;
    private volatile boolean curAllowSound;

    private Serial serial;

    @Override
    public synchronized void init() throws IOException, InterruptedException {
        if (CommonUtils.isRaspberry()) {

            curAllowSound = true;
            umfBulbs = PropertiesManager.ONE.getAllUmfBulbInfo();

            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
            // NOTE: this utility method will determine the default serial port for the
            //       detected platform and board/model.  For all Raspberry Pi models
            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
            //       environment configuration.

            log.info("Initializing UART communication channel");

            this.serial = SerialFactory.createInstance();
            final SerialConfig config = new SerialConfig()
                    .device(SerialPort.getDefaultPort())
                    .baud(PropertiesManager.ONE.getUmfBaudRate())
                    .dataBits(PropertiesManager.ONE.getUmfDataBits())
                    .parity(PropertiesManager.ONE.getUmfParity())
                    .stopBits(PropertiesManager.ONE.getUmfStopBits())
                    .flowControl(PropertiesManager.ONE.getUmfFlowControl());
            serial.addListener(this::handleEvent);
            serial.open(config);
        }
    }

    public BulbState[] getBulbStates() {
        return this.bulbStates.clone(); //TODO not clone?
    }

    private void handleEvent(SerialDataEvent event) {
        try {
            byte[] data = event.getBytes();
            if (data == null || data.length < 8 || data[0] != 'M' || data[1] != 'V' || data[2] != 'K') {
                log.error("Corrupted HEADER. Ignoring event");
                return;
            }

//            if (log.isDebugEnabled()) {
//                log.debug("Received new Bulbs state: data[3]={}, data[4]={}",
//                        Integer.toBinaryString(data[3]), Integer.toBinaryString(data[4]));
//            }

            BulbState[] newBulbStates = extractBulbStatesFromEventData(data);
            boolean newAllowSound = CommonUtils.isBitUp(data[5], 7);
            if (newAllowSound != curAllowSound) {
                SoundDevice.ONE.allowExternally(newAllowSound);
                curAllowSound = newAllowSound;
            }

            handleEventLock.lock();
            sendChangedBulbsNotification(newBulbStates);

            // save new bulb states
            System.arraycopy(newBulbStates, 0, bulbStates, 0, 16);
            // send response message
            serial.write(UartMessage.ONE.getBytes());
        }
        catch (Exception e) {
            log.error("Error during UART event processing", e);
        }
        finally {
            handleEventLock.unlock();
        }
    }

    private static BulbState[] extractBulbStatesFromEventData(byte[] data) {
        BulbState[] newBulbStates = new BulbState[16];
        int bulbIdx = 0;
        byte dataByte = data[3];
        do {
            BulbState bulbState = (dataByte & 0x01) == 1 ? BulbState.red : BulbState.grn;
            newBulbStates[bulbIdx++] = bulbState;
            dataByte >>>= 1;
            if (bulbIdx == 8) {
                dataByte = data[4];
            }
        } while (bulbIdx < 16);
        return newBulbStates;
    }

    private void sendChangedBulbsNotification(BulbState[] newBulbStates) {
        Map<String, BulbState> changedBulbsData = null;
        boolean atLeastOneNotifiableChanged = false;
        for (int i = 0; i < newBulbStates.length; i++) {
            final BulbState oldBulbState = this.bulbStates[i];
            final BulbState newBulbState = newBulbStates[i];
            final UmfBulbConfig bulbCfg = umfBulbs.get(i);
            if (bulbCfg.isNotifyOnChange()) {
                if (changedBulbsData == null) {
                    changedBulbsData = new LinkedHashMap<>(16);
                }
                changedBulbsData.put(bulbCfg.getFullLabel(), newBulbState);
                if (oldBulbState != newBulbState) {
                    atLeastOneNotifiableChanged = true;
                }
            }
        }
        if (atLeastOneNotifiableChanged) {
            NotificationSender.ONE.sendNotification(changedBulbsData);
        }
    }
//
//    public static void main(String[] args) {
//        byte[] data = new byte[] {'M', 'V', 'K', 5, 10};
//        BulbState[] newBulbStates = new BulbState[16];
//        int bulbStatesIdx = 0;
//        byte dataByte = data[3];
//        do {
//            newBulbStates[bulbStatesIdx++] = (dataByte & 0x01) == 1 ? BulbState.red : BulbState.grn;
//            dataByte >>>= 1;
//            if (bulbStatesIdx == 8) {
//                dataByte = data[4];
//            }
//        } while (bulbStatesIdx < 16);
//        System.out.println(toBinStr(data[3]) + " " + toBinStr(data[4]) );
//        System.out.println(Arrays.toString(newBulbStates));
//    }
//
//    static String toBinStr(int i) {
//        return String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
//    }

//    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//        byte data3 = (byte) new Random().nextInt(256);
//        byte data4 = (byte) new Random().nextInt(256);
//        byte[] data = new byte[] {'M', 'V', 'K', data3, data4, 0, 0, 0};
//        log.info("Sending event with data: "
//                + new StringBuilder(Integer.toBinaryString(0xff & data3)).reverse()
//                + "_"
//                + new StringBuilder(Integer.toBinaryString(0xff & data4)).reverse());
//        handleEvent(new SerialDataEvent(serial, data));
//    }, 1000 * 3, 250, TimeUnit.MILLISECONDS);

}
