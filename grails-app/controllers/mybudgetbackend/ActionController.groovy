package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.Validateable
import groovy.transform.ToString
import mybudget.Action
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus

import java.text.SimpleDateFormat

class ActionController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    ActionController() {
        super(Action)
    }

    def index() {
        render Action.findAllByUser(currentUser()) as JSON
    }

    def create(ActionCommand cmd) {
        if (cmd == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (!cmd.validate() || cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else {
            def type = Type.findById(cmd.idType)
            if (type == null) {
                render status: HttpStatus.SC_NOT_FOUND
            } else if (type.user != currentUser()) {
                render status: HttpStatus.SC_FORBIDDEN
            } else {
                def action = new Action(type: type, title: cmd.title, amount: cmd.amount, user: currentUser(), date: new SimpleDateFormat('dd/MM/yyyy').parse(cmd.date))
                action.validate()
                if (action.hasErrors()) {
                    response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                    render action.errors as JSON
                } else {
                    action.save(flush:true)
                    render action as JSON
                }
            }
        }
    }

    def update(ActionCommand cmd) {
        def action
        if (cmd == null || !(action = Action.findById(cmd.id))) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (!cmd.validate() || cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else if (action.user != currentUser()) {
            render status: HttpStatus.SC_FORBIDDEN
        } else {
            def type = Type.findById(cmd.idType)
            if (type == null) {
                render status: HttpStatus.SC_NOT_FOUND
            } else if (type.user != currentUser()) {
                render status: HttpStatus.SC_FORBIDDEN
            } else {
                action.type = type
                action.title = cmd.title
                action.amount = cmd.amount
                action.date = new SimpleDateFormat('dd/MM/yyyy').parse(cmd.date)
                action.validate()
                if (action.hasErrors()) {
                    response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                    render action.errors as JSON
                } else {
                    action.save(flush:true)
                    render action as JSON
                }
            }
        }
    }

    def delete(long id) {
        def action
        if (!(action = Action.findById(id))) {
            render status: HttpStatus.SC_NOT_FOUND
        }
        else if (action.user != currentUser()) {
            render status: HttpStatus.SC_FORBIDDEN
        } else {
            action.delete(flush: true)
            render status: HttpStatus.SC_OK
        }
    }

    protected User currentUser() {
        request.user
    }
}

@Validateable
@ToString
public class ActionCommand {
    long id
    long idType
    String title;
    double amount;
    String date;
    long idRecurring;

    static constraints = {
        id(nullable: true)
        idType(blank: false)
        title(blank: false)
        amount(blank: false)
        date(blank: false, matches: '^\\d{2}/\\d{2}/\\d{4}$')
        idRecurring(nullable: true)
    }
}
