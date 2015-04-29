package mybudgetbackend

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.Action
import mybudget.Recurring
import mybudget.Type
import mybudget.User

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Recurring)
class RecurringSpec extends ADomainSpec {

    void 'test constraint'() {
        given:
        mockForConstraintsTests Recurring

        when: 'the recurring createDate is null'
        def recurring = new Recurring()
        recurring.createDate = null

        then: 'validation should fail'
        !recurring.validate()
        recurring.hasErrors()
        recurring.errors['createDate'] == 'nullable'

        when: 'the recurring nextDate is null'
        recurring.createDate = new Date()
        recurring.nextDate = null

        then: 'validation should fail'
        !recurring.validate()
        recurring.hasErrors()
        recurring.errors['nextDate'] =='nullable'

        when: 'the recurring endDate is null'
        recurring.createDate = new Date()
        recurring.nextDate = new Date()
        recurring.endDate = null
        recurring.action = action1

        then: 'validation should success'
        recurring.validate()
        !recurring.hasErrors()
        //println recurring.errors
        //recurring.errors['nextDate'] == 'blank'
    }
}
