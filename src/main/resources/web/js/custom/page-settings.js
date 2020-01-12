///////////////////
// SETTINGS PAGE //
///////////////////

$(document).ready(() => {

    const BUTTONS_COUNT = 64;

    const initButtons = (states) => {
        const cnt = $('.buttons-container');
        for (let i = 0; i < BUTTONS_COUNT; i++) {
            let isChecked = states[i] === true;
            let btnHtml =
                '<div class="button-item btn btn-secondary ' + (isChecked ? 'active' : '') + '">' +
                '<input class="auxBtn" data-auxBtn-num="' + i + '" type="checkbox" ' + (isChecked ? 'checked' : '') + '>' + i +
                '</div>';
            cnt.append(btnHtml);
            console.log(btnHtml);
            console.log('');
        }
        $('input.auxBtn:checkbox').change(
            function(){
                let _this = $(this);
                let checked = _this.is(':checked');
                let auxBtnNum = _this.attr('data-auxBtn-num');
                $.ajax({
                    type: "PUT",
                    url: '/api/settings/auxButtons/' + auxBtnNum + '/' + checked,
                    success: (response) => {
                        toastr.success('Кнопка ' + auxBtnNum + ' → ' + (checked ? 'ВКЛ' : 'ВЫКЛ') );
                    },
                    error: (jqXHR, textStatus, errorThrown) => {
                        toastr.error('Состояние не изменено: ' + jqXHR.responseText);
                    }
                });

            });
    };

    $.ajax({
        type: "GET",
        url: '/api/settings/auxButtons',
        success: (data) => {
            console.log('AuxButtons: ' + data);
            const auxButtonStates = JSON.parse(data);
            initButtons(auxButtonStates);
        },
        error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
    });

    const initSlider = (techName) => {
        const sliderName = techName + 'Slider';
        const sliderSelector = '#' + sliderName;
        $.ajax({
            type: "GET",
            url: '/api/settings/sliders/' + techName,
            success: (response) => {
                console.log('INIT: ' + sliderName + ' - ' + response);
                $(sliderSelector + 'Out').val(response);
                let sliderElem = $(sliderSelector);
                sliderElem.val(response);
                sliderElem.on('change', () => {
                    let newValue = $(sliderSelector).val();
                    console.log(sliderName + ' CHANGED: ' + newValue);
                    $.ajax({
                        type: "PUT",
                        url: '/api/settings/sliders/' + techName + '/' + newValue,
                        success: (response) => {
                            toastr.success('Изменено: ' + techName + ' = ' + newValue);
                        },
                        error: (jqXHR, textStatus, errorThrown) => {
                            toastr.error('Не изменено: ' + techName + ': ' + jqXHR.responseText);
                        }
                    });
                });
            },
            error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
        });
    };


    ['motor', 'red', 'green', 'blue']
        .forEach(techName => initSlider(techName));

});
