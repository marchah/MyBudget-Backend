package mybudgetbackend

import bcrypt.BcryptCodec
import grails.test.mixin.Mock
import mybudget.Action
import mybudget.Date
import mybudget.Recurring
import mybudget.Token
import mybudget.Type
import mybudget.User
import spock.lang.Specification

/**
 * Created by marcha on 4/29/15.
 */
@Mock([User, Type, Action, Recurring, Token])
abstract class ADomainSpec extends Specification {

    def user1
    def user2
    def typeParent1
    def typeParent2
    def typeParent3
    def action1
    def action2
    def action3

    def password = 'totoauzoo'

    def setup() {
        mockCodec(BcryptCodec)

        user1 = new User(displayName: 'User1', login: 'user1', email: 'user1@email.com', token: new Token())
        user1.token.updateToken()
        user1.passwordHash = password.encodeAsBcrypt()
        user1.save(flush: true)

        user2 = new User(displayName: 'User2', login: 'user2', email: 'user2@email.com', token: new Token())
        user2.token.updateToken()
        user2.passwordHash = password.encodeAsBcrypt()
        user2.save(flush: true)

        typeParent1 = new Type(title: 'Parent 1', user: user1)
        typeParent1.save(flush: true)

        typeParent2 = new Type(title: 'Parent 2', user: user1)
        typeParent2.save(flush: true)

        typeParent3 = new Type(title: 'Parent 3', user: user2)
        typeParent3.save(flush: true)

        action1 = new Action(title: 'Action 1', amount: 4242, date: new Date('10/11/2012'), type: typeParent1, user: user1)
        action1.save(flush: true)

        action2 = new Action(title: 'Action 2', amount: 2121, date: new Date('09/10/2011'), type: typeParent2, user: user1)
        action2.save(flush: true)

        action3 = new Action(title: 'Action 3', amount: 9999, date: new Date('08/09/2010'), type: typeParent3, user: user2)
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
