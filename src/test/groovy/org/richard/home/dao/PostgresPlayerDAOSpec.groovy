package org.richard.home.dao

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import org.richard.home.exception.NotFoundException
import org.richard.home.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

/*
    ToDo: this class is an integration test, but it is treated as a unit test. As a consequence you can not utilize mvn
    parallel builds. The surefire plugin has to be configured to run sequentially.
 */
class PostgresPlayerDAOSpec extends Specification {

    Logger log = LoggerFactory.getLogger(PostgresPlayerDAOSpec.class)

    @Shared
    def dataSource

    def setup(){

        log.info('running setup')
        dataSource = new JdbcDataSource()
        dataSource.user = 'richard'
        dataSource.password = 'test123'
        dataSource.url = "jdbc:h2:mem:playerdb;MODE=PostgreSQL"

        log.info('connection valid?: {}', dataSource.getConnection().isValid(100))
        def flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(new Location('classpath:db/migration'))
                .load()
        def migrRes = flyway.migrate()

        log.info('migration successful?: {}', migrRes.success)
    }

    def 'a valid player can be searched in postgresPlayerDAO'(){

        log.info('a valid player can be searched in postgresPlayerDAO')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayer with a name'
        def found = postgresPlayerDAO.getPlayer('richard')

        then: 'always a player is returned'
        found.name == 'richard'
        found.alter == 30

    }

    def 'player #name throws an Exception in postgresPlayerDAO'(){

        log.info('player #name throws an Exception in postgresPlayerDAO')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayer with a name'
        def found = postgresPlayerDAO.getPlayer(name)

        then: 'always a player is returned'
        thrown(NotFoundException)

        where: 'name is found name or sometimes unknown'
        name << ['none', '', null]
    }

    def 'PostgresPlayerDAO can find players by their age'(){

        log.info('PostgresPlayerDAO can find players by their age')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayerByAlter'
        def foundPlayers = postgresPlayerDAO.getPlayerByAlter(age)

        then: 'always a list of players is returned'
        foundPlayers != null

        and:
        with(foundPlayers){
            size() == mySize
            get(0).getName() != null
        }

        where:
        age << [33, 30]
        mySize << [1, 2]

    }

    def 'PostgresPlayerDAO can persist a player'(){

        log.info('PostgresPlayerDAO can persist a player')

        given: 'the postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        and:
        def player = fromPlayer

        when: 'calling savePlayer with a valid player'
        def success = postgresPlayerDAO.savePlayer(player)

        then:
        success

        where:
        fromPlayer << [new Player('richard', 30), new Player('lidia', 33)]
    }

    def 'trying to find by age =0 throws exception'(){

        log.info('trying to find by age =0 throws exception')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)
        def impossibleAge = 0

        when: 'calling getPlayerByAlter'
        def foundPlayers = postgresPlayerDAO.getPlayerByAlter(impossibleAge)

        then: 'always a player is returned'
        thrown(NotFoundException)

    }

    def 'updating an existing player works'(){

        log.info('trying to find by age =0 throws exception')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling updatePlayer with a name'
        def result = postgresPlayerDAO.updatePlayer(new Player('lidia', 30), 'lidia')

        then: 'result is successfull'
        result

        and:
        def changedPlayer= postgresPlayerDAO.getPlayer('lidia')
        changedPlayer.name == 'lidia'
        changedPlayer.alter == 30
    }

    def 'updating an existing player with wrong name'(){

        log.info('trying to find by age =0 throws exception')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling updatePlayer with a name'
        def result = postgresPlayerDAO.updatePlayer(new Player('arnold', 30), 'notExisting')

        then: 'result is NOT successfull'
        !result

    }

}
