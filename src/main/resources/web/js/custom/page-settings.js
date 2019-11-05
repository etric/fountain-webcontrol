///////////////////
// SETTINGS PAGE //
///////////////////

$(document).ready(() => {

    const BUTTONS_COUNT = 64;

    const initButtons = () => {
        const cnt = $('.buttons-container');
        for (let i = 0; i < BUTTONS_COUNT; i++) {
            cnt.append(
                '<div class="button-item btn btn-secondary">' +
                '<input class="auxBtn" data-auxBtn-num="' + i + '" type="checkbox">' + i +
                '</div>');
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

    initButtons();

    ['motor', 'red', 'green', 'blue']
        .forEach(techName => initSlider(techName));

});
