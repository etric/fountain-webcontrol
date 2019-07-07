//////////////
// UMF PAGE //
//////////////
(() => {
    const gotoUmfPage = (userPass) => $.ajax({
        type: 'GET',
        url: '/api/umf/page',
        headers: { 'pswd': userPass },
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

    $('#umfMenu').on('click', () => {
        bootbox.prompt({
            title: "Вверите пароль",
            inputType: 'password',
            callback: gotoUmfPage
        });
    });
})();
