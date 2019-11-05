///////////////
// CONSTANTS //
///////////////
const INTERVAL_GET_SYS_DATE_TIME = 5000;
const INTERVAL_GET_CURR_PLAYING = 5000;
const INTERVAL_GET_BULB_STATES = 500;

const ALL_DEVICES = [
    {techName: 'fountain', withAlarms: true},
    {techName: 'sound', withAlarms: true},
    {techName: 'light', withAlarms: true}
];
const PAGES = [
    {techName: 'umf', secured: true},
    {techName: 'settings', secured: true},
    {techName: 'music', secured: false}
];

const getDevices = (withAlarmsOnly, soundDevicesEnabled) => {
    return ALL_DEVICES.filter(i => {
        if (withAlarmsOnly && i.withAlarms === false) {
            return false;
        }
        if (soundDevicesEnabled === false && i.techName === 'sound') {
            return false;
        }
        return true;
    });
};

///////////
// UTILS //
///////////
const eng2rus = (device) => {
    if (device.startsWith('fountain')) {
        return 'фонтан';
    } else if (device.startsWith('light')) {
        return 'свет';
    } else if (device.startsWith('sound')) {
        return 'звук';
    }
    return device;
};
const alarmDayByAlarmName = (alarmName) =>{
    return $('#' + alarmName.replace('AlarmStart', '').replace('AlarmEnd', '') + 'AlarmDay').val();
};


////////////
// CONFIG //
////////////
$.ajaxSetup({cache: false});
toastr.options = {
    "debug": false,
    "positionClass": "toast-bottom-left",
    "onclick": null,
    "fadeIn": 300,
    "fadeOut": 1000,
    "timeOut": 1000
};
