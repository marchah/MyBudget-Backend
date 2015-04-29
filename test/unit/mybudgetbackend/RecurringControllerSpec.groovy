package mybudgetbackend

import grails.test.mixin.TestFor
import mybudget.Action
import mybudget.Recurring
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Specification

import javax.xml.bind.DatatypeConverter

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RecurringController)
class RecurringControllerSpec extends AControllerSpec {

    void "test index [basic]"() {
        when:
        controller.index()

        then:
        response.status == HttpStatus.SC_OK

        response.getJson().findAll().size() == 1

        response.json[0].id == recurring1.id
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json[0].createDate).getTime()) == dateFormater.format(recurring1.createDate)
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json[0].nextDate).getTime()) == dateFormater.format(recurring1.nextDate)
        JSONObject.NULL.equals(response.json[0].endDate)
        response.json[0].action.id == recurring1.action.id
    }


    void "test create [null]"() {
        when:
        controller.create(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test create [contraints empty]"() {
        when:
        def cmd = new RecurringCommand()
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "createDate"
        JSONObject.NULL.equals(response.json.errors[0]["rejected-value"])

        response.json.errors[1].field == "nextDate"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test create [contraints createDate blank]"() {
        when:
        def cmd = new RecurringCommand(createDate: '')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "createDate"
        response.json.errors[0]["rejected-value"] == ""
        response.json.errors[1].field == "nextDate"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test create [contraints createDate matches]"() {
        when:
        def cmd = new RecurringCommand(createDate: 'wrong date')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "createDate"
        response.json.errors[0]["rejected-value"] == "wrong date"
        response.json.errors[1].field == "nextDate"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test create [contraints nextDate blank]"() {
        when:
        def cmd = new RecurringCommand(createDate: '10/12/2015', nextDate: '')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "nextDate"
        response.json.errors[0]["rejected-value"] == ""
    }

    void "test create [contraints nextDate matches]"() {
        when:
        def cmd = new RecurringCommand(createDate: '10/12/2015', nextDate: 'wrong date')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "nextDate"
        response.json.errors[0]["rejected-value"] == "wrong date"
    }

    void "test create [unknow action 1/2]"() {
        when:
        def cmd = new RecurringCommand(createDate: '10/12/2015', nextDate: '11/12/2015')

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test create [unknow action 2/2]"() {
        when:
        def cmd = new RecurringCommand(createDate: '10/12/2015', nextDate: '11/12/2015', idAction: 42)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test create [forbidden]"() {
        when:
        def cmd = new RecurringCommand(createDate: '10/12/2015', nextDate: '11/12/2015', idAction: action3.id)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test create [basic]"() {
        when:
        def cmd = new RecurringCommand(createDate: '10/12/2015', nextDate: '11/12/2015', idAction: action1.id)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == 3
        response.json.action.id == action1.id
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json.createDate).getTime()) == cmd.createDate
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json.nextDate).getTime()) == cmd.nextDate
        JSONObject.NULL.equals(response.json.endDate)

        def listRecurrings = Recurring.findAll()

        listRecurrings.size() == 3
        listRecurrings.get(0) == recurring1
        listRecurrings.get(1) == recurring2
        listRecurrings.get(2).id == 3
        listRecurrings.get(2).action.id == cmd.idAction
        dateFormater.format(listRecurrings.get(2).createDate) == cmd.createDate
        dateFormater.format(listRecurrings.get(2).nextDate) == cmd.nextDate
        listRecurrings.get(2).endDate == null

        def action = Action.findById(action1.id)

        action.recurring.id == 3
    }

    void "test update [null]"() {
        when:
        controller.update(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [contraints empty]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id)
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "createDate"
        JSONObject.NULL.equals(response.json.errors[0]["rejected-value"])

        response.json.errors[1].field == "nextDate"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test update [contraints createDate blank]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "createDate"
        response.json.errors[0]["rejected-value"] == ""
        response.json.errors[1].field == "nextDate"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test update [contraints createDate matches]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: 'wrong date')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "createDate"
        response.json.errors[0]["rejected-value"] == "wrong date"
        response.json.errors[1].field == "nextDate"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test update [contraints nextDate blank]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '10/12/2015', nextDate: '')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "nextDate"
        response.json.errors[0]["rejected-value"] == ""
    }

    void "test update [contraints nextDate matches]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '10/12/2015', nextDate: 'wrong date')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "nextDate"
        response.json.errors[0]["rejected-value"] == "wrong date"
    }

    void "test update [unknow action 1/2]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '10/12/2015', nextDate: '11/12/2015')

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [unknow action 2/2]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '10/12/2015', nextDate: '11/12/2015', idAction: 42)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [forbidden 1/2]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '10/12/2015', nextDate: '11/12/2015', idAction: action3.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test update [forbidden 2/2]"() {
        when:
        def cmd = new RecurringCommand(id: recurring2.id, createDate: '10/12/2015', nextDate: '11/12/2015', idAction: action3.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test update [basic]"() {
        when:
        def cmd = new RecurringCommand(id: recurring1.id, createDate: '01/12/2015', nextDate: '08/12/2015', idAction: action2.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == recurring1.id
        response.json.action.id == cmd.idAction
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json.createDate).getTime()) == cmd.createDate
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json.nextDate).getTime()) == cmd.nextDate
        JSONObject.NULL.equals(response.json.endDate)

        def listRecurrings = Recurring.findAll()

        listRecurrings.size() == 2
        listRecurrings.get(0).id == recurring1.id
        listRecurrings.get(0).action.id == cmd.idAction
        dateFormater.format(listRecurrings.get(0).createDate) == cmd.createDate
        dateFormater.format(listRecurrings.get(0).nextDate) == cmd.nextDate
        listRecurrings.get(0).endDate == null
        listRecurrings.get(1) == recurring2

        def action = Action.findById(action2.id)

        action.recurring.id == recurring1.id
    }

    void "test delete [invalid id]"() {
        when:
        controller.delete(42)
        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test delete [with not owner]"() {
        when:
        controller.delete(recurring2.id)
        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test delete [basic]"() {
        when:
        controller.delete(recurring1.id)
        then:
        response.status == HttpStatus.SC_OK

        def listTypes = Type.findAll()
        listTypes.size() == 3
        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
        listTypes.get(2) == typeParent3

        def listUsers = User.findAll()
        listUsers.size() == 2
        listUsers.get(0) == user1
        listUsers.get(1) == user2

        def listActions = Action.findAll()
        listActions.size() == 3
        listActions.get(0) == action1
        listActions.get(1) == action2
        listActions.get(2) == action3

        def listRecurrings = Recurring.findAll()
        listRecurrings.size() == 1
        listRecurrings.get(0) == recurring2
    }
}
