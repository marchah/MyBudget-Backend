package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.Validateable
import groovy.transform.ToString
import mybudget.Token
import mybudget.User
import org.apache.http.HttpStatus

class UserController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    UserController() {
        super(User)
    }

    def signup(UserCommand cmd) {
        if (cmd == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else {
            def user = new User(displayName: cmd.displayName, login: cmd.login, email: cmd.email)
            user.passwordHash = cmd.password.encodeAsBcrypt()
            user.token = new Token()
            user.token.updateToken()
            user.validate()
            if (user.hasErrors()) {
                response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                render user.errors as JSON
            } else {
                user.save(flush: true)
                def ret = [
                        login: user.login,
                        token: user.token.token
                ]
                render ret as JSON
            }
        }
    }
}
@Validateable
@ToString
public class UserCommand {
    long id
    String displayName
    String login
    String password
    String email

    static constraints = {
        id nullable: true
        displayName blank: false, size: 5..30
        login blank: false, size: 5..15, matches: '^[a-zA-Z0-9_]+$'
        password blank: false, size: 5..15
        email blank: false, email: true
    }
}
