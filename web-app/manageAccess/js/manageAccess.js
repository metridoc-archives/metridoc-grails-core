/**
 * Created with IntelliJ IDEA.
 * User: intern
 * Date: 8/15/13
 * Time: 10:03 AM
 * To change this template use File | Settings | File Templates.
 */

function collapseOthers(callingID) {
    if (callingID == 'userList') {
        $('#userList').collapse('toggle');
        $('#roleList').collapse('hide');
        $('#manageReportIndex').collapse('hide');
        $('#cUserList').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
        $('#cRoleList').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
        $('#cManageReportIndex').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
        $('#sUserList').toggle();
        $('#sRoleList').show();
        $('#sManageReportIndex').show();

    }
    else if (callingID == 'roleList') {
        $('#roleList').collapse('toggle');
        $('#userList').collapse('hide');
        $('#manageReportIndex').collapse('hide');
        $('#cRoleList').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
        $('#cUserList').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
        $('#cManageReportIndex').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
        $('#sUserList').show();
        $('#sRoleList').toggle();
        $('#sManageReportIndex').show();
    }
    else {
        $('#manageReportIndex').collapse('toggle');
        $('#roleList').collapse('hide');
        $('#userList').collapse('hide');
        $('#cManageReportIndex').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
        $('#cRoleList').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
        $('#cUserList').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
        $('#sUserList').show();
        $('#sRoleList').show();
        $('#sManageReportIndex').toggle();
    }
}

$(document).ready(function () {
    $("#userList").collapse({toggle: false});
    $("#roleList").collapse({toggle: false});
    $("#manageReportIndex").collapse({toggle: false});

    $('#cRoleList').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
    $('#cUserList').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');
    $('#cManageReportIndex').addClass('icon-circle-arrow-down').removeClass('icon-circle-arrow-up');

    $('#sUserList').show();
    $('#sRoleList').show();
    $('#sManageReportIndex').show();


    var prev = $('#previousExpanded').val();
    var iconId = prev.charAt(0).toUpperCase() + prev.slice(1);
    if (prev != 'none') {
        $('#' + prev).collapse('show');
        $('#' + 'c' + iconId).addClass('icon-circle-arrow-up').removeClass('icon-circle-arrow-down');
        $('#' + 's' + iconId).hide();
    }
})

