package mybudgetbackend.transfert

import grails.validation.Validateable

@Validateable(nullable = false)
class AuthenticationCredentials {
    String username
    String password

    static constraints = {
        username blank: false
        password blank: false
    }
}
