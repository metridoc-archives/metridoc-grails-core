$('#changePW').change(function () {
    if(this.checked){
        $('#changePassword').css({
            display: 'block'
        });
    }else{
        $('#changePassword').css({
            display: 'none'
        });
    }
})
