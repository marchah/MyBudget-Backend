package mybudget

class User {

    java.util.Date dateCreated
    java.util.Date lastUpdated

    String displayName
    String login
    String passwordHash
    String email

    static hasOne = [token:Token]

    static constraints = {
        displayName blank: false, size: 5..30
        login blank: false, size: 5..15, unique: true, matches: '^[a-zA-Z0-9_]+$'
        email blank: false, email: true
    }

}
