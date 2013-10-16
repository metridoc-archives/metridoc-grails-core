<%@ page import="metridoc.core.ShiroRole" %>
<div class="control-group">
    <label for="roles" class="control-label">
        Roles:
    </label>
    <g:if test="${target}">
        <g:set var="selectedRoles" value="${target.roles?.collect { it.name }}"/>
    </g:if>
    <div class="controls">
        <g:if test="${disabled}">
            <select name="roles" multiple="multiple" size="5" disabled="${disabled ?: false}">
                <g:each in="${ShiroRole.list()}" var="shiroRole">
                    <g:if test="${selectedRoles?.contains(shiroRole.name)}">
                        <option value="${shiroRole.name}" selected="selected">${shiroRole.name}</option>
                    </g:if>
                    <g:else>
                        <option value="${shiroRole.name}">${shiroRole.name}</option>
                    </g:else>
                </g:each>
            </select>
        </g:if>
        <g:else>
            <select name="roles" multiple="multiple" size="5">
                <g:each in="${ShiroRole.list()}" var="shiroRole">
                    <g:if test="${selectedRoles?.contains(shiroRole.name)}">
                        <option value="${shiroRole.name}" selected="selected">${shiroRole.name}</option>
                    </g:if>
                    <g:else>
                        <option value="${shiroRole.name}">${shiroRole.name}</option>
                    </g:else>
                </g:each>
            </select>
        </g:else>
    </div>
</div>