////////////////////
// VOLUME CONTROL //
////////////////////
let initVolumeControls = () => {
    $.ajax({
        type: "GET",
        url: '/api/volume',
        success: (response) => {
            console.log('INIT: volume - ' + response);
            $('#volumeSliderOut').val(response);
            let volSlider = $('#volumeSlider');
            volSlider.val(response);
            volSlider.on('change', () => {
                let newVolume = $('#volumeSlider').val();
                console.log('VOLUME CHANGED: ' + newVolume);
                $.ajax({
                    type: "PUT",
                    url: '/api/volume/' + newVolume,
                    success: (response) => {
                        toastr.success('Громкость изменена!');
                    },
                    error: (jqXHR, textStatus, errorThrown) => {
                        toastr.error('Громкость не изменена: ' + textStatus);
                        console.log(textStatus, errorThrown);
                    }
                });
            });
        },
        error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
    });
};
