package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus

class TypeController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    TypeController() {
        super(Type)
    }

    def index() {
        render Type.listOrderByUser(user:currentUser()) as JSON
    }

    def create(Type type) {
        if (type == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else {
            type.user = currentUser()
            println type.user
            if (type.hasErrors()) {
                response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                render type.errors as JSON
            } else {
                type.save(flush:true)
                render type as JSON
            }
        }
    }

    protected User currentUser() {
        request.user
    }
}
