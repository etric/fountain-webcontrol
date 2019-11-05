///////////////
// MAIN PAGE //
///////////////

$(document).ready(() => {
    $.ajax({
        type: "GET",
        url: '/api/config',
        success: (appConfig) => {
            initComponents(appConfig);
            initMenuItems();
        },
        error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
    });
});

const initComponents = (appConfig) => {
    const soundDevicesEnabled = appConfig['soundDevicesEnabled'];
    initDeviceControls(soundDevicesEnabled);
    initSysDateTime();
    initAlarmControls(soundDevicesEnabled);
    initControlModeControls(soundDevicesEnabled);
    if (soundDevicesEnabled) {
        $('.sound-related').removeClass('d-none');
    }
    $('.version-label').text(appConfig['version']);
};

const initMenuItems = () => {
    const gotoMenuItemPage = (page, userPass) => $.ajax({
        type: 'GET',
        url: '/api/' + page + '/page',
        headers: {'pswd': userPass},
        success: (response) => {
            let w = window.open();
            w.document.open();
            w.document.pswd = userPass;
            w.document.write(response);
            w.document.close();
        },
        error: (jqXHR) => {
            toastr.error('Не удалось аутентифицировать: ' + jqXHR.responseText);
        }
    });
    PAGES.forEach(item =>
        $('#' + item.techName + 'Menu').on('click', () => {
            if (!item.secured) {
                gotoMenuItemPage(item.techName);
                return;
            }
            bootbox.prompt({
                title: "Вверите пароль",
                inputType: 'password',
                callback: (userPass) => {
                    if (userPass !== null) {
                        gotoMenuItemPage(item.techName, userPass)
                    }
                }
            });
        }));
};
