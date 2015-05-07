package mybudget

import java.text.SimpleDateFormat

class Date {

    int day
    int week
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

    def setDate(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.getTime());

        day = cal.get(Calendar.DAY_OF_MONTH);
        week = cal.get(Calendar.WEEK_OF_YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
    }

    def setDate(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat('dd/MM/yyyy').parse(date));

        day = cal.get(Calendar.DAY_OF_MONTH);
        week = cal.get(Calendar.WEEK_OF_YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
    }



    static belongsTo = [action:Action]

    static constraints = {
    }
}
