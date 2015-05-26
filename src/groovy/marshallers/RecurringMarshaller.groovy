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
                    createDate: recurring.createDate.format("yyyy/MM/dd"),
                    nextDate: recurring.nextDate.format("yyyy/MM/dd"),
                    endDate: (recurring.endDate != null) ? recurring.endDate.format("yyyy/MM/dd") : null,
                    idAction: recurring.action.id
            ]
        }
    }
}
