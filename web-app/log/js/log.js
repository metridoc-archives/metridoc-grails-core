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

})

var isAtBottomOfLog = false

function chk_scroll(e) {
    var elem = $(e.currentTarget);
    var scrollHeight = elem[0].scrollHeight;
    var scrollTop = elem.scrollTop();
    var outerHeaight = elem.outerHeight()
    if (scrollHeight - scrollTop == outerHeaight) {
        isAtBottomOfLog = true
    } else {
        isAtBottomOfLog = false
    }

}

function updateLog() {
    var isStreaming = $('#doStreaming').is(":checked")
    if (isStreaming && isAtBottomOfLog) {
        $("#metridocLogs").load("plain")
        $("#metridocLogs").scrollTop($('#metridocLogs').prop("scrollHeight"));
    }
}

window.setInterval(updateLog, 1000);