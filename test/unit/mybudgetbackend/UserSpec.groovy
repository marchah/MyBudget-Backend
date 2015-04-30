package mybudgetbackend

import bcrypt.BcryptCodec
import grails.test.mixin.TestFor
import mybudget.User

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(User)
class UserSpec extends ADomainSpec {

    def setup() {
        mockCodec(BcryptCodec)
    }

    void 'test constraint'() {
        given:
        mockForConstraintsTests User

        when: 'the user fields are null'
        def user = new User()

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['displayName'] == 'nullable'
        user.errors['login'] == 'nullable'
        user.errors['email'] == 'nullable'
        user.errors['password'] == 'nullable'

        when: 'the user fields are blank'
        user.displayName = ''
        user.login = ''
        user.email = ''
        user.password = ''

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['displayName'] == 'blank'
        user.errors['login'] == 'blank'
        user.errors['email'] == 'blank'
        user.errors['password'] == 'blank'

        when: 'the user email is incorrect'
        user.displayName = 'User'
        user.login = 'user'
        user.email = 'user@emailcom'
        user.password = 'totoauzoo'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['email'] == 'email'

        when: 'the user displayName/Login are incorrect 1/2'
        user.displayName = '1234'
        user.login = 'user'
        user.email = 'user@email.com'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['displayName'] == 'size'
        user.errors['login'] == 'size'


        when: 'the user displayName/Login are incorrect 2/2'
        user.displayName = '123999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999'
        user.login = 'user111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['displayName'] == 'size'
        user.errors['login'] == 'size'

        when: 'the user login doesn\'t matches'
        user.displayName = 'User1'
        user.login = '1 A z'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['login'] == 'matches'

        when: 'the user login isn\'t unique'
        user.login = 'user1'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['login'] == 'unique'


        when: 'the user password is incorrect 1/2'
        user.password = '1234'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['password'] == 'size'


        when: 'the user password is incorrect 2/2'
        user.password = '12344444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444'

        then: 'validation should fail'
        !user.validate()
        user.hasErrors()
        user.errors['password'] == 'size'

        when: 'the user token is OK'
        user = new User()
        user.displayName = 'User42'
        user.login = 'user42'
        user.password = 'totoauzoo'
        user.email = 'user42@email.com'

        then: 'validation should success'
        user.validate()
        !user.hasErrors()
    }

    void 'test save'(){
        when:
        def user = new User(displayName: 'User42', login: 'user42', email: 'user42@email.com', password: 'totoauzoo')
        user.updateToken()
        user.passwordHash = user.password.encodeAsBcrypt()
        user.save(flush: true)

        then:
        User.findById(user.id) == user
    }

    void 'test update'(){
        when:
        def user = new User(displayName: 'User42', login: 'user42', email: 'user42@email.com', password: 'totoauzoo')
        user.updateToken()
        user.passwordHash = user.password.encodeAsBcrypt()
        user.save(flush: true)

        user.displayName = 'TOTO42'

        user.save(flush: true)

        then:
        User.findById(user.id) == user
    }

    void 'test select'(){
        when:
        def user3 = new User(displayName: 'User3', login: 'user3', email: 'user3@email.com', password: 'totoauzoo')
        user3.updateToken()
        user3.passwordHash = user3.password.encodeAsBcrypt()
        user3.save(flush: true)

        def user4 = new User(displayName: 'User4', login: 'user4', email: 'user4@email.com', password: 'totoauzoo')
        user4.updateToken()
        user4.passwordHash = user4.password.encodeAsBcrypt()
        user4.save(flush: true)

        then:
        def listUsers = User.findAll()
        listUsers.size() == 4

        listUsers.get(0) == user1
        listUsers.get(1) == user2
        listUsers.get(2) == user3
        listUsers.get(3) == user4
    }

    void 'test delete'(){
        when:
        def user = new User(displayName: 'User42', login: 'user42', email: 'user42@email.com', password: 'totoauzoo')
        user.updateToken()
        user.passwordHash = user.password.encodeAsBcrypt()
        user.save(flush: true)

        then:
        User.findById(user.id) == user
        user.delete(flush: true)
        User.findById(user.id) == null
    }
}
