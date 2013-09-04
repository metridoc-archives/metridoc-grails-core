<span class="linkContainer">
    <div style="margin-top: 20px">
        <md:header>${category.appName} <i class="${category.iconName}"></i>
            <a href="#" onclick="showApps(this.id)" class="categoryHeader">
                <i class="icon-minus-sign"></i>
            </a>

        </md:header>
    </div>

    <g:if test="${!category.controllerInfo}">
        <ul class="undecorated">
            <li>No applications available</li>
        </ul>
    </g:if>
    <div class="categoryDiv">
        <g:each in="${category.controllerInfo}" var="controller" status="i">
            <ul class="undecorated">
                <li><a href="${createLink(url: controller.link)}">${controller.title}</a><g:if
                        test="${controller.description}"> - </g:if>${controller.description}</li>
            </ul>
        </g:each>
    </div>
</span>
