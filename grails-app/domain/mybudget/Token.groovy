package mybudget

import org.apache.commons.lang.RandomStringUtils

class Token {

    java.util.Date dateCreated
    java.util.Date lastUpdated

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
