//////////////////
// CONTROL MODE //
//////////////////

let initControlModeControls = () => {

    let changeAllDevicesSwitchState = (autoMode) => {
        DEVICES.forEach(deviceInfo => {
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
            $('#controlMode').bootstrapToggle(switchedOn ? 'on' : 'off');
            changeAllDevicesSwitchState(switchedOn);
            var cancelChangeControlMode = false;
            $('#controlMode').change(function () {
                var controlModeEl = $(this);
                if (cancelChangeControlMode === true) {
                    cancelChangeControlMode = false;
                    return;
                }
                var autoMode = $(this).prop('checked');
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
};
