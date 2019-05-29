//
// UTILS
//
function eng2rus(device) {
    if (device.startsWith('fountain')) {
        return 'фонтан';
    } else if (device.startsWith('light')) {
        return 'свет';
    } else if (device.startsWith('sound')) {
        return 'звук';
    }
}
function parseTime(t) {
    var d = new Date();
    var time = t.match(/(\d+)(?::(\d\d))?\s*(p?)/);
    d.setHours(parseInt(time[1]) + (time[3] ? 12 : 0));
    d.setMinutes(parseInt(time[2]) || 0);
    return d;
}
//
// CONFIG
//
$.ajaxSetup({cache: false});
toastr.options = {
    "debug": false,
    "positionClass": "toast-bottom-left",
    "onclick": null,
    "fadeIn": 300,
    "fadeOut": 1000,
    "timeOut": 1000
};

//
// DOCUMENT READY
//

$(document).ready(function () {

    var alarmPreviousValues = {};

    /////////////////////
    // ALARM TIME PICKERS
    /////////////////////
    function initAlarmTimePicker(alarmType) {
        var picker = new Picker(document.querySelector('#' + alarmType), {
            language: 'ru', headers: true, controls: true, format: 'HH:mm', increment: {hour: 1, minute: 5},
            text: {hour: 'Часы', minute: 'Минуты', title: 'Начало будильника', cancel: 'Отмена', confirm: 'ОК'}
        });
        $.ajax({
            type: "GET",
            url: '/api/alarm/' + alarmType,
            success: function (response) {
                console.log('response: ' + response);
                picker.setValue(response);
                alarmPreviousValues[alarmType] = response;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }
        });
        return picker;
    }

    var fountainAlarmStartPicker = initAlarmTimePicker('fountainAlarmStart');
    var fountainAlarmEndPicker = initAlarmTimePicker('fountainAlarmEnd');
    var lightAlarmStartPicker = initAlarmTimePicker('lightAlarmStart');
    var lightAlarmEndPicker = initAlarmTimePicker('lightAlarmEnd');
    var soundAlarmStartPicker = initAlarmTimePicker('soundAlarmStart');
    var soundAlarmEndPicker = initAlarmTimePicker('soundAlarmEnd');

    function alarmChanged() {
        var prev = $(this).data('value');
        var current = $(this).val();
        console.log('CHANGE ALARM: OLD=' + prev + ', NEW=' + current);

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
            var endAlarmValue = $('#' + endAlarmId).val();
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
            return;
        }
        console.log('Updating ' + alarmName + ' with value ' + newValue);
        var localAlarmName = eng2rus(alarmName);
        $.ajax({
            type: "PUT",
            url: '/api/alarm/' + alarmName,
            data: newValue,
            dataType: 'text',
            success: function (response) {
                console.log('response: ' + response);
                toastr.success('Расписаниее для ' + localAlarmName + 'а изменено!');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                toastr.error('Расписаниее для ' + localAlarmName + 'а не изменено: ' + textStatus);
                console.log(textStatus, errorThrown);
                $(this).val(alarmPreviousValues[alarmName]);
            }
        });
    }

    $('#fountainAlarmStart').change(alarmChanged);
    $('#fountainAlarmEnd').change(alarmChanged);
    $('#soundAlarmStart').change(alarmChanged);
    $('#soundAlarmEnd').change(alarmChanged);
    $('#lightAlarmStart').change(alarmChanged);
    $('#lightAlarmEnd').change(alarmChanged);

    ////////////////////
    // MUSIC UPLOADER //
    ////////////////////
    //TODO declare function and bind to dom element attribute ('onchange')?
    $('.custom-file-input').on('change', function () {
        var fileName = $(this).val().split('\\').pop();
        $(this).siblings('.custom-file-label').addClass('selected').html(fileName);
    });
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
                $.ajax({
                    type: "PUT",
                    url: '/api/music/' + musicNumSelector.val(),
                    data: musicFileFormData,
                    contentType: false,
                    processData: false,
                    success: function () {
                        toastr.success('Мелодия ' + fileName + ' загружена!');
                        musicFileChooser.val('');
                        $('#pli-' + musicNumSelector.val()).text(fileName);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        toastr.error('Мелодия ' + fileName + ' не загружена: ' + textStatus);
                        console.log(textStatus, errorThrown);
                    }
                });
            }
        });
    });

    ///////////////////////////////////
    // FOUNTAIN/LIGHT/SOUND SWITCHES //
    ///////////////////////////////////
    ['fountain', 'light', 'sound'].forEach(function(deviceType) {
        var selector = '#' + deviceType + 'State';
        $.ajax({
            type: "GET",
            url: '/api/' + deviceType,
            success: function (response) {
                console.log(deviceType + ' state: ' + response);
                $(selector).bootstrapToggle(response);
                $(selector).change(function () {
                    var deviceState = $(this).prop('checked') ? 'on' : 'off';
                    console.log(deviceType + ' STATE CHANGED: ' + deviceState);
                    $.ajax({
                        type: "PUT",
                        url: '/api/' + deviceType + '/' + deviceState,
                        success: function (response) {
                            console.log('response: ' + response);
                            toastr.success('Состояние ' + eng2rus(deviceType) + 'а изменено!');
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            toastr.error('Состояние ' + eng2rus(deviceType) + 'а не изменено: ' + textStatus);
                            console.log(textStatus, errorThrown);
                        }
                    });
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }
        });
    });

    //////////////////
    // CONTROL MODE //
    //////////////////
    $.ajax({
        type: "GET",
        url: '/api/mode',
        success: function (data) {
            console.log('Initialized CONTROL_MODE: ' + data);
            var switchedOn = (data === 'auto');
            $('#controlMode').bootstrapToggle(switchedOn ? 'on' : 'off');
            changeFountainState(switchedOn);
            changeLightState(switchedOn);
            changeSoundState(switchedOn);
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
                        changeFountainState(autoMode);
                        changeLightState(autoMode);
                        changeSoundState(autoMode);
                        $.ajax({
                            type: "PUT",
                            url: '/api/mode/' + (autoMode ? 'auto' : 'manual'),
                            success: function (response) {
                                console.log('response: ' + response);
                                toastr.success('Режим ' + (autoMode ? 'АВТО' : 'РУЧНОЙ') + ' установлен!');
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
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
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });

    //////////////////////////////////////////
    // ENABLE/DISABLE SWITCH/ALARM CONTROLS //
    //////////////////////////////////////////
    function changeFountainState(autoMode) {
        $('#fountainState')
        // .bootstrapToggle(autoMode ? 'disable' : 'enable')
            .parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
        $('#fountainAlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        $('#fountainAlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        if (autoMode) {
            fountainAlarmStartPicker = initAlarmTimePicker('fountainAlarmStart');
            fountainAlarmEndPicker = initAlarmTimePicker('fountainAlarmEnd');
        } else if (fountainAlarmStartPicker && fountainAlarmEndPicker) {
            //TODO fix ERROR
            try {
                fountainAlarmStartPicker.destroy();
                fountainAlarmEndPicker.destroy();
            } catch (e) {
                console.log('changeFountainState ERROR: ' + e);
            }
        }
    }

    function changeLightState(autoMode) {
        $('#lightState')
        // .bootstrapToggle(autoMode ? 'disable' : 'enable')
            .parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
        $('#lightAlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        $('#lightAlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        if (autoMode) {
            lightAlarmStartPicker = initAlarmTimePicker('lightAlarmStart');
            lightAlarmEndPicker = initAlarmTimePicker('lightAlarmEnd');
        } else if (lightAlarmStartPicker && lightAlarmEndPicker) {
            //TODO fix ERROR
            try {
                lightAlarmStartPicker.destroy();
                lightAlarmEndPicker.destroy();
            } catch (e) {
                console.log('changeLightState ERROR: ' + e);
            }
        }
    }

    function changeSoundState(autoMode) {
        $('#soundState')
        // .bootstrapToggle(autoMode ? 'disable' : 'enable')
            .parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
        $('#soundAlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        $('#soundAlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        if (autoMode) {
            soundAlarmStartPicker = initAlarmTimePicker('soundAlarmStart');
            soundAlarmEndPicker = initAlarmTimePicker('soundAlarmEnd');
        } else if (soundAlarmStartPicker && soundAlarmEndPicker) {
            //TODO fix ERROR
            try {
                soundAlarmStartPicker.destroy();
                soundAlarmEndPicker.destroy();
            } catch (e) {
                console.log('changeSoundState ERROR: ' + e);
            }
        }
    }

    $.ajax({
        type: "GET",
        url: '/api/volume',
        success: function (response) {
            console.log('response: ' + response);
            $('#volumeSliderOut').val(response);
            $('#volumeSlider').val(response);
            $('#volumeSlider').on('change', function () {
                var newVolume = $('#volumeSlider').val();
                console.log('VOLUME CHANGED: ' + newVolume);
                $.ajax({
                    type: "PUT",
                    url: '/api/volume/' + newVolume,
                    success: function (response) {
                        console.log('response: ' + response);
                        toastr.success('Громкость изменена!');
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        toastr.error('Громкость не изменена: ' + textStatus);
                        console.log(textStatus, errorThrown);
                    }
                });
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });

    function markCurrentPlayingItem() {
        $.ajax({
            type: "GET",
            url: "/api/music/currentPlayingItem",
            success: function (index) {
                console.log('current playing: ' + index);
                $('.wave').remove();
                if (index > 0) {
                    $('.playlist #pli-' + index).parent().append('<span class="wave"></span>');
                }
            }
        });
    }

    setInterval(markCurrentPlayingItem, 5000);

    function buildPlaylist(array) {
        var playlist = $('.playlist');
        for (var i = 0; i < array.length; i++) {
            var item = array[i] || '-';
            playlist.append(
                '<li class="list-group-item d-flex justify-content-between 1h-condensed">' +
                '  <h6 id="pli-' + (i + 1) + '" class="my-0">' + item + '</h6>' +
                '</li>');
        }
    }

    function reloadPlaylist() {
        $.ajax({
            type: "GET",
            url: "/api/music/playlist",
            success: function (data) {
                console.log('playlist: ' + data);
                var array = JSON.parse(data);
                if (array) {
                    buildPlaylist(array);
                    markCurrentPlayingItem();
                }
            }
        })
    }

    reloadPlaylist();

    function isTabletWidth() {
        var cssDisplay = $('#tablet-indicator').css('display');
        return cssDisplay === 'block';
    }

    function layoutTabs() {
        if (isTabletWidth()) {
            $('#myPlaylist').addClass('fade show active');
            $('#myControls').addClass('fade show active');
        } else {
            $('#playlist-tab').addClass('active');
            $('#controls-tab').removeClass('active');
            $('#myControls').removeClass('fade show active');
        }
    }

    $(window).on("orientationchange", function () {
        setTimeout(layoutTabs, 0);
    });

    layoutTabs();
    // buildPlaylist([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]);

    /////////////////////
    // PI SYSTEM TIME ///
    /////////////////////
    (function() {
        var sysDateTimePicker = new Picker(document.querySelector('.pi-sysdatetime'), {
            controls: true,
            headers: true,
            language: 'ru',
            format: 'YYYY-MM-DD HH:mm',
            text: {
                year: 'Год', month: 'Мясяц', day: 'День', hour: 'Часы', minute: 'Минуты',
                title: 'Системное время', cancel: 'Отмена', confirm: 'ОК'
            }
        });
        $.ajax({
            type: "GET",
            url: '/api/sysdatetime',
            success: function (response) {
                console.log('response: ' + response);
                sysDateTimePicker.setValue(response);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }
        });
        $('.pi-sysdatetime').change(function() {
            var newSysTime = $(this).val();
            console.log('Updating system time to ' + newSysTime);
            $.ajax({
                type: "PUT",
                url: '/api/sysdatetime/' + newSysTime,
                success: function (response) {
                    console.log('response: ' + response);
                    toastr.success('Системное время изменено!');
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    toastr.error('Системное время не изменено: ' + textStatus);
                    console.log(textStatus, errorThrown);
                }
            });
        });

    })();
});


function addDeviceControls(userTitle, techTitle) {
    var html =
    '                <div class="shadow mb-2 px-3 card bg-semi-trans">' +
    '                    <div class="row pb-3">' +
    '                        <div class="col-4 col-md-2 mt-3 pr-0 align-self-center text-center">' +
    '                            <h5 class="m-0" style="font-size:1rem"><span><strong>' + userTitle + '</strong></span></h5>' +
    '                        </div>' +
    '                        <div class="input-group col-4 col-md-2 mt-3 align-self-center">' +
    '                            <input class="m-0" type="checkbox" id="' + techTitle + 'State" data-toggle="toggle"' +
    '                                   data-on="ВКЛ" data-off="ВЫКЛ" data-width="100">' +
    '                        </div>' +
    '                        <div class="input-group col-4 col-md-2 mt-3 align-self-center">' +
    '                            <select class="form-control" id="' + techTitle + 'AlarmDay">' +
    '                                <option value="mon">ПН</option>' +
    '                                <option value="tue">ВТ</option>' +
    '                                <option value="wed">СР</option>' +
    '                                <option value="thu">ЧТ</option>' +
    '                                <option value="fri">ПТ</option>' +
    '                                <option value="sat">СБ</option>' +
    '                                <option value="sun">ВС</option>' +
    '                            </select>' +
    '                        </div>' +
    '                        <div class="input-group col-6 col-md-3 mt-3">' +
    '                            <div class="input-group-prepend">' +
    '                                <span class="input-group-text">С</span>' +
    '                            </div>' +
    '                            <input type="text" class="form-control" id="' + techTitle + 'AlarmStart" name="alarmStart" value="09:00"' +
    '                                   required readonly>' +
    '                        </div>' +
    '                        <div class="input-group col-6 col-md-3 mt-3">' +
    '                            <div class="input-group-prepend">' +
    '                                <span class="input-group-text">До</span>' +
    '                            </div>' +
    '                            <input type="text" class="form-control" id="' + techTitle + 'AlarmEnd" name="alarmEnd" value="18:00"' +
    '                                   required readonly>' +
    '                        </div>' +
    '                    </div>' +
    '                </div>';

    $('#myControls > div').append(html);
}
addDeviceControls('ФОНТАН', 'fountain');
addDeviceControls('СВЕТ', 'light');
addDeviceControls('ЗВУК', 'sound');
addDeviceControls('AUX GPIO 1', 'auxGpio1');
addDeviceControls('AUX GPIO 2', 'auxGpio2');
addDeviceControls('AUX GPIO 3', 'auxGpio3');
addDeviceControls('AUX GPIO 4', 'auxGpio4');
addDeviceControls('AUX GPIO 5', 'auxGpio5');
addDeviceControls('AUX GPIO 6', 'auxGpio6');
