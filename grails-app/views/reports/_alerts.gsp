<g:each in="${flash.alerts}" var="alertVar">
    <div class="alert alert-block">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
        ${alertVar}
    </div>
</g:each>

<g:each in="${flash.infos}" var="infoVar">
    <div class="alert alert-info alert-block">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    ${infoVar}</div>
</g:each>

<g:if test="${flash.alert}">
    <div class="alert alert-block">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    ${flash.alert}</div>
</g:if>

<g:if test="${flash.info}">
    <div class="alert alert-info alert-block">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    ${flash.info}</div>
</g:if>

