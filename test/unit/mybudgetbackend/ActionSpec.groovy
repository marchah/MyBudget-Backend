package mybudgetbackend

import grails.test.mixin.TestFor
import mybudget.Action
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Action)
class ActionSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

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
}
