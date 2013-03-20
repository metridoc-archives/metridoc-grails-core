<input id="${name}" name="${name}" type="file" style="display: none" />
<div class="input-append">
    <input id="${name}Path" name="${name}Path" type="text" disabled="true"/>
    <a class="btn" onclick="$('input[id=${name}}]').click();">Browse</a>
</div>
<g:javascript>
    $('input[id=${name}]').change(function(){
        var fileName = $(this).val().replace("C:\\fakepath\\", "");
        $('#${name}Path').val(fileName);
    });
</g:javascript>