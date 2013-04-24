class UrlMappings {

    static mappings = {
        //TEMPLATE_URL_MAPPINGS
        "/"(controller: "home", action: "index")

        "/rest/$controllerForward/$actionForward?/$id?"(controller: "rest", action: "index")

        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        "500"(view: '/error')
        //TEMPLATE_URL_MAPPINGS
    }
}
