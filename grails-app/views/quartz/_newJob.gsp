<div id="newJobModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalLabel"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="modalLabel">Create New Job</h3>
    </div>

    <g:form action="newJob" class="form-horizontal">
        <div class="modal-body">
            <div class="control-group">
                <label for="description" class="control-label">Description:</label>
                <div class="controls">
                    <textarea name="description" id="description"></textarea>
                </div>
                <label for="url" class="control-label">Script to run (url):</label>
                <div class="controls">
                    <input type="text" name="url" id="url" required=""/>
                </div>
                <label for="jobName" class="control-label">Job name:</label>
                <div class="controls">
                    <input type="text" name="jobName" id="jobName" required=""/>
                </div>

            </div>

        </div>

    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
        <button class="btn" type="submit" id="addNewJobButton">Add Job</button>
    </div>
    </g:form>



</div>