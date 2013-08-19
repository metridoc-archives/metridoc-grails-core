function deleteMapping(mappingId) {
    if (window.confirm("are you sure you want to delete this mapping?")) {
        $('#mdForm_' + mappingId).submit();
    }
}

function showGroupForm() {
    $('#createGroupForm').toggle();
    $('#createGroup').toggleClass('icon-plus-sign icon-circle-arrow-up')
}

function changeIcon(callingID) {
    if (callingID == 'groupList') {
        $('#cGroupList').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
    }

    else {
        $('#cLdapConfig').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
    }
}

$(document).ready(function () {
    $("#ldapConfig").collapse({toggle: false});
    $("#groupList").collapse({toggle: false});
    var prev = $('#previousExpanded').val();
    var iconId = prev.charAt(0).toUpperCase() + prev.slice(1);
    if (prev != 'none') {
        $('#' + prev).collapse('show');
        $('#' + 'c' + iconId).addClass('icon-circle-arrow-up').removeClass('icon-circle-arrow-down');

    }
})

