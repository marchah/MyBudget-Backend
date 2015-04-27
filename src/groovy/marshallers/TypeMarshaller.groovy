package marshallers

import grails.converters.JSON
import mybudget.Type

import javax.annotation.PostConstruct

/**
 * Created by marcha on 4/24/15.
 */
class TypeMarshaller {

    @PostConstruct
    void register() {

        JSON.registerObjectMarshaller(Type) { Type type ->
            [
                    id: type.id,
                    idParent: (type.parent != null) ? type.parent.id : 0,
                    title: type.title,
                    isIncoming: type.isIncoming,
                    rgb: type.rgb,
                    idUser: type.user.id
            ]
        }
    }
}
