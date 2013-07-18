/**
 * Created with IntelliJ IDEA.
 * User: intern
 * Date: 7/18/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */

function protect() {
    $('#isProtected').prop("checked", true)
}

function unProtect() {
    $('#isProtected').prop("checked", false)
}

function getControllerNames() {
    var cNames = [];
    var table = document.getElementById("controllerTable");
    var i, j;
    var k = 0;
    var cellText;

    var boxes = $('input[name="controllerNames"]');

    for (i = 1, j = table.rows.length; i < j; i++) {

        //alert(table.rows[i].cells[1].innerHTML)
        //if(table.rows[i].getElementsByName('selectController').checked){
        if (boxes[i - 1].checked) {

            cellText = table.rows[i].cells[1].innerHTML.replace('<a href=\"/metridoc-core/manageReport/show/', "");
            cellText = cellText.replace('</a>', "");
            cellText = cellText.replace(/[a-zA-Z]*">/, "");
            cNames.push(cellText);
        }

    }
    $('#controllerNames').val(cNames);

}


$(document).ready(function () {
    $('#searchFilter').keyup(function () {
        var searchText = $('#searchFilter').val();
        var cellText;
        var table = document.getElementById("controllerTable");
        var i, j;
        //When changing search, boxes should be unchecked
        $('input[name=selectAll]').prop("checked", false);
        $('input[name=controllerNames]').prop("checked", false);

        for (i = 1, j = table.rows.length; i < j; i++) {
            cellText = table.rows[i].cells[1].innerHTML.replace('<a href=\"/metridoc-core/manageReport/show/', "");
            cellText = cellText.replace('</a>', "");
            cellText = cellText.replace(/[a-zA-Z]*">/, "");


            if (cellText.indexOf(searchText) != -1) {
                $('#controllerTable tr').slice(i, i + 1).show();

            }
            else {
                $('#controllerTable tr').slice(i, i + 1).hide();


            }


        }
    });
    $('input[name=selectAll]').click(function () {
        if (this.checked) {
            $('#controllerTable tr').slice(1).each(function () {
                if ($(this).is(':visible')) {
                    $(this).find('input:checkbox').prop("checked", true);
                }

            });

        }
        else {
            $('input[name=controllerNames]').prop("checked", false);

        }
    });

})