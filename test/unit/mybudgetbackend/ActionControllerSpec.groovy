package mybudgetbackend


import grails.test.mixin.TestFor
import mybudget.Action
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus
import org.codehaus.groovy.grails.web.json.JSONObject

import javax.xml.bind.DatatypeConverter

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ActionController)
class ActionControllerSpec extends AControllerSpec {

    void "test index [basic]"() {
        when:
        controller.index()

        then:
        response.status == HttpStatus.SC_OK

        response.getJson().findAll().size() == 2

        response.json[0].id == action1.id
        response.json[0].title == action1.title
        response.json[0].amount == action1.amount
        response.json[0].date.dayInMonth == 10
        response.json[0].date.weekInYear == 45
        response.json[0].date.month == 11
        response.json[0].date.year == 2012
        response.json[0].type.id == typeParent1.id
        response.json[0].recurring.id == recurring1.id

        response.json[1].id == action2.id
        response.json[1].title == action2.title
        response.json[1].amount == action2.amount
        response.json[1].date.dayInMonth == 9
        response.json[1].date.weekInYear == 42
        response.json[1].date.month == 10
        response.json[1].date.year == 2011
        response.json[1].type.id == typeParent2.id
        JSONObject.NULL.equals(response.json[1].recurring)
    }

    void "test create [null]"() {
        when:
        controller.create(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test create [contraints empty]"() {
        when:
        def cmd = new ActionCommand()
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "title"
        JSONObject.NULL.equals(response.json.errors[0]["rejected-value"])

        response.json.errors[1].field == "date"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test create [contraints title blank]"() {
        when:
        def cmd = new ActionCommand(title: '')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "title"
        response.json.errors[0]["rejected-value"] == ""

        response.json.errors[1].field == "date"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test create [contraints date blank]"() {
        when:
        def cmd = new ActionCommand(title: 'Action 4', date: '')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "date"
        response.json.errors[0]["rejected-value"] == ""
    }

    void "test create [contraints date matches]"() {
        when:
        def cmd = new ActionCommand(title: 'Action 4', date: 'wrong date')
        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "date"
        response.json.errors[0]["rejected-value"] == "wrong date"
    }

    void "test create [unknow type 1/2]"() {
        when:
        def cmd = new ActionCommand(title: 'Action 4', date: "10/12/2015")

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test create [unknow type 2/2]"() {
        when:
        def cmd = new ActionCommand(title: 'Action 4', date: "10/12/2015", idType: 42)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test create [forbidden]"() {
        when:
        def cmd = new ActionCommand(title: 'Action 4', date: "10/12/2015", idType: typeParent3.id)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test create [basic]"() {
        when:
        def cmd = new ActionCommand(title: 'Action 4', date: "10/12/2015", idType: typeParent1.id, amount: 42.42)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == 4
        response.json.type.id == cmd.idType
        response.json.title == cmd.title
        response.json.amount == 4242
        response.json.date.dayInMonth == 10
        response.json.date.weekInYear == 50
        response.json.date.month == 12
        response.json.date.year == 2015
        JSONObject.NULL.equals(response.json.recurring)

        def listActions = Action.findAll()

        listActions.size() == 4
        listActions.get(0) == action1
        listActions.get(1) == action2
        listActions.get(2) == action3
        listActions.get(3).id == 4
        listActions.get(3).type.id == cmd.idType
        listActions.get(3).title == cmd.title
        listActions.get(3).amount == 4242
        listActions.get(3).date.dayInMonth == 10
        listActions.get(3).date.weekInYear == 50
        listActions.get(3).date.month == 12
        listActions.get(3).date.year == 2015
        listActions.get(3).recurring == null
    }

    void "test update [null]"() {
        when:
        controller.update(null)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [contraints empty]"() {
        when:
        def cmd = new ActionCommand(id: action1.id)
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "title"
        JSONObject.NULL.equals(response.json.errors[0]["rejected-value"])

        response.json.errors[1].field == "date"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test update [contraints title blank]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: '')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 2

        response.json.errors[0].field == "title"
        response.json.errors[0]["rejected-value"] == ""

        response.json.errors[1].field == "date"
        JSONObject.NULL.equals(response.json.errors[1]["rejected-value"])
    }

    void "test update [contraints date blank]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: 'Action 4', date: '')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "date"
        response.json.errors[0]["rejected-value"] == ""
    }

    void "test update [contraints date matches]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: 'Action 4', date: 'wrong date')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "date"
        response.json.errors[0]["rejected-value"] == "wrong date"
    }

    void "test update [unknow type 1/2]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: 'Action 4', date: "10/12/2015")

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [unknow type 2/2]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: 'Action 4', date: "10/12/2015", idType: 42)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [forbidden 1/2]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: 'Action 4', date: "10/12/2015", idType: typeParent3.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test update [forbidden 2/2]"() {
        when:
        def cmd = new ActionCommand(id: action3.id, title: 'Action 4', date: "10/12/2015", idType: typeParent1.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test update [basic]"() {
        when:
        def cmd = new ActionCommand(id: action1.id, title: 'Action 4', date: "12/12/2015", idType: typeParent2.id, amount: 43.43)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == cmd.id
        response.json.type.id == cmd.idType
        response.json.title == cmd.title
        response.json.amount == 4343
        response.json.date.dayInMonth == 12
        response.json.date.weekInYear == 50
        response.json.date.month == 12
        response.json.date.year == 2015
        response.json.recurring.id == recurring1.id

        def listActions = Action.findAll()

        listActions.size() == 3
        listActions.get(0).type.id == cmd.idType
        listActions.get(0).title == cmd.title
        listActions.get(0).amount == 4343
        listActions.get(0).date.dayInMonth == 12
        listActions.get(0).date.weekInYear == 50
        listActions.get(0).date.month == 12
        listActions.get(0).date.year == 2015
        listActions.get(0).recurring == recurring1
        listActions.get(1) == action2
        listActions.get(2) == action3
    }

    void "test delete [invalid id]"() {
        when:
        controller.delete(42)
        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test delete [with not owner]"() {
        when:
        controller.delete(action3.id)
        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test delete [basic]"() {
        when:
        controller.delete(action2.id)
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
        listActions.size() == 2
        listActions.get(0) == action1
        listActions.get(1) == action3
    }

}
