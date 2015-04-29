package marshallers

import grails.converters.JSON
import mybudget.Recurring

import javax.annotation.PostConstruct

/**
 * Created by marcha on 4/29/15.
 */
class RecurringMarshaller {
    @PostConstruct
    void register() {
        JSON.registerObjectMarshaller(Recurring) { Recurring recurring ->
            [
                    id: recurring.id,
                    createDate: recurring.createDate.format("dd/MM/yyyy"),
                    nextDate: recurring.nextDate.format("dd/MM/yyyy"),
                    endDate: (recurring.endDate != null) ? recurring.endDate.format("dd/MM/yyyy") : null,
                    idAction: recurring.action.id
            ]
        }
    }
}
