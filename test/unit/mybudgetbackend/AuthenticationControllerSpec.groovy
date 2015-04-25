package mybudgetbackend

import bcrypt.BcryptCodec
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.User
import mybudgetbackend.transfert.AuthenticationCredentials
import org.apache.http.HttpStatus
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(AuthenticationController)
@Mock(User)
class AuthenticationControllerSpec extends Specification {

    def user

    def setup() {
        mockCodec(BcryptCodec)

        user = new User(displayName: 'User2', login: 'user2', password: 'totoauzoo', email: 'user2@email.com')
        user.passwordHash = user.password.encodeAsBcrypt()
        user.updateToken()

        user.save(flush: true)
    }

    def cleanup() {
        user.delete(flush: true)
    }

    void "test signup [empty]"() {
        when:
        controller.signup(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test signup"() {
        when:
        controller.signup(new User(displayName: 'User1', login: 'user1', password: 'totoauzoo', email: 'user1@email.com', token: null))

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 2
        response.json.login == 'user1'
        response.json.token == (User.findByLogin('user1')).token
    }

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
        response.getJson().findAll().size() == 2
        response.json.login == 'user2'
        response.json.token == (User.findByLogin('user2')).token
    }

    void "test signin login"() {
        when:
        controller.signin(new AuthenticationCredentials(login: "user2", password: "totoauzoo"))

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 2
        response.json.login == 'user2'
        response.json.token == (User.findByLogin('user2')).token
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
