<div id="template">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
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
</div>
