package mybudget

class Action {

    String title;
    double amount;
    Date date;
    long idRecurring;

    static belongsTo = [type:Type]

    static constraints = {
        title blank: false
        amount blank: false
        date blank: false
    }
}
