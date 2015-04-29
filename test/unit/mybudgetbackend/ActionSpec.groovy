package mybudgetbackend

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.Action
import mybudget.Type
import mybudget.User
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Action)
//@Mock([User, Type])
class ActionSpec extends ADomainSpec {

    /*def user1
    def user2
    def typeParent1
    def typeParent2

    def setup() {
        user1 = new User(displayName: 'User1', login: 'user1', password: 'totoauzoo', email: 'user1@email.com')
        user1.save(flush: true)

        user2 = new User(displayName: 'User2', login: 'user2', password: 'totoauzoo', email: 'user2@email.com')
        user2.save(flush: true)

        typeParent1 = new Type(title: 'Parent 1', user: user1)
        typeParent1.save(flush: true)

        typeParent2 = new Type(title: 'Parent 2', user: user2)
        typeParent2.save(flush: true)
    }

    def cleanup() {
        user1.delete(flush: true)
        user2.delete(flush: true)

        typeParent1.delete(flush: true)
        typeParent2.delete(flush: true)
    }*/

    void 'test constraint'() {
        given:
        mockForConstraintsTests Action

        when: 'the action title is null'
        def action = new Action()
        action.title = null

        then: 'validation should fail'
        !action.validate()
        action.hasErrors()
        action.errors['title'] == 'nullable'

        when: 'the action title is blank'
        action.title = ''

        then: 'validation should fail'
        !action.validate()
        action.hasErrors()
        action.errors['title'] == 'blank'
    }

    void 'test save'(){
        when:
        def action = new Action(title: 'Action 1', amount: 42.5, date: new Date(), type: typeParent1, user: user1)
        action.save(flush: true)

        then:
        Action.findById(action.id) == action
    }

    void 'test update'(){
        when:
        def action = new Action(title: 'Action 1', amount: 42.5, date: new Date(), type: typeParent1, user: user1)
        action.save(flush: true)

        action.title = 'Action 2'
        action.amount = 42.42

        action.save(flush: true)

        then:
        Action.findById(action.id) == action
    }

    void 'test select'(){
        when:
        def action4 = new Action(title: 'Action 1', amount: 42.5, date: new Date(), type: typeParent1, user: user1)
        action4.save(flush: true)

        def action5 = new Action(title: 'Action 2', amount: 21.99, date: new Date(), type: typeParent1, user: user1)
        action5.save(flush: true)

        then:
        def listActions = Action.findAll()
        listActions.size() == 5

        listActions.get(0) == action1
        listActions.get(1) == action2
        listActions.get(2) == action3
        listActions.get(3) == action4
        listActions.get(4) == action5
    }

    void 'test delete'(){
        when:
        def action = new Action(title: 'Action 1', amount: 42.5, date: new Date(), type: typeParent1, user: user1)
        action.save(flush: true)

        then:
        action.delete(flush: true)
        Action.findById(action.id) == null
    }
}
