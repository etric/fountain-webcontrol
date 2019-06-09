///////////////
// CONSTANTS //
///////////////
const INTERVAL_GET_SYS_DATE_TIME = 5000;
const INTERVAL_GET_CURR_PLAYING = 5000;
const DEVICES = [
    {userTitle: 'ФОНТАН', techName: 'fountain', withAlarms: true},
    {userTitle: 'ЗВУК', techName: 'sound', withAlarms: true},
    {userTitle: 'СВЕТ', techName: 'light', withAlarms: true},
    {userTitle: 'AUX GPIO 1', techName: 'auxGpio1', withAlarms: false},
    {userTitle: 'AUX GPIO 2', techName: 'auxGpio2', withAlarms: false},
    {userTitle: 'AUX GPIO 3', techName: 'auxGpio3', withAlarms: false},
    {userTitle: 'AUX GPIO 4', techName: 'auxGpio4', withAlarms: false},
    {userTitle: 'AUX GPIO 5', techName: 'auxGpio5', withAlarms: false},
    {userTitle: 'AUX GPIO 6', techName: 'auxGpio6', withAlarms: false}
];
const DEVICES_WITH_ALARMS = DEVICES.filter(i => i.withAlarms);

///////////
// UTILS //
///////////
let eng2rus = (device) => {
    if (device.startsWith('fountain')) {
        return 'фонтан';
    } else if (device.startsWith('light')) {
        return 'свет';
    } else if (device.startsWith('sound')) {
        return 'звук';
    }
    return device;
};
let isTabletWidth = () => {
    let cssDisplay = $('#tablet-indicator').css('display');
    return cssDisplay === 'block';
};
let alarmDayByAlarmName = (alarmName) =>{
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


//TODO CAUTION!!! double check all usages of arrow function!!!
