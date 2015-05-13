package mybudgetbackend

import Utils.ParamConverter
import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.Validateable
import groovy.transform.ToString
import mybudget.Action
import mybudget.Date
import mybudget.Type
import mybudget.User
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpStatus
import org.hibernate.Criteria
import org.hibernate.criterion.CriteriaSpecification

class ActionController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    ActionController() {
        super(Action)
    }

    def index() {
        int nbActionPerCall = 20;
        int position = 0;

        if (StringUtils.isNumeric(params.nbActionPerCall) && Integer.parseInt(params.nbActionPerCall) > 0 && Integer.parseInt(params.nbActionPerCall) < 50) {
            nbActionPerCall = Integer.parseInt(params.nbActionPerCall)
        }

        if (StringUtils.isNumeric(params.nbActionPerCall) &&  Integer.parseInt(params.offset) > 0) {
            position = Integer.parseInt(params.offset)
        }

        def result = Action.createCriteria().list(max: nbActionPerCall, offset: position) {
            createAlias('type', 'type', Criteria.LEFT_JOIN)
            projections {
                eq('user', currentUser())
                date {
                    order('date', 'desc')
                }
                if (params.search != null) {
                    or {
                        ilike("title", "%" + params.search + "%")
                        ilike("type.title", "%" + params.search + "%")
                    }
                }
                if (params.min != null) {
                    ge("amount", Integer.parseInt(params.min)*100)
                }
                if (params.max != null) {
                    le("amount", Integer.parseInt(params.max)*100)
                }
            }
        }
        render result as JSON
    }

    def sum() {
        def result = Action.createCriteria().list() {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            projections {
                sum('amount', 'total')
                eq('user', currentUser())
                date {
                    if (params.date == 'month') {
                        groupProperty('month', 'month')
                    } else if (params.date == 'day') {
                        groupProperty('dayInMonth', 'day')
                        groupProperty('month', 'month')
                    } else if (params.date == 'week') {
                        groupProperty('weekInYear', 'week')
                    }
                    groupProperty('year', 'year')
                }
                type {
                    groupProperty('isIncoming', 'isIncoming')
                }
            }
        }
        for (def type : result) {
            type.total /= 100
        }
        render result as JSON
    }

    def getDatesActionAvailable() {
        def result = Date.createCriteria().list() {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            projections {
                count('id', 'count')
                action {
                    eq('user', currentUser())
                }
                if (params.date == 'month') {
                    groupProperty('month', 'month')
                } else if (params.date == 'day') {
                    groupProperty('dayInMonth', 'day')
                    groupProperty('month', 'month')
                } else if (params.date == 'week') {
                    groupProperty('weekInYear', 'week')
                }
                groupProperty('year', 'year')
            }
        }
        return ParamConverter.ConvertDateIdFromDateFormat(result, params.date)
    }

    def groupByType() {

        def datesActionAvailable = getDatesActionAvailable()

        def types = []

        if (datesActionAvailable.size() > 0) {
            def dateActionAvailable = ParamConverter.ConvertStringDateToDateTab(params.id, params.date, datesActionAvailable)
            types = Action.createCriteria().list() {
                resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
                projections {
                    sum('amount', 'total')
                    eq('user', currentUser())
                    date {
                        if (params.date == 'month') {

                            eq('month', dateActionAvailable.month)
                        } else if (params.date == 'day') {
                            eq('dayInMonth', dateActionAvailable.day)
                            eq('month', dateActionAvailable.month)
                        } else if (params.date == 'week') {
                            eq('weekInYear', dateActionAvailable.week)
                        }
                        eq('year', dateActionAvailable.year)
                    }
                    type {
                        if (params.excludeTypes != null) {
                            not {'in'("id", ParamConverter.ConvertListStringToListLong(params.excludeTypes))}
                        }
                        groupProperty('id', 'idType')
                        property('title', 'title')
                        eq('isIncoming', Boolean.valueOf(params.isIncoming))
                    }
                }
            }
        }
        for (def type : types) {
            type.total /= 100
        }
        def ret = [dates: datesActionAvailable, types: types]
        render ret as JSON
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
                def action = new Action(type: type, title: cmd.title, amount: cmd.amount*100, user: currentUser(), date: new Date(cmd.date))
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
                action.amount = cmd.amount*100
                action.date.setDate(cmd.date)
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
    String title
    double amount
    String date
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
