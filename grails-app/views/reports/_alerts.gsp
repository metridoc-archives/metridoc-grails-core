<g:each in="${flash.alerts}" var="alertVar">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-error']">${alertVar}</g:render>
</g:each>

<g:each in="${flash.infos}" var="infoVar">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-info']">${infoVar}</g:render>
</g:each>

<g:each in="${flash.messages}" var="messageVar">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-info']">${messageVar}</g:render>
</g:each>

<g:each in="${flash.warnings}" var="warningVar">
    <g:render template="/reports/alert" plugin="metridocCore" model="[alertClass: 'alert-info']">${warningVar}</g:render>
</g:each>

