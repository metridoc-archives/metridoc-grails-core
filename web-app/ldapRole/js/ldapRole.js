function deleteMapping(mappingId) {
    if (window.confirm("are you sure you want to delete this mapping?")) {
        $('#mdForm_' + mappingId).submit();
    }
}


