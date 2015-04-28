package mybudget

class Type {

    String title;
    boolean isIncoming;
    int rgb;

    Type parent;
    static hasOne = [Type]

    static hasMany = [actions: Action]

    User user
    static belongsTo = [User]

    static constraints = {
        parent nullable: true
        title blank: false, unique:true
    }
}
