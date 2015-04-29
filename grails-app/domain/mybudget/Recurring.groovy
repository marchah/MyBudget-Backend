package mybudget

class Recurring {

    Date createDate;
    Date endDate;
    Date nextDate;

    static belongsTo = [action:Action]

    static constraints = {
        createDate blank: false
        nextDate blank: false
        endDate nullable: true
    }
}
