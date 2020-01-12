////////////////////////
// ALARM TIME PICKERS //
////////////////////////

let initAlarmControls = (soundDevicesEnabled) => {

    let alarmsWeekData = {};
    let alarmPreviousValues = {};

    let parseTime = (t) => {
        let d = new Date();
        let time = t.match(/(\d+)(?::(\d\d))?\s*(p?)/);
        d.setHours(parseInt(time[1]) + (time[3] ? 12 : 0));
        d.setMinutes(parseInt(time[2]) || 0);
        return d;
    };

    let initAlarmTimePicker = (deviceTechName, alarmType) => {

        // The hack to forbid updating alarms if device state is off
        $('#' + alarmType).on('focus click', e => {
            let deviceStateOn = $('#' + deviceTechName + 'State').prop('checked');
            if (deviceStateOn) {
                // e.preventDefault();
                // e.stopPropagation();
                e.stopImmediatePropagation();
                e.target.blur();
                // $('#blur-hack').focus();
            }
        });

        let picker = new Picker(document.querySelector('#' + alarmType), {
            language: 'ru', headers: true, controls: true, format: 'HH:mm', increment: {hour: 1, minute: 5},
            text: {hour: 'Часы', minute: 'Минуты', title: 'Начало будильника', cancel: 'Отмена', confirm: 'ОК'}
        });
        $.ajax({
            type: "GET",
            url: '/api/alarm/' + alarmType,
            success: (response) => {
                console.log('INIT: ' + alarmType + ': ' + response);
                let data = JSON.parse(response);
                alarmsWeekData[alarmType] = data;
                let alarmDay = alarmDayByAlarmName(alarmType);
                picker.setValue(data[alarmDay]);
                alarmPreviousValues[alarmType] = data;
            },
            error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
        });
        return picker;
    };

    let alarmChanged = function() {
        let prev = $(this).data('value');
        let current = $(this).val();
        console.log('CHANGE ALARM: OLD=' + prev + ', NEW=' + current);

        let alarmName = $(this).attr('id');
        let newValue = $(this).val();
        let alarmDay = alarmDayByAlarmName(alarmName);

        if (alarmPreviousValues[alarmName][alarmDay] === newValue) {
            //nothing changed
            return;
        }
        let fromTime, toTime;
        if (alarmName.endsWith('End')) {
            toTime = parseTime(newValue);
            let startAlarmId = alarmName.replace('End', 'Start');
            let startAlarmValue = $('#' + startAlarmId).val();
            fromTime = parseTime(startAlarmValue);
        } else if (alarmName.endsWith('Start')) {
            fromTime = parseTime(newValue);
            let endAlarmId = alarmName.replace('Start', 'End');
            let endAlarmValue = $('#' + endAlarmId).val();
            toTime = parseTime(endAlarmValue);
        } else {
            //effectively unreachable
            toastr.error('Что-то поломано..');
            return;
        }
        if (!fromTime || !toTime) {
            toastr.error('Некорректно задано время!');
            return;
        }
        if (fromTime >= toTime) {
            toastr.warning('Время Начала должно быть раньше Конца!');
            $(this).val(alarmPreviousValues[alarmName][alarmDay]);
            return;
        }
        console.log('Updating ' + alarmName + '/' + alarmDay + ' with value ' + newValue);
        let localAlarmName = eng2rus(alarmName);
        $.ajax({
            type: "PUT",
            url: '/api/alarm/' + alarmName + '/' + alarmDay,
            data: newValue,
            dataType: 'text',
            success: function (response) {
                alarmsWeekData[alarmName][alarmDay] = current;
                toastr.success('Расписаниее для ' + localAlarmName + 'а изменено!');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                toastr.error('Расписаниее для ' + localAlarmName + 'а не изменено: ' + jqXHR.responseText);
                $(this).val(alarmPreviousValues[alarmName][alarmDay]);
            }
        });
    };


    let alarmDayChanged = function() {
        let selectedAlarmDay = $(this).val();
        let deviceName = $(this).attr('id').replace('AlarmDay', '');
        let alarmStartType = deviceName + 'AlarmStart';
        let alarmEndType = deviceName + 'AlarmEnd';
        $('#' + alarmStartType).val(alarmsWeekData[alarmStartType][selectedAlarmDay]);
        $('#' + alarmEndType).val(alarmsWeekData[alarmEndType][selectedAlarmDay]);
    };

    getDevices(true, soundDevicesEnabled).forEach(deviceInfo => {
        let alarmStartType = deviceInfo.techName + 'AlarmStart';
        let alarmEndType = deviceInfo.techName + 'AlarmEnd';
        let alarmDay = deviceInfo.techName + 'AlarmDay';
        let alarmStartPicker = initAlarmTimePicker(deviceInfo.techName, alarmStartType);
        let alarmEndPicker = initAlarmTimePicker(deviceInfo.techName, alarmEndType);
        $('#' + alarmStartType).change(alarmChanged);
        $('#' + alarmEndType).change(alarmChanged);
        $('#' + alarmDay).change(alarmDayChanged);
    });

};
