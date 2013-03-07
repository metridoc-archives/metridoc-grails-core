var FIVE_MINUTES = 1000 * 60 * 5;

addCountDownAndJobDetails();
addWindowRefresh();
addToolTips()
addClock()

function addClock() {
    var clock = $('#clock');
    clock.clock({"timestamp": new Date(clock.data('time'))});
}

function addToolTips() {
    $('#new-job').tooltip()
    $('#quartz-settings').tooltip()
    $('#start-scheduler').tooltip()
    $('#stop-scheduler').tooltip()
}

function reloadWindow(delay) {
    var fifteenSeconds = 1000 * 15;
    var usedDelay = Math.max(delay, fifteenSeconds);
    console.log("Job list refresh will occur in " + usedDelay + " milliseconds");
    setTimeout(function () {
        $.get('jobTableOnly', replaceJobTable);
    }, usedDelay);
}

function replaceJobTable(data) {
    $('#jobListTable').html(data);
    console.log('replaced the raw data for the job list');
    addCountDownAndJobDetails();
}

function addWindowRefresh() {
    var today = new Date();
    var time = today.getTime();

    $("#quartz-jobs tr").each(function (index) {
        if (index != 0) {
            var row = $(this).find("td");
            var countDown = row[4];
            var nextRun = $(countDown).attr("data-next-run");
            if (nextRun) {
                var delay = nextRun - time;
                var nextRefresh = Math.min(Math.max(0, delay), FIVE_MINUTES);
                reloadWindow(nextRefresh);
            }

            var status = row[2];
            var running = $(status).hasClass("running");
            if (running) {
                var duration = $(countDown).attr("data-last-runtime");
                var usedDuration = FIVE_MINUTES;
                if (duration > 0) {
                    /*
                     * TODO: we need to fine tune this a bit.  We do have enough information to
                     */
                    usedDuration = Math.min(duration / 2, FIVE_MINUTES);
                }
                reloadWindow(usedDuration);
            }

        }
    });
}
function addCountDownAndJobDetails() {
    var
        init = function () {
            $('.quartz-to-hide').hide();
            $('.quartz-tooltip').tooltip();
            $('.quartz-countdown').each(function () {
                var item = $(this),
                    remaining = item.data('next-run');
                if (remaining === "") return;
                countdown(item, remaining);
            });

        },

        reloadPage = function () {
            var original = $(this).css('color');
            var delay = 200;
            $(this).animate({
                backgroundColor: '#f00',
                color: '#fff'
            }, delay, function () {
                $(this).animate({
                    backgroundColor: 'transparent',
                    color: original
                }, delay, function () {
                    $(this).animate({
                        backgroundColor: '#f00',
                        color: '#fff'
                    }, delay, function () {
                        $(this).animate({
                            backgroundColor: 'transparent',
                            color: original
                        }, delay, function () {
                            location.reload(true);
                        });
                    });
                });
            });
        },

        countdown = function (item, remaining) {
            if (item.countdown !== undefined) {
                item.countdown(
                    {until: new Date(remaining),
                        onExpiry: reloadPage,
                        compact: true
                    });
            }
        };
    $(init);
}

