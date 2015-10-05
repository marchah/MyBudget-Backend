package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
import mybudget.Token
import mybudget.User
import mybudgetbackend.transfert.AuthenticationCredentials
import org.apache.http.HttpStatus
import security.PasswordEncoder

class AuthenticationController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    def passwordEncoder = new PasswordEncoder()

    AuthenticationController() {
        super(User)
    }

    def signin(AuthenticationCredentials signin) {
        if (signin == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (signin.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render signin.errors as JSON
        } else {
            // fetch user with same mail or same login
            def user = User.findByEmail(signin.username)
            if (!user) {
                user = User.findByLogin(signin.username)
            }
            if (user == null || !isPasswordValid(signin.password, user)) {
                render status: HttpStatus.SC_UNAUTHORIZED
            } else {
                user.token.updateToken()
                user.save()
                def ret = [
                        displayName: user.displayName,
                        login: user.login,
                        token: user.token.token
                ]
                render ret as JSON
            }
        }
    }

    private boolean isPasswordValid(String password, User user) {
        passwordEncoder.isPasswordValid(password, user.passwordHash)
    }

}
