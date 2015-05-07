package mybudget

class Action {

    String title;
    double amount;

    Type type
    User user
    static belongsTo = [Type, User]

    static hasOne = [recurring:Recurring, date:Date]

    static constraints = {
        title blank: false
        amount blank: false
        date blank: false
        recurring nullable: true
    }
}
