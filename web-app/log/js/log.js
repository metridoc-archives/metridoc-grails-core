/**
 * Created with IntelliJ IDEA.
 * User: dongheng
 * Date: 8/24/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */


//add all the handlers for the buttons
$(function () {

    $('#scrollBottom').click(function (event) {
        event.preventDefault();
        $("#metridocLogs").scrollTop($('#metridocLogs').prop("scrollHeight"));
    });

    $('#scrollTop').click(function (event) {
        event.preventDefault();
        $("#metridocLogs").scrollTop(0);
    });

    $('#metridocLogs').bind('scroll', chk_scroll);

    $('#logFile').change(function () {
        $('#metridocLogs').fadeOut(200, function () {
            forceLogUpdate(true);
        });
    });

});

var isAtBottomOfLog = false;

function chk_scroll(e) {
    var elem = $(e.currentTarget);
    var scrollHeight = elem[0].scrollHeight;
    var scrollTop = elem.scrollTop();
    var outerHeaight = elem.outerHeight();
    var difference = scrollHeight - scrollTop - outerHeaight;
    var fudgeFactor = 20;
    isAtBottomOfLog = Math.abs(difference) < fudgeFactor;
}

function forceLogUpdate(fade) {
    var encodedFileName = encodeURIComponent($('#logFile').val());
    if (fade) {
        $("#metridocLogs").load("plain/" + encodedFileName, function () {
            $('#metridocLogs').fadeIn()
        });
    } else {
        $("#metridocLogs").load("plain/" + encodedFileName);
    }
}

function updateLog() {
    var isStreaming = $('#doStreaming').is(":checked");
    if (isStreaming && isAtBottomOfLog) {
        forceLogUpdate(false);
        $("#metridocLogs").scrollTop($('#metridocLogs').prop("scrollHeight"));
        //make checks frequent
        triggerNextLogUpdate(1000);
    } else {
        //don't need to check for awhile
        triggerNextLogUpdate(3000);
    }
}

//by using set timeout instead of interval we guarantee that there is a one second break in refreshing the log
function triggerNextLogUpdate(time) {
    window.setTimeout(updateLog, time);
}

triggerNextLogUpdate(3000);

