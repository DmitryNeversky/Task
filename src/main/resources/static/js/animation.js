jQuery(document).ready(function () {

    $('.advanced-input').change(function () {
        $("label[for='" + $(this).attr('id') + "']").animate({top: "1em", left: "-3rem", fontWeight: "100"}, 300)
    })

    $('.advanced-input').focus(function () {
        $("label[for='" + $(this).attr('id') + "']").animate({top: "1em", left: "-3rem", fontWeight: "100"}, 300)
    });

    $('.advanced-input').focusout(function () {
        if($(this).val() === '') {
            $("label[for='" + $(this).attr('id') + "']").animate({top: "3.6em", left: "0", fontWeight: "600"}, 300)
        }
    });
});