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
/**
 * adds form functionality to the COUNTER form
 */
$('#getCounterReport').click(function () {
        $('#counter-form > form').submit()
    }
);
/**
 * adds click functionality to the legend link
 */
$('#counterLegend').click(function () {
        $('#dialog').dialog('open');
    }
);
/**
 * adds ajax to make reports list rendered dynamically
 */
$('#year').change(function () {
    $.getJSON('getDirectoryContentInJSON', {selected:$("select option:selected").text()},
        function (data) {
            var items = [];
            $.each(data, function (key, val) {
                if (items.length == 0) {
                    items.push('<div> <input type="radio" name="counter_group" value="' + key + '"checked="checked">&nbsp;' + val + '</input></div>');
                } else {
                    items.push('<div> <input type="radio" name="counter_group" value="' + key + '">&nbsp;' + val + '</input></div>');
                }
            });

            var files = items.sort().join(' ');
            $('#filesOfYear').html(files);
        });
});

$(document).ready(function () {
})



