package mybudgetbackend


import grails.test.mixin.TestFor
import mybudget.Action
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus
import org.codehaus.groovy.grails.web.json.JSONObject

import javax.xml.bind.DatatypeConverter
import java.text.SimpleDateFormat


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ActionController)
class ActionControllerSpec extends ASpec {

    def dateFormater = new SimpleDateFormat("dd/MM/yyyy")

    void "test index [basic]"() {
        when:
        controller.index()

        then:
        response.status == HttpStatus.SC_OK

        response.getJson().findAll().size() == 2

        response.json[0].id == action1.id
        response.json[0].title == action1.title
        response.json[0].amount == action1.amount
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json[0].date).getTime()) == dateFormater.format(action1.date)
        response.json[0].type.id == typeParent1.id
        JSONObject.NULL.equals(response.json[0].recurring)

        response.json[1].id == action2.id
        response.json[1].title == action2.title
        response.json[1].amount == action2.amount
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json[1].date).getTime()) == dateFormater.format(action2.date)
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
        def cmd = new ActionCommand(title: 'Action 4', date: "10/12/2015", idType: typeParent1.id)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == 4
        response.json.type.id == cmd.idType
        response.json.title == cmd.title
        response.json.amount == cmd.amount
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json.date).getTime()) == cmd.date
        JSONObject.NULL.equals(response.json.recurring)

        def listActions = Action.findAll()

        listActions.size() == 4
        listActions.get(0) == action1
        listActions.get(1) == action2
        listActions.get(2) == action3
        listActions.get(3).id == 4
        listActions.get(3).type.id == cmd.idType
        listActions.get(3).title == cmd.title
        listActions.get(3).amount == cmd.amount
        dateFormater.format(listActions.get(3).date) == cmd.date
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
        def cmd = new ActionCommand(id: 1)
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
        def cmd = new ActionCommand(id: 1, title: '')
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
        def cmd = new ActionCommand(id: 1, title: 'Action 4', date: '')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "date"
        response.json.errors[0]["rejected-value"] == ""
    }

    void "test update [contraints date matches]"() {
        when:
        def cmd = new ActionCommand(id: 1, title: 'Action 4', date: 'wrong date')
        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY

        response.json.errors.length() == 1

        response.json.errors[0].field == "date"
        response.json.errors[0]["rejected-value"] == "wrong date"
    }

    void "test update [unknow type 1/2]"() {
        when:
        def cmd = new ActionCommand(id: 1, title: 'Action 4', date: "10/12/2015")

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [unknow type 2/2]"() {
        when:
        def cmd = new ActionCommand(id: 1, title: 'Action 4', date: "10/12/2015", idType: 42)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test update [forbidden]"() {
        when:
        def cmd = new ActionCommand(id: 1, title: 'Action 4', date: "10/12/2015", idType: typeParent3.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }

    void "test update [basic]"() {
        when:
        def cmd = new ActionCommand(id: 1, title: 'Action 4', date: "12/12/2015", idType: typeParent2.id)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == cmd.id
        response.json.type.id == cmd.idType
        response.json.title == cmd.title
        response.json.amount == cmd.amount
        dateFormater.format(DatatypeConverter.parseDateTime((String)response.json.date).getTime()) == cmd.date
        JSONObject.NULL.equals(response.json.recurring)

        def listActions = Action.findAll()

        listActions.size() == 3
        listActions.get(0).type.id == cmd.idType
        listActions.get(0).title == cmd.title
        listActions.get(0).amount == cmd.amount
        dateFormater.format(listActions.get(0).date) == cmd.date
        listActions.get(0).recurring == null
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
