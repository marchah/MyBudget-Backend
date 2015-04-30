package mybudgetbackend

import mybudget.User
import org.apache.http.HttpStatus

class AuthenticateFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                if (!actionName.equals('signup') && !actionName.equals('signin')) {
                    def credentials = getAuthenticationCredentials(request)
                    def user

                    // put user into request scope
                    if (credentials) {
                        user = User.findByLoginAndToken(credentials[0], credentials[1], [cache: true])
                        if (user) {
                            request.user = user
                        }
                    }
                    if (!user) {
                        response.setStatus(HttpStatus.SC_UNAUTHORIZED)
                        return false
                    }
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }

    def getAuthenticationCredentials(def request) {
        def authString = request.getHeader('Authorization')
        def credentials = null

        if (authString) {
            def encodedPair = authString - 'Basic '
            def decodedPair = new String(encodedPair.decodeBase64());
            credentials = decodedPair.split(':')

            if (credentials == null || credentials.length != 2) {
                credentials = null
            }
        }
        credentials
    }
}
