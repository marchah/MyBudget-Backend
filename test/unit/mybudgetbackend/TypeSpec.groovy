package mybudgetbackend

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import mybudget.Type
import mybudget.User
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Type)
class TypeSpec extends ADomainSpec {

    void 'test constraint [title]'() {
        given:
        mockForConstraintsTests Type

        when: 'the type title is null'
        def type = new Type(title: null)

        then: 'validation should fail'
        !type.validate()
        type.hasErrors()
        type.errors['title'] == 'nullable'

        when: 'the type title is blank'
        type.title = ''

        then: 'validation should fail'
        !type.validate()
        type.hasErrors()
        type.errors['title'] == 'blank'


        when: 'the type title is not unique'
        type.title = typeParent1.title

        then: 'validation should fail'
        !type.validate()
        type.hasErrors()
        type.errors['title'] == 'unique'
    }

    void 'test constraint [User]'() {
        given:
        mockForConstraintsTests Type

        when: 'the type user is null'
        def type = new Type(title: 'Type 1', user: null)

        then: 'validation should fail'
        !type.validate()
        type.hasErrors()

        when: 'the type user is set'
        type.user = user1

        then: 'validation success'
        type.validate()
        !type.hasErrors()
    }

    void 'test save'(){
        when:
        def type = new Type(title: 'Type 1', user: user1)
        type.save(flush: true)

        then:
        Type.findById(type.id) == type
    }

    void 'test update'(){
        when:
        def type = new Type(title: 'Type 1', user: user1)
        type.save(flush: true)

        type.parent = typeParent1
        type.title = 'Type 2'
        type.isIncoming = true
        type.rgb = 555
        type.user = user2
        type.save(flush: true)

        then:
        Type.findById(type.id) == type
    }

    void 'test select'(){
        when:
        def type1 = new Type(title: 'Type 1', isIncoming: true, rgb: 111, parent: typeParent1, user: user1)
        type1.save(flush: true)

        def type2 = new Type(title: 'Type 2', isIncoming: false, rgb: 222, parent: typeParent2, user: user2)
        type2.save(flush: true)

        then:
        def listTypes = Type.findAll()
        listTypes.size() == 5


        listTypes.get(0) == typeParent1
        listTypes.get(1) == typeParent2
        listTypes.get(2) == typeParent3
        listTypes.get(3) == type1
        listTypes.get(4) == type2
    }

    void 'test delete'(){
        when:
        def type = new Type(title: 'Type 1', user: user1)
        type.save(flush: true)

        then:
        type.delete(flush: true)
        Type.findById(type.id) == null
    }
}
