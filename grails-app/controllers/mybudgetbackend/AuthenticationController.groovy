package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
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
        } else {
            // fetch user with same mail or same username
            def user = User.findByEmail(signin.email)
            if (!user) {
                user = User.findByLogin(signin.login)
            }
            if (user == null || !isPasswordValid(signin.password, user)) {
                render status: HttpStatus.SC_UNAUTHORIZED
            } else {
                user.updateToken()
                user.save()
                def ret = [
                        login: user.login,
                        token: user.token
                ]
                render ret as JSON
            }
        }
    }

    def signup(User user) {
        if (user == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (user.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render user.errors as JSON
        } else {
            user.passwordHash = user.password.encodeAsBcrypt()
            user.updateToken()
            user.save(flush:true)
            def ret = [
                login: user.login,
                token: user.token
            ]
            render ret as JSON
        }
    }

    private boolean isPasswordValid(String password, User user) {
        passwordEncoder.isPasswordValid(password, user.passwordHash)
    }

}
