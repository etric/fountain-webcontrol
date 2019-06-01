////////////////////
// DOCUMENT READY //
////////////////////

$(document).ready(() => {
    initDeviceControls();
    initMusicControls();
    initSysDateTime();
    initAlarmControls();
    initVolumeControls();
    initControlModeControls();

    let layoutTabs = () => {
        if (isTabletWidth()) {
            $('#myPlaylist').addClass('fade show active');
            $('#myControls').addClass('fade show active');
        } else {
            $('#playlist-tab').addClass('active');
            $('#controls-tab').removeClass('active');
            $('#myControls').removeClass('fade show active');
        }
    };
    layoutTabs();
    $(window).on("orientationchange", () => setTimeout(layoutTabs, 0));
});
