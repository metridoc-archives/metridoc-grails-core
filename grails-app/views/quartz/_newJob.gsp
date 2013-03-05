<div id="newJobModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

        <h3 id="myModalLabel">Create New Job or Template</h3>
    </div>


    <ul id="newJobTab" class="nav nav-tabs">
        <li class="active">
            <a href="#newJob" data-toggle="tab">New Job</a>
        </li>
        <li>
            <a href="#newTemplate" data-toggle="tab">New Template</a>
        </li>
    </ul>

    <div id="newJobTabContent" class='tab-content'>
        <div class="tab-pane fade active in" id="newJob">
            <g:form action="newJob">
                <div class="modal-body">

                    <div>
                        Not implemented yet
                    </div>

                </div>

                %{--<div class="modal-footer">--}%
                    %{--<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>--}%
                    %{--<button class="btn" type="submit" value="Save" id="addNewJobButton">Add Job</button>--}%
                %{--</div>--}%
            </g:form>
        </div>
        <div class="tab-pane fade" id="newTemplate">
            <g:form action="newTemplate">
                <div class="modal-body">

                    <div>
                        Not Implemented Yet
                    </div>

                </div>

                %{--<div class="modal-footer">--}%
                    %{--<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>--}%
                    %{--<button class="btn" type="submit" value="Save" id="addNewTemplateButton">Add Template</button>--}%
                %{--</div>--}%
            </g:form>
        </div>
    </div>



</div>