<span class="linkContainer">
    <div style="margin-top: 20px">
        <md:header>${category.getKey().name} <i class="${category.getKey().iconName}"></i>
            <a href="#" onclick="showApps(this.id)" class="categoryHeader">
                <i class="icon-minus-sign"></i>
            </a>

        </md:header>
    </div>

    <g:if test="${!category.getValue()}">
        <ul class="undecorated">
            <li>No applications available</li>
        </ul>
    </g:if>
    <div class="categoryDiv">
        <g:each in="${category.getValue()}" var="controller" status="i">
            <ul class="undecorated">
                <li><a href="${createLink(url: controller.controllerPath)}">${controller.appName}</a><g:if
                        test="${controller.appDescription}">-</g:if>${controller.appDescription}</li>
            </ul>
        </g:each>
    </div>
</span>
