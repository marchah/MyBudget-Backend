package mybudget

import java.text.SimpleDateFormat

class Date {


    java.util.Date date
    int dayInWeek
    int dayInMonth
    int dayInYear
    int weekInMonth
    int weekInYear
    int month
    int year

    Date() {

    }

    Date (java.util.Date date) {
        setDate(date)
    }

    Date (String date) {
        setDate(date)
    }

    def setDate(java.util.Date datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);

        this.date = datetime
        dayInWeek = cal.get(Calendar.DAY_OF_WEEK);
        dayInMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayInYear = cal.get(Calendar.DAY_OF_YEAR);
        weekInMonth = cal.get(Calendar.WEEK_OF_MONTH);
        weekInYear = cal.get(Calendar.WEEK_OF_YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
    }

    def setDate(String dateString) {
        setDate(new SimpleDateFormat('dd/MM/yyyy').parse(dateString))
    }



    static belongsTo = [action:Action]

    static constraints = {
    }
}
