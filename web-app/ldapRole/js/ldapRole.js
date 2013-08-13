function deleteMapping(mappingId) {
    if (window.confirm("are you sure you want to delete this mapping?")) {
        $('#mdForm_' + mappingId).submit();
    }
}

function showGroupForm() {
    $('#createGroupForm').toggle();
    $('#createGroup').toggleClass('icon-plus-sign icon-circle-arrow-up')
}

$(document).ready(function () {
    assignID()
})

