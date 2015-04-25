package mybudgetbackend.transfert

import grails.validation.Validateable

@Validateable(nullable = true)
class AuthenticationCredentials {
    String email
    String password
    String login
}
