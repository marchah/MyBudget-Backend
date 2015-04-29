package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.Validateable
import groovy.transform.ToString
import mybudget.Action
import mybudget.Recurring
import mybudget.User
import org.apache.http.HttpStatus

import java.text.SimpleDateFormat

class RecurringController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    RecurringController() {
        super(Recurring)
    }

    def index() {
        def listActions = Action.findAllByUser(currentUser())
        List<Recurring> listRecurring = []
        for (Action action : listActions) {
            listRecurring.addAll(Recurring.findAllByAction(action))
        }
        render listRecurring as JSON
    }

    def create(RecurringCommand cmd) {
        if (cmd == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (!cmd.validate() || cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else {
            def action = Action.findById(cmd.idAction)
            if (action == null) {
                render status: HttpStatus.SC_NOT_FOUND
            } else if (action.user != currentUser()) {
                render status: HttpStatus.SC_FORBIDDEN
            } else {
                def recurring = new Recurring(action: action,
                                                createDate: new SimpleDateFormat('dd/MM/yyyy').parse(cmd.createDate),
                                                nextDate: new SimpleDateFormat('dd/MM/yyyy').parse(cmd.nextDate))
                recurring.validate()
                if (recurring.hasErrors()) {
                    response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                    render recurring.errors as JSON
                } else {
                    recurring.save(flush:true)
                    render recurring as JSON
                }
            }
        }
    }

    def update(RecurringCommand cmd) {
        def recurring
        if (cmd == null || !(recurring = Recurring.findById(cmd.id))) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (!cmd.validate() || cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else if (recurring.action.user != currentUser()) {
            render status: HttpStatus.SC_FORBIDDEN
        } else {
            def action = Action.findById(cmd.idAction)
            if (action == null) {
                render status: HttpStatus.SC_NOT_FOUND
            } else if (action.user != currentUser()) {
                render status: HttpStatus.SC_FORBIDDEN
            } else {
                recurring.action = action
                recurring.createDate = new SimpleDateFormat('dd/MM/yyyy').parse(cmd.createDate)
                recurring.nextDate = new SimpleDateFormat('dd/MM/yyyy').parse(cmd.nextDate)
                recurring.validate()
                if (recurring.hasErrors()) {
                    response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                    render recurring.errors as JSON
                } else {
                    recurring.save(flush:true)
                    render recurring as JSON
                }
            }
        }
    }

    def delete(long id) {
        def recurring
        if (!(recurring = Recurring.findById(id))) {
            render status: HttpStatus.SC_NOT_FOUND
        }
        else if (recurring.action.user != currentUser()) {
            render status: HttpStatus.SC_FORBIDDEN
        } else {
            recurring.delete(flush: true)
            render status: HttpStatus.SC_OK
        }
    }

    protected User currentUser() {
        request.user
    }
}

@Validateable
@ToString
public class RecurringCommand {
    long id
    long idAction
    String createDate
    String nextDate
    String endDate

    static constraints = {
        id(nullable: true)
        idAction(blank: false)
        createDate(blank: false, matches: '^\\d{2}/\\d{2}/\\d{4}$')
        nextDate(blank: false, matches: '^\\d{2}/\\d{2}/\\d{4}$')
        endDate(nullable: true, matches: '^\\d{2}/\\d{2}/\\d{4}$')
    }
}
