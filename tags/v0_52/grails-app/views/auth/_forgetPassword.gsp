<div id="template">
    <g:if test="${request.message}">
        <div class="message">${request.message}</div>
    </g:if>
    <g:if test="${!hideInput}">
    <g:form action="resetPassword">

        <div>
            <label for="email">Email Address :</label>
            <input type="text" name="emailAddress" value="" class="userInput" id="emailInput"/>
        </div>
        <br/>

        <div class="buttons">
            <label for="submit"></label>
            <input type="submit" value="Reset Password" id="button" name="submit"/>
        </div>

    </g:form>
        </g:if>
</div>
