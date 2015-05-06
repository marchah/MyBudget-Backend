package mybudgetbackend

import grails.test.mixin.TestFor
import mybudget.Token
import mybudget.User
import org.apache.http.HttpStatus
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(UserController)
class UserControllerSpec extends AControllerSpec {

    void 'test constraint'() {
        given:
        mockForConstraintsTests User

        when: 'the user fields are null'
        def user = new UserCommand()

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()

        //user.errors['displayName'] == 'nullable'
        //user.errors['login'] == 'nullable'
        //user.errors['email'] == 'nullable'
        //user.errors['password'] == 'nullable'

        when: 'the user fields are blank'
        user.displayName = ''
        user.login = ''
        user.email = ''
        user.password = ''

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['displayName'] == 'blank'
        //user.errors['login'] == 'blank'
        //user.errors['email'] == 'blank'
        //user.errors['password'] == 'blank'

        when: 'the user email is incorrect'
        user.displayName = 'User'
        user.login = 'user'
        user.email = 'user@emailcom'
        user.password = 'totoauzoo'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['email'] == 'email'

        when: 'the user displayName/Login are incorrect 1/2'
        user.displayName = '1234'
        user.login = 'user'
        user.email = 'user@email.com'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['displayName'] == 'size'
        //user.errors['login'] == 'size'


        when: 'the user displayName/Login are incorrect 2/2'
        user.displayName = '123999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999'
        user.login = 'user111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['displayName'] == 'size'
        //user.errors['login'] == 'size'

        when: 'the user login doesn\'t matches'
        user.displayName = 'User1'
        user.login = '1 A z'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['login'] == 'matches'

        when: 'the user password is incorrect 1/2'
        user.password = '1234'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['password'] == 'size'


        when: 'the user password is incorrect 2/2'
        user.password = '12344444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        //user.errors['password'] == 'size'

        when: 'the user token is OK'
        user = new UserCommand()
        user.displayName = 'User42'
        user.login = 'user42'
        user.password = 'totoauzoo'
        user.email = 'user42@email.com'

        then: 'validation should success'
        user.validate()
        !user.hasErrors()
    }

    void "test signup [empty]"() {
        when:
        controller.signup(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test signup [login not unique]"() {
        when:
        def cmd = new UserCommand(displayName: 'User1', login: 'user1', password: 'totoauzoo', email: 'user1@email.com')
        controller.signup(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY
    }

    void "test signup"() {
        when:
        def cmd = new UserCommand(displayName: 'User42', login: 'user42', password: 'totoauzoo', email: 'user42@email.com')
        controller.signup(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 2
        response.json.login == cmd.login
        def user = User.findByLogin(cmd.login)
        response.json.token == (Token.findByUser(user)).token
    }
}
