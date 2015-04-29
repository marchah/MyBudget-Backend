# MyBudget-Backend

Unit Test UpToDate
--------------
* Type
* AuthenticationControllerSpec
* TypeController


TODO
----

* permistant data in unit test
* ~~mapping hasOne in Type isn't good (see more info with $>grails test-app Type)~~
* Not Restfull (TypeController: update)
* TypeController: enable to delete a type which has children so removed the parent ref (probably due to a wrong mapping)
* TypeController: unit test for delete not working: "String-based queries like [executeUpdate] are currently not supported in this implementation of GORM. Use criteria instead."
* ActionController: doesn't support Recurring
* @Validateable(nullable = true) -> contraints validation always return true