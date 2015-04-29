package mybudget

class Action {

    String title;
    double amount;
    Date date;

    Type type
    User user
    static belongsTo = [Type, User]

    Recurring recurring;
    static hasOne = [Recurring]

    static constraints = {
        title blank: false
        amount blank: false
        date blank: false
        recurring nullable: true
    }
}
