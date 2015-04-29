package mybudgetbackend

import grails.test.mixin.TestFor
import mybudget.Action


/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Action)
class ActionSpec extends ADomainSpec {

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
