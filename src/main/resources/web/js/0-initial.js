///////////////
// CONSTANTS //
///////////////
const INTERVAL_GET_SYS_DATE_TIME = 5000;
const INTERVAL_GET_CURR_PLAYING = 5000;
const DEVICES = [
    {techName: 'fountain', withAlarms: true},
    {techName: 'sound', withAlarms: true},
    {techName: 'light', withAlarms: true},
    {techName: 'auxGpio1', withAlarms: false},
    {techName: 'auxGpio2', withAlarms: false},
    {techName: 'auxGpio3', withAlarms: false},
    {techName: 'auxGpio4', withAlarms: false},
    {techName: 'auxGpio5', withAlarms: false},
    {techName: 'auxGpio6', withAlarms: false}
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
