////////////////////
// MUSIC UPLOADER //
////////////////////

let initMusicControls = () => {

    $('.custom-file-input').on('change', function() {
        let thisElem = $(this);
        let fileName = thisElem.val().split('\\').pop();
        thisElem.siblings('.custom-file-label').addClass('selected').html(fileName);
    });

    $('#upload-music-file').on('click', () => {
        let musicFileChooser = $('#music-file-chooser');
        let musicNumSelector = $('#music-num-selector');
        let fileName = musicFileChooser.val();
        if (!fileName) {
            toastr.warning('Выберите мелодию!');
            return;
        }
        fileName = fileName.split('\\').pop();
        let confirmationText = "Загрузить новую мелодию для <b>#" + musicNumSelector.val() + ": " + fileName + "</b>?";
        bootbox.confirm(confirmationText, (result) => {
            if (result === true) {
                let musicFileFormData = new FormData();
                let musicFile = musicFileChooser[0].files[0];
                musicFileFormData.append('file', musicFile);
                $.ajax({
                    type: "PUT",
                    url: '/api/music/' + musicNumSelector.val(),
                    data: musicFileFormData,
                    contentType: false,
                    processData: false,
                    success: () => {
                        toastr.success('Мелодия ' + fileName + ' загружена!');
                        musicFileChooser.val('');
                        $('#pli-' + musicNumSelector.val()).text(fileName);
                    },
                    error: (jqXHR, textStatus, errorThrown) => {
                        toastr.error('Мелодия ' + fileName + ' не загружена: ' + jqXHR.responseText);
                    }
                });
            }
        });
    });

    let currentPlayingItem = 0;
    let markCurrentPlayingItem = () => {
        $.ajax({
            type: "GET",
            url: "/api/music/currentPlayingItem",
            success: (index) =>{
                if (index !== currentPlayingItem) {
                    currentPlayingItem = index;
                    $('.wave').remove();
                    if (index > 0) {
                        $('.playlist #pli-' + index).parent().append('<span class="wave"></span>');
                    }
                }
            }
        });
    };
    setInterval(markCurrentPlayingItem, INTERVAL_GET_CURR_PLAYING);

    // LOAD PLAYLIST
    (() => {
        $.ajax({
            type: "GET",
            url: "/api/music/playlist",
            success: (data) => {
                console.log('Playlist: ' + data);
                let array = JSON.parse(data);
                if (array) {
                    let playlist = $('.playlist');
                    for (let i = 0; i < array.length; i++) {
                        let item = array[i] || '-';
                        playlist.append(
                            '<li class="list-group-item d-flex justify-content-between 1h-condensed">' +
                            '  <h6 id="pli-' + (i + 1) + '" class="my-0">' + item + '</h6>' +
                            '</li>');
                    }
                    markCurrentPlayingItem();
                }
            }
        })
    })();

    // PAUSE BETWEEN TRACKS
    (() => {
        const pauseBetweenTracksEl = $('#pauseBetweenTracks');

        for (let i = 0; i <= 300; i++) {
            pauseBetweenTracksEl.append(`<option value="${i}">${i}</option>}`);
        }

        $.ajax({
            type: "GET",
            url: '/api/pauseBetweenTracks',
            success: (response) => {
                console.log('INIT: pauseBetweenTracksEl - ' + response);
                pauseBetweenTracksEl.val(response); //TODO works?
                pauseBetweenTracksEl
                    .on('focusin', function () {
                        $(this).data('val', $(this).val());
                    })
                    .on('change', function () {
                        let prev = $(this).data('val');
                        let current = $(this).val();
                        if (prev !== current) {
                            $.ajax({
                                type: "PUT",
                                url: '/api/pauseBetweenTracks/' + current,
                                success: (response) => {
                                    toastr.success('Пауза изменена!');
                                },
                                error: (jqXHR, textStatus, errorThrown) => {
                                    toastr.error('Пауза не изменена: ' + jqXHR.responseText);
                                }
                            });
                        }
                    });
            },
            error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
        });
    })();
};
