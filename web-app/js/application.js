/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
if (typeof jQuery !== 'undefined') {
    (function ($) {
        $('#spinner').ajaxStart(function () {
            $(this).fadeIn();
        }).ajaxStop(function () {
                $(this).fadeOut();
            });
    })(jQuery);
}

$('#runJenkins').click(function () {
        $('#toRunJDiv > form').submit()
    }
)

$(function () {
    var dialog = $("#dialog")
    if (dialog.length) {
        dialog.dialog({
            autoOpen:false,
            width:600,
            resizable:false
        });
    }
});


$(document).ready(function () {

    $('#metridocNavigation > ul > li').bind('mouseover', openSubMenu);
    $('#metridocNavigation >ul > li').bind('mouseout', closeSubMenu);

    function openSubMenu() {
        var offset = $(this).children('strong').offset();
        var height = $(this).outerHeight(true);
        var width = $(this).outerWidth(true);
        var dropDownWidth = $(this).children('ul').outerWidth(true);
        $(this).find('ul').css({
            visibility: 'visible',
            position: 'absolute',
            top:(offset.top + height) + "px",
            marginLeft:(offset.left - (dropDownWidth-width)/2) + "px"
        });
    };

    function closeSubMenu() {
        $(this).find('ul').css('visibility', 'hidden');
    }

    ;
});
