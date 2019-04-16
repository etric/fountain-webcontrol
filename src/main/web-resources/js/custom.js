$('.custom-file-input').on('change', function () {
    var fileName = $(this).val().split('\\').pop();
    $(this).siblings('.custom-file-label').addClass('selected').html(fileName);
});

function initAlarmTimePicker(selector) {
    return new Picker(document.querySelector(selector), {
        format: 'HH:mm',
        controls: true,
        increment: {
            hour: 1,
            minute: 5
        },
        // inline: true,
        headers: true,
        language: 'ru',
        text: {
            hour: 'Часы',
            minute: 'Минуты',
            title: 'Начало будильника',
            cancel: 'Отмена',
            confirm: 'ОК'
        }
    });
}

var fountainAlarmStartPicker = {};
var fountainAlarmEndPicker = {};
var lightAlarmStartPicker = {};
var lightAlarmEndPicker = {};
var soundAlarmStartPicker = {};
var soundAlarmEndPicker = {};

toastr.options = {
    "debug": false,
    "positionClass": "toast-bottom-left",
    "onclick": null,
    "fadeIn": 300,
    "fadeOut": 1000,
    "timeOut": 1000
};

$('#upload-music-file').on('click', function () {
    var musicFileChooser = $('#music-file-chooser');
    var musicNumSelector = $('#music-num-selector');
    var fileName = musicFileChooser.val();
    if (!fileName) {
        toastr.warning('Выберите мелодию!');
        return;
    }
    fileName = fileName.split('\\').pop();
    var confirmationText = "Загрузить новую мелодию для <b>#" + musicNumSelector.val() + ": " + fileName + "</b>?";
    bootbox.confirm(confirmationText, function (result) {
        if (result === true) {
            var musicFileFormData = new FormData();
            var musicFile = musicFileChooser[0].files[0];
            musicFileFormData.append('file', musicFile);
            // console.log('musicFile = ' + musicFile);
            // console.log('Uploading ' + fileName + '...');
            $.ajax({
                type: "PUT",
                url: '/api/music/' + musicNumSelector.val(),
                data: musicFileFormData,
                // dataType: 'json',
                contentType: false,
                processData: false,
                success: function (response) {
                    toastr.success('Мелодия ' + fileName + ' загружена!');
                    //TODO update playlist
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    toastr.error('Мелодия ' + fileName + ' не загружена: ' + textStatus);
                    console.log(textStatus, errorThrown);
                }
            });
            musicFileChooser.val('');
        }
    });
});

// $('#save-settings').on('click', function () {
//
//     var settingsData = {};
//     settingsData.alarmStart = $('#alarmStart').val();
//     settingsData.alarmEnd = $('#alarmEnd').val();
//     settingsData.delay = $('#delay').val();
//     settingsData.volume = $('#volumeSlider').val();
//
//     $.ajax({
//         type: "PUT",
//         url: '/api/settings',
//         data: settingsData,
//         dataType: 'json',
//         success: function (response) {
//             console.log('response: ' + response);
//             toastr.success('Настройки сохранены!');
//         }
//     });
// });

$('#controlMode').bootstrapToggle('off');
$('#fountainState').bootstrapToggle('off');
$('#lightState').bootstrapToggle('off');
$('#soundState').bootstrapToggle('off');

function changeFountainControlMode(autoMode) {
    $('#fountainState').bootstrapToggle(autoMode ? 'disable' : 'enable').parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
    $('#fountainAlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
    $('#fountainAlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
    if (autoMode) {
        fountainAlarmStartPicker = initAlarmTimePicker('#fountainAlarmStart');
        fountainAlarmEndPicker = initAlarmTimePicker('#fountainAlarmEnd');
    } else if (fountainAlarmStartPicker.destroy && fountainAlarmEndPicker.destroy) {
        fountainAlarmStartPicker.destroy();
        fountainAlarmEndPicker.destroy();
    }
}

function changeLightControlMode(autoMode) {
    $('#lightState').bootstrapToggle(autoMode ? 'disable' : 'enable').parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
    $('#lightAlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
    $('#lightAlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
    if (autoMode) {
        lightAlarmStartPicker = initAlarmTimePicker('#lightAlarmStart');
        lightAlarmEndPicker = initAlarmTimePicker('#lightAlarmEnd');
    } else if (lightAlarmStartPicker.destroy && lightAlarmEndPicker.destroy) {
        lightAlarmStartPicker.destroy();
        lightAlarmEndPicker.destroy();
    }
}

function changeSoundControlMode(autoMode) {
    $('#soundState').bootstrapToggle(autoMode ? 'disable' : 'enable').parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
    $('#soundAlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
    $('#soundAlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
    if (autoMode) {
        soundAlarmStartPicker = initAlarmTimePicker('#soundAlarmStart');
        soundAlarmEndPicker = initAlarmTimePicker('#soundAlarmEnd');
    } else if (soundAlarmStartPicker.destroy && soundAlarmEndPicker.destroy) {
        soundAlarmStartPicker.destroy();
        soundAlarmEndPicker.destroy();
    }
}

var cancelChangeControlMode = false;
$('#controlMode').change(function () {
    var controlModeEl = $(this);
    if (cancelChangeControlMode === true) {
        cancelChangeControlMode = false;
        console.log('Ignoring #controlMode CHANGE event.. ');
        return;
    }
    var autoMode = $(this).prop('checked');
    var confirmationText = "Сменить режим на <b>" + (autoMode ? 'АВТО' : 'РУЧНОЙ') + "</b>?";
    bootbox.confirm(confirmationText, function (result) {
        if (result === true) {
            console.log('CONTROL MODE CHANGED: AUTO? ' + autoMode);
            changeFountainControlMode(autoMode);
            changeLightControlMode(autoMode);
            changeSoundControlMode(autoMode);
            $.ajax({
                type: "PUT",
                url: '/api/mode/' + (autoMode ? 'auto' : 'manual'),
                // data: {},
                // dataType: 'json',
                success: function (response) {
                    console.log('response: ' + response);
                    toastr.success('Режим ' + (autoMode ? 'АВТО' : 'РУЧНОЙ') + ' установлен!');
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(textStatus, errorThrown);
                    toastr.error('Режим ' + (autoMode ? 'АВТО' : 'РУЧНОЙ') + ' не установлен: ' + textStatus);
                }
            });
        } else {
            cancelChangeControlMode = true;
            controlModeEl.bootstrapToggle('toggle');
        }
    });
});

//
//     .change(function () {
//     var controlModeEl = $(this);
//     if (cancelChangeControlMode === true) {
//         cancelChangeControlMode = false;
//         console.log('Ignoring #controlMode CHANGE event.. ')
//         return;
//     }
//     var autoMode = $(this).prop('checked');
//     var confirmationText = "Сменить режим на <b>" + (autoMode ? 'АВТО' : 'РУЧНОЙ') + "</b>?";
//     bootbox.confirm(confirmationText, function (result) {
//         if (result === true) {
//             console.log('CONTROL MODE CHANGED: AUTO? ' + autoMode);
//             changeFountainControlMode(autoMode);
//             changeLightControlMode(autoMode);
//             changeSoundControlMode(autoMode);
//         } else {
//             cancelChangeControlMode = true;
//             controlModeEl.bootstrapToggle('toggle');
//         }
//     });
// });

$('#fountainState').change(function () {
    var fountainOn = $(this).prop('checked') ? 'on' : 'off';
    console.log('FOUNTAIN STATE CHANGED: ' + fountainOn);
    $.ajax({
        type: "PUT",
        url: '/api/fountain/' + fountainOn,
        // data: {},
        // dataType: 'json',
        success: function (response) {
            console.log('response: ' + response);
            toastr.success('Состояние фонтана изменено!');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            toastr.error('Состояние фонтана не изменено: ' + textStatus);
            console.log(textStatus, errorThrown);
        }
    });
});

$('#lightState').change(function () {
    var lightOn = $(this).prop('checked') ? 'on' : 'off';
    console.log('LIGHT STATE CHANGED: ' + lightOn);
    $.ajax({
        type: "PUT",
        url: '/api/light/' + lightOn,
        // data: {},
        // dataType: 'json',
        success: function (response) {
            console.log('response: ' + response);
            toastr.success('Состояние света изменено!');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            toastr.error('Состояние света не изменено: ' + textStatus);
            console.log(textStatus, errorThrown);
        }
    });
});

$('#soundState').change(function () {
    var soundOn = $(this).prop('checked') ? 'on' : 'off';
    console.log('SOUND STATE CHANGED: ' + soundOn);
    $.ajax({
        type: "PUT",
        url: '/api/sound/' + soundOn,
        // data: {},
        // dataType: 'json',
        success: function (response) {
            console.log('response: ' + response);
            toastr.success('Состояние звука изменено!');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            toastr.error('Состояние звука не изменено: ' + textStatus);
            console.log(textStatus, errorThrown);
        }
    });
});

function parseTime( t ) {
    var d = new Date();
    var time = t.match( /(\d+)(?::(\d\d))?\s*(p?)/ );
    d.setHours( parseInt( time[1]) + (time[3] ? 12 : 0) );
    d.setMinutes( parseInt( time[2]) || 0 );
    return d;
}

function alarmChanged() {
    var alarmName = $(this).attr('id');
    var newValue = $(this).val();

    if (alarmPreviousValues[alarmName] === newValue) {
        //nothing changed
        return;
    }
    var fromTime, toTime;
    if (alarmName.endsWith('End')) {
        toTime = parseTime(newValue);
        var startAlarmId = alarmName.replace('End', 'Start');
        var startAlarmValue = $('#' + startAlarmId).val();
        fromTime = parseTime(startAlarmValue);
    } else if (alarmName.endsWith('Start')) {
        fromTime = parseTime(newValue);
        var endAlarmId = alarmName.replace('Start', 'End');
        var endAlarmValue =  $('#' + endAlarmId).val();
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
        $(this).val(alarmPreviousValues[alarmName]);
        //TODO revert to old value!
        return;
    }

    //TODO validate BEFORE/AFTER
    console.log('Updating ' + alarmName + ' with value ' + newValue);
    var localAlarmName;
    if (alarmName.startsWith('fountain')) {
        localAlarmName = 'Фонтан';
    } else if (alarmName.startsWith('light')) {
        localAlarmName = 'свет';
    } else if (alarmName.startsWith('sound')) {
        localAlarmName = 'звук';
    }
    $.ajax({
        type: "PUT",
        url: '/api/alarm/' + alarmName,
        data: newValue,
        dataType: 'text',
        success: function (response) {
            console.log('response: ' + response);
            toastr.success('Расписаниее для ' + localAlarmName + 'а изменено!');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            toastr.error('Расписаниее для ' + localAlarmName + 'а не изменено: ' + textStatus);
            console.log(textStatus, errorThrown);
            $(this).val(alarmPreviousValues[alarmName]);
        }
    });
}

var alarmPreviousValues = {
    'fountainAlarmStart' : $('#fountainAlarmStart').val(),
    'fountainAlarmEnd' : $('#fountainAlarmEnd').val(),
    'soundAlarmStart' : $('#soundAlarmStart').val(),
    'soundAlarmEnd' : $('#soundAlarmEnd').val(),
    'lightAlarmStart' : $('#lightAlarmStart').val(),
    'lightAlarmEnd' : $('#lightAlarmEnd').val(),
};

$('#fountainAlarmStart').change(alarmChanged);
$('#fountainAlarmEnd').change(alarmChanged);
$('#soundAlarmStart').change(alarmChanged);
$('#soundAlarmEnd').change(alarmChanged);
$('#lightAlarmStart').change(alarmChanged);
$('#lightAlarmEnd').change(alarmChanged);

function volumeChanged() {
    var newVolume = $('#volumeSlider').val();
    console.log('VOLUME CHANGED: ' + newVolume);
    $.ajax({
        type: "PUT",
        url: '/api/volume/' + newVolume,
        // data: {},
        // dataType: 'json',
        success: function (response) {
            console.log('response: ' + response);
            toastr.success('Громкость изменена!');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            toastr.error('Громкость не изменена: ' + textStatus);
            console.log(textStatus, errorThrown);
        }
    });
}

changeLightControlMode(false);
changeFountainControlMode(false);
changeSoundControlMode(false);

// $(function () {
//     $('#myTab li:last-child a').tab('show')
// });
//
