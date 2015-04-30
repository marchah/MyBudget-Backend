package mybudget

import org.apache.commons.lang.RandomStringUtils

class Token {

    Date dateCreated
    Date lastUpdated

    String token

    static belongsTo = [user:User]

    static constraints = {
        token blank: false
    }

    def updateToken() {
        if (!token) {
            token = RandomStringUtils.random(30, true, true)
        }
    }
}
