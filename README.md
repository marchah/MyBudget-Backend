# MyBudget-Backend


TODO
----

* permistant data in unit test
* ~~mapping hasOne in Type isn't good (see more info with $>grails test-app Type)~~
* Not Restfull (update: POST)
* TypeController: enable to delete a type which has children so removed the parent ref (probably due to a wrong mapping)
* TypeController: unit test for delete not working: "String-based queries like [executeUpdate] are currently not supported in this implementation of GORM. Use criteria instead."
* ActionController: doesn't support Recurring
* ActionController: when update and don't set idRecurring in ActionCommand doesn't remove the recurring, not really a problems.
* RecurringController: doesn't support endDate
* Unit Test: Mapping and Filter
* Clean code in ActionController and add unit test