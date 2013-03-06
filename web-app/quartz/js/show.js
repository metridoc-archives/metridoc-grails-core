//TODO: add check for actual changes..... don't make the update button appear unless something actually did happen
function makeUpdateButtonAppear() {
    $('#updateScheduleBtn').removeAttr("disabled")
}
$('#availableSchedules').change(makeUpdateButtonAppear);
$('#arguments').change(makeUpdateButtonAppear);


function editorChange() {
    makeUpdateButtonAppear()
}

//override the groovy based one so we can hook into change events
editor = CodeMirror.fromTextArea(document.getElementById('code'), {
    mode: 'groovy',
    lineNumbers: true,
    matchBrackets: true,
    onKeyEvent: editorChange
});