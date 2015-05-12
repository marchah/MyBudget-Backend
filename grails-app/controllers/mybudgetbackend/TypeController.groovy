package mybudgetbackend

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.Validateable
import groovy.transform.ToString
import mybudget.Type
import mybudget.User
import org.apache.http.HttpStatus

class TypeController extends RestfulController {

    static namespace = 'v1'
    static responseFormats = ['json']

    TypeController() {
        super(Type)
    }

    def index() {
        render Type.findAllByUser(currentUser()) as JSON
    }

    def create(TypeCommand cmd) {
        if (cmd == null) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else if (Type.findAllByUserAndTitle(currentUser(), cmd.title).size() > 0) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            flash.args = ['title']
            flash.message = "Type title isn't unique"
            render flash as JSON
        } else {
            def type = new Type(title:cmd.title, isIncoming:cmd.isIncoming, rgb:cmd.rgb, user:currentUser())
            type.parent = (cmd.idParent && cmd.idParent > 0) ? Type.findByIdAndUser(cmd.idParent, currentUser()) : null
            type.validate()
            if (type.hasErrors()) {
                response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                render type.errors as JSON
            } else {
                type.save(flush:true)
                render type as JSON
            }
        }
    }

    def update(TypeCommand cmd) {
        def type
        if (cmd == null || !(type = Type.findByIdAndUser(cmd.id, currentUser()))) {
            render status: HttpStatus.SC_NOT_FOUND
        } else if (cmd.hasErrors()) {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render cmd.errors as JSON
        } else {
            def listTypes = Type.findAllByUserAndTitle(currentUser(), cmd.title);
            if (listTypes.size() > 1) {
                response.status = HttpStatus.SC_INTERNAL_SERVER_ERROR
                flash.args = ['title']
                flash.message = "More than one title with the same value found"
                render flash as JSON
            } else if (listTypes.size() > 0 && listTypes.get(0).id != cmd.id) {
                response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                flash.args = ['title']
                flash.message = "Type title isn't unique"
                render flash as JSON
            } else {
                type.title = cmd.title
                type.isIncoming = cmd.isIncoming
                type.rgb = cmd.rgb
                type.parent = (cmd.idParent && cmd.idParent > 0) ? Type.findByIdAndUser(cmd.idParent, currentUser()) : null
                type.validate()
                if (type.hasErrors()) {
                    response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
                    render type.errors as JSON
                } else {
                    type.save(flush: true)
                    render type as JSON
                }
            }
        }
    }

    def delete(long id) {
        def type
        if (!(type = Type.findById(id))) {
            render status: HttpStatus.SC_NOT_FOUND
        }
        else if (type.user != currentUser()) {
            render status: HttpStatus.SC_FORBIDDEN
        } else {
            Type.executeUpdate("UPDATE Type t SET t.parent.id=NULL WHERE t.parent.id=?", [id])
            type.delete(flush: true) // TODO: enable to delete a type which has children so removed the parent ref
            render status: HttpStatus.SC_OK
        }
    }

    protected User currentUser() {
        request.user
    }
}

@Validateable
@ToString
public class TypeCommand {
    long id
    long idParent
    String title;
    boolean isIncoming;
    int rgb;

    static constraints = {
        id(nullable: true)
        idParent(nullable: true)
        title(blank: false)
        isIncoming(nullable: true)
        rgb(nullable: true)
    }
}
