function makeUpdateButtonAppear() {
    console.log("Schedule has changed, enabling the update button");
    $('#updateScheduleBtn').removeAttr("disabled")
}
$('#availableSchedules').change(makeUpdateButtonAppear);