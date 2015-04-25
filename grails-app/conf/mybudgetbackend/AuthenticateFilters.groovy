package mybudgetbackend

import mybudget.User
import org.apache.http.HttpStatus

class AuthenticateFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                if (!actionName.equals('signup') && !actionName.equals('signin')) {
                    def token = getAuthenticationToken(request)
                    def user

                    // put user into request scope
                    if (token) {
                        user = User.findByToken(token, [cache: true])
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

    def getAuthenticationToken(def request) {
        def authString = request.getHeader('Authorization')
        /*def username = null

        if (authString) {
            def encodedPair = authString - 'Basic '
            def decodedPair = new String(encodedPair.decodeBase64());
            def credentials = decodedPair.split(':')

            username = (credentials.size() > 0 ? credentials[0] : null)
        }

        username*/
        authString
    }
}
