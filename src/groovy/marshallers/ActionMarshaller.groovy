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
                    amount: action.amount,
                    date: action.date.format("dd/MM/yyyy"),
                    idRecurring: (action.recurring != null) ? action.recurring.id : 0
            ]
        }
    }
}
