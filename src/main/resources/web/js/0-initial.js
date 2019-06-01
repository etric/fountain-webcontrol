///////////////
// CONSTANTS //
///////////////
let INTERVAL_GET_SYS_DATE_TIME = 5000;
let INTERVAL_GET_CURR_PLAYING = 5000;
let DEVICES = [
    {userTitle: 'ФОНТАН', techName: 'fountain'},
    {userTitle: 'ЗВУК', techName: 'sound'},
    {userTitle: 'СВЕТ', techName: 'light'},
    {userTitle: 'AUX GPIO 1', techName: 'auxGpio1'},
    {userTitle: 'AUX GPIO 2', techName: 'auxGpio2'},
    {userTitle: 'AUX GPIO 3', techName: 'auxGpio3'},
    {userTitle: 'AUX GPIO 4', techName: 'auxGpio4'},
    {userTitle: 'AUX GPIO 5', techName: 'auxGpio5'},
    {userTitle: 'AUX GPIO 6', techName: 'auxGpio6'}
];

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
