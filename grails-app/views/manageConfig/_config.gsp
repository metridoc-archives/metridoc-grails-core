<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 2/4/13
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>


<md:header>Metridoc Configuration</md:header>
<g:form class="form-horizontal" enctype="multipart/form-data">
    <div class="control-group">

        %{--TODO: move all scripts and css to separate js and css files--}%
        <div class="controls">
            <input id="metridocConfig" name="metridocConfig" type="file" style="display: none"/>

            <div class="input-append">
                <!--suppress HtmlFormInputWithoutLabel -->
                <input id="metridocConfigPath" name="metridocConfigPath" type="text" disabled="true"/>
                <a class="btn" onclick="$('input[id=metridocConfig]').click();">Browse</a>
            </div>
            <g:javascript>
                $('input[id=metridocConfig]').change(function () {
                    var fileName = $(this).val().replace("C:\\fakepath\\", "");
                    $('#metridocConfigPath').val(fileName);
                });
            </g:javascript>
        </div>

        <div class="controls">
            <g:if test="${metridocConfigExists}">
                <button class="btn" type="submit" name="_action_download">
                    <i class="icon-download-alt"></i> Download
                </button>
            </g:if>
            <button class="btn" type="submit" name="_action_upload">
                <i class="icon-upload-alt"></i> Upload
            </button>
        </div>
    </div>
</g:form>
