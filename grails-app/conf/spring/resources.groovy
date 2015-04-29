import marshallers.ActionMarshaller
import marshallers.RecurringMarshaller
import marshallers.TypeMarshaller
import marshallers.UserMarshaller

// Place your Spring DSL code here
beans = {
    userMarshaller(UserMarshaller) { bean ->
        bean.autowire = 'byName'
    }

    typeMarshaller(TypeMarshaller) { bean ->
        bean.autowire = 'byName'
    }

    actionMarshaller(ActionMarshaller) { bean ->
        bean.autowire = 'byName'
    }

    recurringMarshaller(RecurringMarshaller) { bean ->
        bean.autowire = 'byName'
    }
}
