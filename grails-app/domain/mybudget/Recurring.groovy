package mybudget

class Recurring {

    java.util.Date createDate;
    java.util.Date endDate;
    java.util.Date nextDate;

    static belongsTo = [action:Action]

    static constraints = {
        createDate blank: false
        nextDate blank: false
        endDate nullable: true
    }
}
