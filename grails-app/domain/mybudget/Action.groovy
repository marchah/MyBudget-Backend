package mybudget

class Action {

    String title;
    double amount;
    Date date;
    long idRecurring;

    Type type
    static belongsTo = [Type]

    static constraints = {
        title blank: false
        amount blank: false
        date blank: false
        idRecurring nullable: true
    }
}
