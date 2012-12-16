<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/28/12
  Time: 12:01 PM
  To change this template use File | Settings | File Templates.
--%>
<g:if test="${errorMessage}">
    <div class="ui-widget">
        <div class="ui-state-error ui-corner-all" style="padding: 0 0.7em;">
            <p>
                <strong>Error in ${capitalizedWorkflowName} Workflow:</strong>

            <pre>
                ${errorMessage}
            </pre>
        </p>
        </div>
    </div>
</g:if>
