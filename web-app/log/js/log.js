/**
 * Created with IntelliJ IDEA.
 * User: dongheng
 * Date: 8/24/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */



$(function () {
    $('#typeSelector').change(function () {
        handleLogOutput()
    })
})


function handleLogOutput() {
    var selector = $('#typeSelector')
    if (selector.val() == "all") {
        $('.all').show();
    }
    else if (selector.val() == "error") {
        $('.all').hide();
        $('.error').show();
    }
    else if (selector.val() == "info") {
        $('.all').hide();
        $('.info').show();
    }
    else if (selector.val() == "warn") {
        $('.all').hide();
        $('.warn').show();
    }
    else if (selector.val() == "hour") {
        $('.all').hide();
        $('.hour').show();
    }
    else if (selector.val() == "sixHours") {
        $('.all').hide();
        $('.sixHours').show();
    }
    else if (selector.val() == "twelveHours") {
        $('.all').hide();
        $('.twelveHours').show();
    }
    else if (selector.val() == "day") {
        $('.all').hide();
        $('.day').show();
    }
}

$(window).load(
    function () {
        handleLogOutput()
    }
)