package mybudget

class Type {

    String title;
    boolean isIncoming;
    int rgb;

    Type parent;
    static hasOne = [Type]

    static hasMany = [actions: Action]

    //int idUser
    User user
    static belongsTo = [User]
    //static belongsTo = [user:User]
    //

    static constraints = {
        parent nullable: true
        title blank: false, unique:true
    //    idUser blank: false
    }
}
