/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

function deleteMapping(mappingId) {
    if (window.confirm("are you sure you want to delete this mapping?")) {
        $('#mdForm_' + mappingId).submit();
    }
}

function showGroupForm() {
    $('#createGroupForm').toggle();
    $('#createGroup').toggleClass('icon-plus-sign icon-circle-arrow-up')
}

function changeIcon(callingID) {
    if (callingID == 'groupList') {
        $('#cGroupList').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
    }

    else {
        $('#cLdapConfig').toggleClass('icon-circle-arrow-down icon-circle-arrow-up');
    }
}

$(document).ready(function () {
    $("#ldapConfig").collapse({toggle: false});
    $("#groupList").collapse({toggle: false});
    var prev = $('#previousExpanded').val();
    var iconId = prev.charAt(0).toUpperCase() + prev.slice(1);
    if (prev != 'none') {
        $('#' + prev).collapse('show');
        $('#' + 'c' + iconId).addClass('icon-circle-arrow-up').removeClass('icon-circle-arrow-down');

    }
})

