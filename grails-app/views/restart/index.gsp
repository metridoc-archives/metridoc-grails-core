<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 2/7/13
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>

<md:report module="codeMirrorShell">
    <g:render template="/user/tabs"/>
    <g:form action="run" class="form-horizontal">
        <div class="control-group">

            <label for="workDirectory" class="control-label">Work Directory:</label>

            <div class="controls">
                <g:textField name="workDirectory" value="${workDirectory}" placeholder="/foo/bar" required=""/>
            </div>

            <label for="command" class="control-label">Command To Run:</label>

            <div class="controls">
                <g:textArea id="code" name="command" value="${command}" placeholder="grails run-war" required=""/>
            </div>

            <div class="controls">
                <button type="submit" class="btn"><i class="icon-refresh"></i> Restart</button>
            </div>
        </div>
    </g:form>
</md:report>
