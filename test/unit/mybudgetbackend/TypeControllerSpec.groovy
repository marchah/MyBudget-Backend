package mybudgetbackend

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.Type
import mybudget.User
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TypeController)
@Mock([User, Type])
class TypeControllerSpec extends Specification {

    def user1
    def typeParent1
    def typeParent2

    def setup() {
        user1 = new User(displayName: 'User1', login: 'user1', password: 'totoauzoo', email: 'user1@email.com')
        user1.save(flush: true)

        typeParent1 = new Type(title: 'Parent 1', user: user1)
        typeParent1.save(flush: true)

        typeParent2 = new Type(title: 'Parent 2', user: user1)
        typeParent2.save(flush: true)

        request.user = user1
    }

    def cleanup() {
        user1.delete(flush: true)

        typeParent1.delete(flush: true)
        typeParent2.delete(flush: true)
    }

    void "test index [basic]"() {
        when:
        controller.index()

        then:
        response.status == 200
        response.getJson().findAll().size() == 2

        response.json[0].id == typeParent1.id
        JSONObject.NULL.equals(response.json[0].parent)// == typeParent1.parent
        response.json[0].title == typeParent1.title
        response.json[0].isIncoming == typeParent1.isIncoming
        response.json[0].rgb == typeParent1.rgb
        response.json[0].user.id == typeParent1.user.id

        response.json[1].id == typeParent2.id
        JSONObject.NULL.equals(response.json[1].parent)// == typeParent2.parent
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
        response.status == 200
        response.getJson().findAll().size() == 4

        response.json[0].id == typeParent1.id
        JSONObject.NULL.equals(response.json[0].parent)// == typeParent1.parent
        response.json[0].title == typeParent1.title
        response.json[0].isIncoming == typeParent1.isIncoming
        response.json[0].rgb == typeParent1.rgb
        response.json[0].user.id == typeParent1.user.id

        response.json[1].id == typeParent2.id
        JSONObject.NULL.equals(response.json[1].parent)// == typeParent2.parent
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
}
