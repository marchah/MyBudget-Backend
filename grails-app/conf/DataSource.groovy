dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    dialect = "org.hibernate.dialect.MySQL5Dialect"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://localhost/mybudget?autoReconnect=true"
            username = "root"
            password = "root"
        }
        /*dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }*/
    }
    test {
        dataSource {
            dbCreate = "create"//-drop"
            url = "jdbc:mysql://localhost/mybudgettest?autoReconnect=true"
            username = "root"
            password = "root"
        }
    }
    production {
        dataSource {
            dbCreate = "update"//-drop"
            url = "jdbc:mysql://localhost/mybudget?autoReconnect=true"
            username = "root"
            password = "root"
        }
    }
}
