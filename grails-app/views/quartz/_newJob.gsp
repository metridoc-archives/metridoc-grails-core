<md:saveModal action="newJob" name="newJob" header="Create New Job" formClass="form-horizontal">
    <div class="control-group">
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
</md:saveModal>