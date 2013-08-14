function deleteRole(roleId) {
    $('#mdForm_' + roleId).submit()
}

function showRoleForm() {
    $('#createRoleForm').toggle();
    $('#createRole').toggleClass('icon-plus-sign icon-circle-arrow-up')


}
