function checkAndSetAccess(status) {
    var url = $('#linkUrl_' + status).text();
    if (url.search("checkAccess") == -1) {
        if (url.search('\\?') == -1) {
            url = url + "?"
        }

        url = url + "checkAccess"
    }

    var text = $.ajax({
        url:url,
        type:'GET',
        cache:false,
        async:false
    }).responseText;

    if (text.search("Remember Me?") == -1) {
        $('#linkHasAccess_' + status).html('true')
    } else {
        $('#linkHasAccess_' + status).html('false')
    }
}
