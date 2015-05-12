package Utils
/**
 * Created by marcha on 5/12/15.
 */
class ParamConverter {

    def static ConvertListStringToListLong(def listString) {
        def listLong = [];

        if (listString != null) {
            for (String str : listString) {
                listLong.add(Long.parseLong(str))
            }
        }
        return listLong
    }

    def static ConvertStringDateToDateTab(String dateString, String dateFormat, datesActionAvailable) {
        def dateActionAvailable = [day:0,week:0,month:0,year:0]

        if (dateString) {
            def tab = dateString.split('/')
            if (dateFormat == 'month' && tab.length == 2) {
                dateActionAvailable.month = Integer.parseInt(tab[0])
                dateActionAvailable.year = Integer.parseInt(tab[1])
            } else if (dateFormat == 'day' && tab.length == 3) {
                dateActionAvailable.day = Integer.parseInt(tab[0])
                dateActionAvailable.month = Integer.parseInt(tab[1])
                dateActionAvailable.year = Integer.parseInt(tab[2])
            } else if (dateFormat == 'week' && tab.length == 2) {
                dateActionAvailable.week = Integer.parseInt(tab[0])
                dateActionAvailable.year = Integer.parseInt(tab[1])
            } else if (tab.length == 1) {
                dateActionAvailable.year = Integer.parseInt(tab[0])
            } else {
                dateActionAvailable = datesActionAvailable[datesActionAvailable.size() - 1]
            }
        } else {
            dateActionAvailable = datesActionAvailable[datesActionAvailable.size() - 1]
        }
        return dateActionAvailable
    }

    def static ConvertDateIdFromDateFormat(def dates, String dateFormat) {
        for (def tmp : dates) {
            if (dateFormat == 'month') {
                tmp.id = tmp.month + '/' + tmp.year
            } else if (dateFormat == 'day') {
                tmp.id = tmp.day + '/' + tmp.month + '/' + tmp.year
            } else if (dateFormat == 'week') {
                tmp.id = tmp.week + '/' + tmp.year
            } else {
                tmp.id = tmp.year
            }
        }
        return dates
    }
}
