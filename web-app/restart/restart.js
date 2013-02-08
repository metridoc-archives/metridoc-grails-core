function getHomeLink() {
    return $("#homeLink").text();
}

function getLinkToCheck() {
    return $("#linkToCheck").text();
}

function getTriggerRestartLink() {
    return $("#triggerRestartLink").text();
}

function ajaxSuccess (data) {
    var homeLink = getHomeLink();
    console.log("link check succeeded, redirecting to " + homeLink);
    window.location = getHomeLink();
}

function ajaxError (data) {
    console.log("link check failed, application is still restarting");
    fireOffTimeout();
}

function checkLink() {
    $.ajax({
        url: getLinkToCheck(),
        success: ajaxSuccess,
        error:ajaxError
    });
}

function fireOffTimeout() {
    setTimeout(checkLink, 5000);
}

function triggerRestart() {
    $.ajax(
        {
            url: getTriggerRestartLink()
        }
    )
}

triggerRestart()
fireOffTimeout()