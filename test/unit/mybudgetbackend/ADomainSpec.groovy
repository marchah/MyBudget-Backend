package mybudgetbackend

import grails.test.mixin.Mock
import mybudget.Action
import mybudget.Recurring
import mybudget.Type
import mybudget.User
import spock.lang.Specification

/**
 * Created by marcha on 4/29/15.
 */
@Mock([User, Type, Action, Recurring])
abstract class ADomainSpec extends Specification {

    def user1
    def user2
    def typeParent1
    def typeParent2
    def typeParent3
    def action1
    def action2
    def action3

    def setup() {
        user1 = new User(displayName: 'User1', login: 'user1', password: 'totoauzoo', email: 'user1@email.com')
        user1.save(flush: true)

        user2 = new User(displayName: 'User2', login: 'user2', password: 'totoauzoo', email: 'user2@email.com')
        user2.save(flush: true)

        typeParent1 = new Type(title: 'Parent 1', user: user1)
        typeParent1.save(flush: true)

        typeParent2 = new Type(title: 'Parent 2', user: user1)
        typeParent2.save(flush: true)

        typeParent3 = new Type(title: 'Parent 3', user: user2)
        typeParent3.save(flush: true)

        action1 = new Action(title: 'Action 1', amount: 42.42, date: new Date(), type: typeParent1, user: user1)
        action1.save(flush: true)

        action2 = new Action(title: 'Action 2', amount: 21.21, date: new Date(), type: typeParent2, user: user1)
        action2.save(flush: true)

        action3 = new Action(title: 'Action 3', amount: 99.99, date: new Date(), type: typeParent3, user: user2)
        action3.save(flush: true)
    }

    def cleanup() {
        user1.delete(flush: true)
        user2.delete(flush: true)

        typeParent1.delete(flush: true)
        typeParent2.delete(flush: true)
        typeParent3.delete(flush: true)

        action1.delete(flush: true)
        action2.delete(flush: true)
        action3.delete(flush: true)
    }
}
