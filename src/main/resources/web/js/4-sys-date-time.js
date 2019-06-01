////////////////////
// PI SYSTEM TIME //
////////////////////

let initSysDateTime = () => {

    let sysDateTimePicker = new Picker(document.querySelector('.pi-sysdatetime'), {
        controls: true,
        headers: true,
        language: 'ru',
        format: 'YYYY-MM-DD HH:mm',
        text: {
            year: 'Год', month: 'Мясяц', day: 'День', hour: 'Часы', minute: 'Минуты',
            title: 'Системное время', cancel: 'Отмена', confirm: 'ОК'
        }
    });

    let getDayOfWeek = (date) => {
        let dayNum = date.getDay();
        if (dayNum === 0) return "SUNDAY";
        if (dayNum === 1) return "MONDAY";
        if (dayNum === 2) return "TUESDAY";
        if (dayNum === 3) return "WEDNESDAY";
        if (dayNum === 4) return "THURSDAY";
        if (dayNum === 5) return "FRIDAY";
        if (dayNum === 6) return "SATURDAY";
        console.log('ERROR! Weird Date: ' + date);
    };

    let markTodayDayOfWeek = (date) => {
        const dayOfWeek = getDayOfWeek(date);
        $('*[id$="AlarmDay"] > option').each((index, item) => {
            let elem = $(item);
            elem.css('font-weight', elem.val() === dayOfWeek ? 'bold' : 'normal');
        });
    };

    let fetchUpdates = () => {
        $.ajax({
            type: "GET",
            url: '/api/sysdatetime',
            success: (response) => {
                sysDateTimePicker.setValue(response);
                markTodayDayOfWeek(new Date(response));
            },
            error: (jqXHR, textStatus, errorThrown) => console.log(textStatus, errorThrown)
        });
    };

    fetchUpdates();
    setInterval(fetchUpdates, INTERVAL_GET_SYS_DATE_TIME);

    $('.pi-sysdatetime').change(function() {
        let newSysTime = $(this).val();
        console.log('Updating system time to ' + newSysTime);
        $.ajax({
            type: "PUT",
            url: '/api/sysdatetime',
            data: newSysTime,
            dataType: 'text',
            success: (response) => {
                toastr.success('Системное время изменено!');
            },
            error: (jqXHR, textStatus, errorThrown) => {
                toastr.error('Системное время не изменено: ' + textStatus);
                console.log(textStatus, errorThrown);
            }
        });
    });
};
