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
        $('#lineNumText').text("Total line number: "+ ($("div:visible").length-2) );
    });

    $('a.logNameATag').click(function(){
        var aTagClass = $(this).attr('class').split(" ")[0];
        var divClass = aTagClass.substring( 0, aTagClass.length-4 );
        $('.all').hide();
        $('.'+divClass+'.'+ $('#typeSelector').val()).show();
        $('#lineNumText').text("Total line number: "+ ($("div:visible").length-2) );
    });

    $('#showAllLogName').click(function(){
        $('.'+$('#typeSelector').val()).show();
        $('#lineNumText').text("Total line number: "+ ($("div:visible").length-2) );
    });

    $('#scrollTop').click(function(event){
        event.preventDefault();
        var previousURL = window.location.toString().split("#")[0];
        window.location.href = previousURL + "#" + $("#metridocLogs div:visible:first").attr('id');
    });

    $('#scrollBottom').click(function(event){
        event.preventDefault();
        var previousURL = window.location.toString().split("#")[0];
        window.location.href = previousURL + "#" + $("#metridocLogs div:visible:last").attr('id');
    });


    $( '#showAllLogName, #scrollTop, #scrollBottom' ).button().css({'padding' : '.1em', 'color' : 'black',
        'height' : '1.5em', 'width' : '110px', 'font-size': '0.9em'});

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
        $('#lineNumText').text("Total line number: "+ ($("div:visible").length-2) );
    }
)