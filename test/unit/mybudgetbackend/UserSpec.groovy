package mybudgetbackend

import grails.test.mixin.TestFor
import mybudget.User
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(User)
class UserSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void 'test constraint'() {
        given:
        mockForConstraintsTests User

        when: 'the user token is null'
        def user = new User()
        user.displayName = 'User1'
        user.login = 'user1'
        user.password = 'totoauzoo'
        user.email = 'user1@email.com'
        user.token = null

        then: 'validation should success'
        user.validate()
        !user.hasErrors()
    }
}
