////////////////////
// DOCUMENT READY //
////////////////////

$(document).ready(() => {
    $.ajax({
        type: "GET",
        url: '/api/config',
        success: (appConfig) => {
            initComponents(appConfig);
        },
        error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
    });
});

function initComponents(appConfig) {
    const soundDevicesEnabled = appConfig['soundDevicesEnabled'];

    initDeviceControls(soundDevicesEnabled);
    initSysDateTime();
    initAlarmControls(soundDevicesEnabled);
    initControlModeControls(soundDevicesEnabled);

    if (soundDevicesEnabled) {
        initMusicControls();
        initVolumeControls();
        $('.sound-related').removeClass('d-none');
    }

    if (soundDevicesEnabled) {
        $('#playlist-tab').addClass('active');
        $('#controls-tab').removeClass('active');
        $('#myControls').removeClass('fade show active');
    } else {
        $('#playlist-tab').removeClass('active');
        $('#controls-tab').addClass('active');
        $('#myControls').addClass('fade show active');
    }

    $('.version-label').text(appConfig['version']);
}
