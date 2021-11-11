package org.richard.home.dao

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import org.richard.home.exception.NotFoundException
import org.richard.home.model.Player
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


    def 'a valid player can be searched in postgresPlayerDAO'(){

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayer with a name'
        def found = postgresPlayerDAO.getPlayer(name)

        then: 'always a player is returned'
        found.name == nameFound
        found.alter == ageFound

        where: 'name is found name or sometimes unknown'
        name        |   nameFound       |   ageFound
        'richard'   |   'richard'       |   30
    }

    def 'player #name throws an Exception in postgresPlayerDAO'(){

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayer with a name'
        def found = postgresPlayerDAO.getPlayer(name)

        then: 'always a player is returned'
        thrown(NotFoundException)

        where: 'name is found name or sometimes unknown'
        name << ['none', '', null]
    }


    def 'PostgresPlayerDAO can persist a player'(){

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        and:
        def player = fromPlayer

        when:
        def success = postgresPlayerDAO.savePlayer(player)

        then:
        success

        where:
        fromPlayer << [new Player('richard', 30), new Player('lidia', 33)]
    }
}
