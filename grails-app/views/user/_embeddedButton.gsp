<g:set var="type" value="${type ?: "submit"}"/>

<button class="btn ${buttonClass}" type="${type}" name="${action}" onclick="this.form.submited=this.name">
    <i class="${icon}"></i> ${content}
</button>