package mybudget

import org.apache.commons.lang.RandomStringUtils

class User {

    Date dateCreated
    Date lastUpdated

    String displayName
    String login
    String password
    String passwordHash
    String email

    String token

    static transients = ['password']

    static constraints = {
        displayName blank: false
        login size: 5..15, blank: false, unique: true, matches: '^[a-zA-Z0-9_]+$'
        password size: 5..15, blank: false, bindable: true
        email email: true
        passwordHash nullable: true
        token nullable: true
    }

    def updateToken() {
        if (!token) {
            token = RandomStringUtils.random(30, true, true)
        }
    }
}
