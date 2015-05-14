package marshallers

import grails.converters.JSON
import mybudget.Action

import javax.annotation.PostConstruct

/**
 * Created by marcha on 4/28/15.
 */
class ActionMarshaller {
    @PostConstruct
    void register() {
        JSON.registerObjectMarshaller(Action) { Action action ->
            [
                    id: action.id,
                    idType: action.type.id,
                    title: action.title,
                    amount: action.amount/100,
                    rgb: action.type.rgb,
                    date: action.date.dayInMonth + "/" + action.date.month + "/" + action.date.year,
                    idRecurring: (action.recurring != null) ? action.recurring.id : 0
            ]
        }
    }
}
