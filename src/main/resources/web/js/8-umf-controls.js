//////////////////
// UMF CONTROLS //
//////////////////
(() => {

    let pollWasActive = true;

    const reloadBulbStates = () => {
        $.ajax({
            type: "GET",
            url: "/api/umf/bulb/states",
            headers: {
                'pswd': document.pswd
            },
            success: (data) => {
                const bulbStates = JSON.parse(data);
                const bulbs = $('.bulb-container .bulb-indicator');
                for (let i = 0; i < bulbs.length; i++) {
                    bulbs[i].classList.remove('bulb-grn', 'bulb-red');
                    bulbs[i].classList.add('bulb-' + bulbStates[i]);
                }
                if (!pollWasActive) {
                    $('.pulse').show();
                    pollWasActive = true;
                }
            },
            error: () => {
                if (pollWasActive) {
                    $('.pulse').hide();
                    pollWasActive = false;
                }
            }
        });
    };

    const bulbControlsHtml = (idx, switchLabel, bulbLabel) =>
        '    <div class="row bulb-container" style="margin-bottom:1.3vh">' +
        '        <div class="col-12 align-self-center text-left">' +
        '           <span class="mr-2 d-inline-block text-right" style="width:80px">' + switchLabel + '</span>' +
        '           <input type="checkbox" checked data-toggle="toggle" data-on="1" data-off="0"' +
        '               data-size="xs" data-bulbnum="' + idx + '" data-onstyle="danger" data-offstyle="success">' +
        '           <div class="ml-3 d-inline-block"><i class="fas fa-circle fa-lg bulb-indicator"></i></div>' +
        '           <span class="mr-2 d-inline-block text-left" style="width:80px">' + bulbLabel + '</span>' +
        '        </div>' +
        '    </div>';

    const initBulbs = (bulbDetails) => {
        const cnt = $('.container');
        for (let i = 0; i < bulbDetails.length; i++) {
            cnt.append(bulbControlsHtml(i, bulbDetails[i]['switchLabel'], bulbDetails[i]['bulbLabel']));
            // console.log('switchState #' + i + ": " + bulbDetails[i].switchState);
            // console.log('found el: ' + $('[data-bulbnum=' + i + ']'));
            $('[data-bulbnum=' + i + ']').bootstrapToggle(bulbDetails[i].switchState).change(e => {
                let num = e.target.dataset.bulbnum;
                console.log(num + ' -> ' + e.target.checked);
                let switchState = e.target.checked ? 'on' : 'off';
                $.ajax({
                    type: "PUT",
                    url: "/api/umf/bulb/" + num +'/' + switchState,
                    headers: {
                        'pswd': document.pswd
                    },
                });
            });
        }
        setInterval(reloadBulbStates, INTERVAL_GET_BULB_STATES);
    };

    $.ajax({
        type: "GET",
        url: "/api/umf/bulb/list",
        headers: {
            'pswd': document.pswd
        },
        success: (data) => {
            initBulbs(JSON.parse(data))
        },
        error: (jqXHR, textStatus, errorThrown) => {
            toastr.error('Не удалось инициализировать лампы: ' + jqXHR.responseText);
        }
    });

})();
