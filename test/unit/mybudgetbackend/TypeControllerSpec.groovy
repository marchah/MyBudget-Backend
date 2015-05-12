package mybudgetbackend

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TypeController)
class TypeControllerSpec extends AControllerSpec {

    void "test index [basic]"() {
        when:
        controller.index()

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 2

        response.json[0].id == typeParent1.id
        JSONObject.NULL.equals(response.json[0].parent)
        response.json[0].title == typeParent1.title
        response.json[0].isIncoming == typeParent1.isIncoming
        response.json[0].rgb == typeParent1.rgb
        response.json[0].user.id == typeParent1.user.id

        response.json[1].id == typeParent2.id
        JSONObject.NULL.equals(response.json[1].parent)
        response.json[1].title == typeParent2.title
        response.json[1].isIncoming == typeParent2.isIncoming
        response.json[1].rgb == typeParent2.rgb
        response.json[1].user.id == typeParent2.user.id
    }

    void "test index [with parent]"() {
        when:
        def type1 = new Type(title: 'Title 1', parent: typeParent1, user: user1)
        type1.save(flush: true)

        def type2 = new Type(title: 'Title 2', parent: typeParent2, user: user1)
        type2.save(flush: true)

        controller.index()

        then:
        response.status == HttpStatus.SC_OK
        response.getJson().findAll().size() == 4

        response.json[0].id == typeParent1.id
        JSONObject.NULL.equals(response.json[0].parent)
        response.json[0].title == typeParent1.title
        response.json[0].isIncoming == typeParent1.isIncoming
        response.json[0].rgb == typeParent1.rgb
        response.json[0].user.id == typeParent1.user.id

        response.json[1].id == typeParent2.id
        JSONObject.NULL.equals(response.json[1].parent)
        response.json[1].title == typeParent2.title
        response.json[1].isIncoming == typeParent2.isIncoming
        response.json[1].rgb == typeParent2.rgb
        response.json[1].user.id == typeParent2.user.id

        response.json[2].id == type1.id
        response.json[2].parent.id == type1.parent.id
        response.json[2].title == type1.title
        response.json[2].isIncoming == type1.isIncoming
        response.json[2].rgb == type1.rgb
        response.json[2].user.id == type1.user.id

        response.json[3].id == type2.id
        response.json[3].parent.id == type2.parent.id
        response.json[3].title == type2.title
        response.json[3].isIncoming == type2.isIncoming
        response.json[3].rgb == type2.rgb
        response.json[3].user.id == type2.user.id
    }

    void "test create [Title Not Unique]"() {
        when:
        def cmd = new TypeCommand(title: 'Parent 1')

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY
        response.json.args == ['title']
        response.json.message == "Type title isn't unique"
    }

    void "test create [basic]"() {
        when:
        def cmd = new TypeCommand(title: 'Title 1')

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == 4
        JSONObject.NULL.equals(response.json.parent)
        response.json.title == cmd.title
        response.json.isIncoming == cmd.isIncoming
        response.json.rgb == cmd.rgb
        response.json.user.id == user1.id

        def listTypes = Type.findAllByUser(user1)

        listTypes.size() == 3
        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
        listTypes.get(2).id == 4
        listTypes.get(2).parent == null
        listTypes.get(2).title == cmd.title
        listTypes.get(2).isIncoming == cmd.isIncoming
        listTypes.get(2).rgb == cmd.rgb
        listTypes.get(2).user.id == user1.id
    }

    void "test create [with parent]"() {
        when:
        def cmd = new TypeCommand(title: 'Title 1', idParent: typeParent1.id)

        controller.create(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == 4
        response.json.parent.id == typeParent1.id
        response.json.title == cmd.title
        response.json.isIncoming == cmd.isIncoming
        response.json.rgb == cmd.rgb
        response.json.user.id == user1.id

        def listTypes = Type.findAllByUser(user1)

        listTypes.size() == 3
        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
        listTypes.get(2).id == 4
        listTypes.get(2).parent == typeParent1
        listTypes.get(2).title == cmd.title
        listTypes.get(2).isIncoming == cmd.isIncoming
        listTypes.get(2).rgb == cmd.rgb
        listTypes.get(2).user.id == user1.id
    }

    void "test update [Title Not Unique]"() {
        when:
        typeParent2.rgb = 123
        def cmd = new TypeCommand(id: typeParent2.id, title: typeParent1.title, rgb: typeParent2.rgb)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_UNPROCESSABLE_ENTITY
        response.json.args == ['title']
        response.json.message == "Type title isn't unique"
    }

    void "test update [basic]"() {
        when:
        typeParent2.rgb = 123
        def cmd = new TypeCommand(id: typeParent2.id, title: typeParent2.title, rgb: typeParent2.rgb)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == typeParent2.id
        JSONObject.NULL.equals(response.json.parent)
        response.json.title == typeParent2.title
        response.json.isIncoming == typeParent2.isIncoming
        response.json.rgb == typeParent2.rgb
        response.json.user.id == user1.id

        def listTypes = Type.findAllByUser(user1)

        listTypes.size() == 2
        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
    }

    void "test update [with parent]"() {
        when:
        typeParent2.parent = typeParent1
        typeParent2.rgb = 123
        def cmd = new TypeCommand(id: typeParent2.id, idParent: typeParent2.parent.id, title: typeParent2.title, rgb: typeParent2.rgb)

        controller.update(cmd)

        then:
        response.status == HttpStatus.SC_OK
        response.json.id == typeParent2.id
        response.json.parent.id == typeParent2.parent.id
        response.json.title == typeParent2.title
        response.json.isIncoming == typeParent2.isIncoming
        response.json.rgb == typeParent2.rgb
        response.json.user.id == user1.id

        def listTypes = Type.findAllByUser(user1)

        listTypes.size() == 2
        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
    }

    void "test delete [basic]"() {
        when:
        def cmd = new TypeCommand(title: 'Title 1')

        controller.create(cmd)
        controller.delete(4)
        then:
        response.status == HttpStatus.SC_OK

        def listTypes = Type.findAllByUser(user1)

        listTypes.size() == 2
        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
    }

    void "test delete [invalid id]"() {
        when:
        controller.delete(42)
        then:
        response.status == HttpStatus.SC_NOT_FOUND
    }

    void "test delete [with not owner]"() {
        when:
        controller.delete(typeParent3.id)
        then:
        response.status == HttpStatus.SC_FORBIDDEN
    }
}
