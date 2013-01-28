<g:set var="type" value="${type ?: "submit"}"/>

<button class="btn ${buttonClass}" type="${type}" name="${action}">
    <i class="${icon}"></i> ${content}
</button>