<g:each in="${flash.alerts}" var="alertVar">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-error']">${alertVar}</g:render>
</g:each>

<g:each in="${flash.infos}" var="infoVar">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-info']">${infoVar}</g:render>
</g:each>

<g:if test="${flash.alert}">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-error']">${flash.alert}</g:render>
</g:if>

<g:if test="${flash.info}">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-info']">${flash.info}</g:render>
</g:if>

<g:if test="${flash.message}">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-info']">${flash.message}</g:render>
</g:if>

