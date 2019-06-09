///////////////////////////////////////
// FOUNTAIN/LIGHT/SOUND/AUX SWITCHES //
///////////////////////////////////////

let initDeviceControls = () => {

    let addDeviceControls = (deviceInfo) => {
        const {userTitle, techName, withAlarms} = deviceInfo;

        let html =
            '                <div class="shadow mb-2 px-3 card bg-semi-trans">' +
            '                    <div class="row pb-3">' +
            '                        <div class="col-4 col-md-2 mt-3 pr-0 align-self-center text-center">' +
            '                            <h5 class="m-0" style="font-size:1rem"><span><strong>' + userTitle + '</strong></span></h5>' +
            '                        </div>' +
            '                        <div class="input-group col-4 col-md-2 mt-3 pr-1 align-self-center">' +
            '                            <input class="m-0" type="checkbox" id="' + techName + 'State" data-toggle="toggle"' +
            '                                   data-on="ВКЛ" data-off="ВЫКЛ" data-width="100">' +
            '                        </div>';

        if (withAlarms) {
            html +=
                '                        <div class="input-group col-4 col-md-2 mt-3 pl-1 align-self-center">' +
                '                            <select class="form-control" id="' + techName + 'AlarmDay">' +
                '                                <option value="MONDAY">ПН</option>' +
                '                                <option value="TUESDAY">ВТ</option>' +
                '                                <option value="WEDNESDAY">СР</option>' +
                '                                <option value="THURSDAY">ЧТ</option>' +
                '                                <option value="FRIDAY">ПТ</option>' +
                '                                <option value="SATURDAY">СБ</option>' +
                '                                <option value="SUNDAY">ВС</option>' +
                '                            </select>' +
                '                        </div>' +
                '                        <div class="input-group col-6 col-md-3 mt-3">' +
                '                            <div class="input-group-prepend">' +
                '                                <span class="input-group-text">С</span>' +
                '                            </div>' +
                '                            <input type="text" class="form-control" id="' + techName + 'AlarmStart" name="alarmStart" value="09:00"' +
                '                                   required readonly>' +
                '                        </div>' +
                '                        <div class="input-group col-6 col-md-3 mt-3">' +
                '                            <div class="input-group-prepend">' +
                '                                <span class="input-group-text">До</span>' +
                '                            </div>' +
                '                            <input type="text" class="form-control" id="' + techName + 'AlarmEnd" name="alarmEnd" value="18:00"' +
                '                                   required readonly>' +
                '                        </div>';
        }

        html +=
            '                    </div>' +
            '                </div>';

        $('#myControls > div').append(html);
    };

    $('#commonAlarmDay').change(function() {
        let selectedAlarmDay = $(this).val();
        DEVICES_WITH_ALARMS.forEach(deviceInfo => {
            let alarmDay = $('#' + deviceInfo.techName + 'AlarmDay');
            alarmDay.val(selectedAlarmDay);
            alarmDay.change();
        });
    });

    DEVICES.forEach(deviceInfo => {

        addDeviceControls(deviceInfo);

        let deviceType = deviceInfo.techName;
        let selector = '#' + deviceType + 'State';

        $.ajax({
            type: "GET",
            url: '/api/' + deviceType,
            success: (response) => {
                console.log('INIT: ' + deviceType + ' state: ' + response);
                $(selector).bootstrapToggle(response);
                $(selector).change(function() {
                    let deviceState = $(this).prop('checked') ? 'on' : 'off';
                    console.log('STATE CHANGED: ' + deviceType + ' state: ' + deviceState);
                    $.ajax({
                        type: "PUT",
                        url: '/api/' + deviceType + '/' + deviceState,
                        success: (response) => {
                            toastr.success('Состояние ' + eng2rus(deviceType) + 'а изменено!');
                        },
                        error: (jqXHR, textStatus, errorThrown) => {
                            toastr.error('Состояние ' + eng2rus(deviceType) + 'а не изменено: ' + textStatus);
                            console.log(textStatus, errorThrown);
                        }
                    });
                });
            },
            error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
        });
    });
};
