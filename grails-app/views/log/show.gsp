<md:report>
    <g:form class="form-inline">
        <a id="scrollTop" href="#" class="btn"><i class="icon-arrow-up"></i> Top</a>
        <a id="scrollBottom" href="#" class="btn"><i class="icon-arrow-down"></i> Bottom</a>
        <g:select name="logFile" from="${logFiles}" value="${initialValue}"/>
        <label class="checkbox">
            <input type="checkbox" id="doStreaming" name="doStreaming"/> Stream
        </label>

    </g:form>

    <div id="metridocLogsContainer">
        <div id="metridocLogs">
            <tmpl:plainLog/>
        </div>
    </div>

</md:report>