import marshallers.ActionMarshaller
import marshallers.RecurringMarshaller
import marshallers.TypeMarshaller

// Place your Spring DSL code here
beans = {
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
