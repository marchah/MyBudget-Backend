package mybudgetbackend

import bcrypt.BcryptCodec
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.Token
import mybudget.User
import mybudgetbackend.transfert.AuthenticationCredentials
import org.apache.http.HttpStatus
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(AuthenticationController)
class AuthenticationControllerSpec extends AControllerSpec {

    void "test signin [empty]"() {
        when:
        controller.signin(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test signin email"() {
        when:
        controller.signin(new AuthenticationCredentials(email: "user2@email.com", password: "totoauzoo"))

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 3
        response.json.login == user2.login
        response.json.token == (User.findByLogin('user2')).token.token
        response.json.displayName == user2.displayName
    }

    void "test signin login"() {
        when:
        controller.signin(new AuthenticationCredentials(login: "user2", password: "totoauzoo"))

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 3
        response.json.login == user2.login
        response.json.token == (User.findByLogin('user2')).token.token
        response.json.displayName == user2.displayName
    }

    void "test signin email failed [wrong email]"() {
        when:
        controller.signin(new AuthenticationCredentials(email: "user3@email.com", password: "totoauzoo"))

        then:
        response.status == HttpStatus.SC_UNAUTHORIZED
    }

    void "test signin login failed [wrong login]"() {
        when:
        controller.signin(new AuthenticationCredentials(login: "user3", password: "totoauzoo"))

        then:
        response.status == HttpStatus.SC_UNAUTHORIZED
    }

    void "test signin email failed [wrong password]"() {
        when:
        controller.signin(new AuthenticationCredentials(email: "user2@email.com", password: "unknow"))

        then:
        response.status == 401
    }

    void "test signin login failed [wrong password]"() {
        when:
        controller.signin(new AuthenticationCredentials(login: "user2", password: "unknow"))

        then:
        response.status == HttpStatus.SC_UNAUTHORIZED
    }
}
