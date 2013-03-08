<div id="${modalName}Modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="${modalName}Modal"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

        <h3 id="${modalName}ModalLabel">${modalHeader}</h3>
    </div>
    <g:form action="${modalFormAction}" id="${modalFormId}" controller="${modalFormController}" class="${modalFormClass}">
        <div class="modal-body">
            ${modalBody}
        </div>

        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
            <button class="btn" type="submit" value="Save" id="${modalName}SubmitButton">${modalSaveChanges}</button>
        </div>
    </g:form>
</div>