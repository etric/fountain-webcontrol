//////////////////
// CONTROL MODE //
//////////////////

let initControlModeControls = (soundDevicesEnabled) => {

    let changeAllDevicesSwitchState = (autoMode) => {
        getDevices(true, soundDevicesEnabled).forEach(deviceInfo => {
            let deviceType = deviceInfo.techName;
            let stateElem = $('#' + deviceType + 'State');

            stateElem.parent().parent().css('opacity', autoMode ? 0.3 : 1.0);
            stateElem.bootstrapToggle(autoMode ? 'disable' : 'enable');

            $('#' + deviceType + 'AlarmStart').parent().css('opacity', !autoMode ? 0.3 : 1.0);
            $('#' + deviceType + 'AlarmEnd').parent().css('opacity', !autoMode ? 0.3 : 1.0);
        });
    };

    $.ajax({
        type: "GET",
        url: '/api/mode',
        success: function (data) {
            console.log('Initialized CONTROL_MODE: ' + data);
            var switchedOn = (data === 'auto');
            $('#controlMode').prop("checked", switchedOn);
            changeAllDevicesSwitchState(switchedOn);
            // var cancelChangeControlMode = false;
            $('#controlMode').click(function () {
                var controlModeEl = $(this);
                var autoMode = controlModeEl.is(':checked');
                var confirmationText = "Сменить режим на <b>" + (autoMode ? 'АВТО' : 'РУЧНОЙ') + "</b>?";
                bootbox.confirm(confirmationText, function (result) {
                    if (result === true) {
                        console.log('CONTROL MODE CHANGED: AUTO? ' + autoMode);
                        changeAllDevicesSwitchState(autoMode);
                        $.ajax({
                            type: "PUT",
                            url: '/api/mode/' + (autoMode ? 'auto' : 'manual'),
                            success: function (response) {
                                toastr.success('Режим ' + (autoMode ? 'АВТО' : 'РУЧНОЙ') + ' установлен!');
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                toastr.error('Режим ' + (autoMode ? 'АВТО' : 'РУЧНОЙ') + ' не установлен: ' + jqXHR.responseText);
                            }
                        });
                    } else {
                        controlModeEl.prop("checked", !autoMode);
                    }
                });
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });
};
