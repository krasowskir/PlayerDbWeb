package org.richard.home.dao

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import spock.lang.Shared
import spock.lang.Specification

class PostgresPlayerDAOSpec extends Specification {

    @Shared
    def dataSource

    def setupSpec(){

        dataSource = new JdbcDataSource()
        dataSource.user = 'richard'
        dataSource.password = 'test123'
        dataSource.url = "jdbc:h2:mem:playerdb;MODE=PostgreSQL"

        System.out.println('connection valid?: ' + dataSource.getConnection().isValid(100))
        def flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(new Location('classpath:db/migration'))
                .load()
        def migrRes = flyway.migrate()

        System.out.println('migration successful?: ' + migrRes.success)
    }

    def 'PostgresPlayerDAO can be instantiated'(){

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when:
        def found = postgresPlayerDAO.getPlayer('richard')

        then:
        found.name == 'richard'
        found.alter == 30
    }
}
