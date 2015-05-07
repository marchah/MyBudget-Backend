package mybudgetbackend

import grails.test.mixin.TestFor
import mybudget.Date
import mybudget.Recurring

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
        recurring.createDate = new java.util.Date()
        recurring.nextDate = null

        then: 'validation should fail'
        !recurring.validate()
        recurring.hasErrors()
        recurring.errors['nextDate'] =='nullable'

        when: 'the recurring endDate is null'
        recurring.createDate = new java.util.Date()
        recurring.nextDate = new java.util.Date()
        recurring.endDate = null
        recurring.action = action1

        then: 'validation should success'
        recurring.validate()
        !recurring.hasErrors()
    }

    void 'test save'(){
        when:
        def recurring = new Recurring(createDate: new java.util.Date(), nextDate: new java.util.Date(), action: action1.id)
        recurring.save(flush: true)

        then:
        Recurring.findById(recurring.id) == recurring
    }

    void 'test update'(){
        when:
        def recurring = new Recurring(createDate: new java.util.Date(), nextDate: new java.util.Date(), action: action1)
        recurring.save(flush: true)

        recurring.createDate = new java.util.Date()
        recurring.action = action2

        recurring.save(flush: true)

        then:
        Recurring.findById(recurring.id) == recurring
    }

    void 'test select'(){
        when:
        def recurring1 = new Recurring(createDate: new java.util.Date(), nextDate: new java.util.Date(), action: action1)
        recurring1.save(flush: true)

        def recurring2 = new Recurring(createDate: new java.util.Date(), nextDate: new java.util.Date(), action: action2)
        recurring2.save(flush: true)

        then:
        def listRecurrings = Recurring.findAll()
        listRecurrings.size() == 2

        listRecurrings.get(0) == recurring1
        listRecurrings.get(1) == recurring2
    }

    void 'test delete'(){
        when:
        def recurring = new Recurring(createDate: new java.util.Date(), nextDate: new java.util.Date(), action: action1)
        recurring.save(flush: true)

        then:
        recurring.delete(flush: true)
        Recurring.findById(recurring.id) == null
    }
}
