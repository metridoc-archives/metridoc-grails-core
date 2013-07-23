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

function triggerFilter() {
    var searchText = $('#searchControllers').val();
    var roleFilter = $('#roleFilter').val();
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
    var searchField = $('#searchControllers').val();
    //alert(searchField);
    $('#searchFilter').val(searchField);
    //alert($('#searchFilter').val());

    var roleField = $('#roleFilter').val();
    //alert(searchField);
    $('#rFilter').val(roleField);

}


$(document).ready(function () {
    $('#searchControllers').keyup(function () {
        var searchText = $('#searchControllers').val();
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

        var filterValue = $('#roleFilter').val();
        table = document.getElementById("controllerTable");
        var selRow, lastCell;
        //When changing search, boxes should be unchecked
        for (i = 2, j = table.rows.length; i <= j; i++) {
            //cellText = $('#controllerTable tr .popRoles').attr("data-content");
            //alert(cellText);

            selRow = $('#controllerTable tr:nth-child(' + i + ')');
            lastCell = selRow.find('td:last');
            cellText = lastCell.html();
            if (cellText.indexOf(filterValue) == -1) {
                //alert("YES");
                $('#controllerTable tr').slice(i - 1, i).hide();

            }


        }


    });

    $(function () {
        $('#roleFilter').change(function () {
            var filterValue = $('#roleFilter').val();
            var cellText;
            var table = document.getElementById("controllerTable");
            var i, j;
            var selRow, lastCell;
            //When changing search, boxes should be unchecked
            $('input[name=selectAll]').prop("checked", false);
            $('input[name=controllerNames]').prop("checked", false);
            for (i = 2, j = table.rows.length; i <= j; i++) {
                //cellText = $('#controllerTable tr .popRoles').attr("data-content");
                //alert(cellText);

                selRow = $('#controllerTable tr:nth-child(' + i + ')');
                lastCell = selRow.find('td:last');
                cellText = lastCell.html();
                if (cellText.indexOf(filterValue) != -1) {
                    //alert("YES");
                    $('#controllerTable tr').slice(i - 1, i).show();

                }
                else {
                    //alert("ERROR");
                    $('#controllerTable tr').slice(i - 1, i).hide();


                }


            }

            var searchText = $('#searchControllers').val();
            table = document.getElementById("controllerTable");
            //When changing search, boxes should be unchecked
            for (i = 1, j = table.rows.length; i < j; i++) {
                cellText = table.rows[i].cells[1].innerHTML.replace('<a href=\"/metridoc-core/manageReport/show/', "");
                cellText = cellText.replace('</a>', "");
                cellText = cellText.replace(/[a-zA-Z]*">/, "");


                if (cellText.indexOf(searchText) == -1) {
                    $('#controllerTable tr').slice(i, i + 1).hide();

                }
            }


        });
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

});

$(function () {
    $("[rel='tooltip']").tooltip();
});

$(function () {
    $("[rel='popover']").popover();
});

$(document).ready(function () {
    $(function () {
        $(".popRoles")
            .mouseover({
                offset: 10
            })

    })
});

