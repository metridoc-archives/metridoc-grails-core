/**
 * Created with IntelliJ IDEA.
 * User: intern
 * Date: 7/18/13
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */

var newID = "category"
var newHeader = "header"
var newIcon = "icon"
$(function () {
    $('.categoryDiv').each(function (i) {
        $(this).attr({id: newID + i});
    });

    $('.categoryHeader').each(function (i) {
        $(this).attr({id: newHeader + i});
    });
    $('.icon-minus-sign').each(function (i) {
        $(this).attr({id: newIcon + i});
    });


});
function showApps(id) {
    var targetID = id.replace("header", "category")
    var iconID = id.replace("header", "icon")
    $('#' + targetID).toggle()
    $('#' + iconID).toggleClass('icon-minus-sign icon-plus-sign')
}

function assignID() {
    var newID = "category"
    var newHeader = "header"
    var newIcon = "icon"
    $('.categoryDiv').each(function (i) {
        $(this).attr({id: newID + i});
    });
    $('.categoryHeader').each(function (i) {
        $(this).attr({id: newHeader + i});
    });
    $('.icon-minus-sign').each(function (i) {
        $(this).attr({id: newIcon + i});
    });

};




