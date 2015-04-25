class UrlMappings {

	static mappings = {
        /*"/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }*/

        "/"(view:"/index")
        "500"(view:'/error')

        "/api/v1/signup"(controller: "authentication", action: "signup", method: "POST", namespace: 'v1')
        "/api/v1/signin"(controller: "authentication", action: "signin", method: "POST", namespace: 'v1')

        "/api/v1/type"(controller: "type", action: "create", method: "POST", namespace: 'v1')
        "/api/v1/types"(controller: "type", action: "index", method: "GET", namespace: 'v1')
	}
}
