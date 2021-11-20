package org.richard.home.dao

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import org.richard.home.exception.DatabaseAccessFailed
import org.richard.home.exception.NotFoundException
import org.richard.home.model.Address
import org.richard.home.model.Country
import org.richard.home.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.model.parallel.ExecutionMode
import spock.lang.Execution
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

/*
    ToDo: this class is an integration test, but it is treated as a unit test. As a consequence you can not utilize mvn
    parallel builds. The surefire plugin has to be configured to run sequentially.
 */
@Execution(ExecutionMode.SAME_THREAD)
class PostgresPlayerDAOSpec extends Specification {

    private static Logger log = LoggerFactory.getLogger(PostgresPlayerDAOSpec.class)

    @Shared
    def dataSource

    @Shared
    Connection con

    def setup() {

        log.info('running setup')
        dataSource = new JdbcDataSource()
        dataSource.user = 'richard'
        dataSource.password = 'test123'
        dataSource.url = "jdbc:h2:mem:playerdb;MODE=PostgreSQL"

        con = dataSource.getConnection()
        log.info('connection valid?: {}', con.isValid(100))
        def flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(new Location('classpath:db/migration'))
                .load()
        def migrRes = flyway.migrate()

        log.info('migration successful?: {}', migrRes.success)
    }

    def cleanup(){
        con.close()
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'a valid player can be searched in postgresPlayerDAO'() {

        log.info('a valid player can be searched in postgresPlayerDAO')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayer with a name'
        def found = postgresPlayerDAO.getPlayer('richard')

        then: 'always a player is returned'
        found.name == 'richard'
        found.alter == 30

    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'trying to fetch a player with a flawed dataSource leads to DataAccessException'() {

        log.info('trying to fetch a player with a flawed dataSource leads to DataAccessException')

        given: 'a flacky dataSource'
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer, _ as Integer) >> Mock(PreparedStatement) {
                setString(_ as int, _ as String) >> {}
                setInt(_ as int, _ as int) >> {}
                executeQuery() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }

        and: 'postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(mockedDataSource, mockedDataSource)

        when: 'calling getPlayer with a valid parameter'
        postgresPlayerDAO.getPlayer('richard')

        then: 'an exception is thrown from service layer'
        thrown(NotFoundException)
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'player #name throws an NotFoundException in postgresPlayerDAO'() {

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

    @Execution(ExecutionMode.SAME_THREAD)
    def 'PostgresPlayerDAO can find players by their age'() {

        log.info('PostgresPlayerDAO can find players by their age')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling getPlayerByAlter'
        def foundPlayers = postgresPlayerDAO.getPlayerByAlter(33)

        then: 'always a list of players is returned'
        foundPlayers != null

        and:
        with(foundPlayers) {
            size() == 1
            get(0).getName() != null
        }

    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'findByAlter with a flawed dataSource leads to DataAccessException'() {

        log.info('findByAlter with a flawed dataSource leads to DataAccessException')

        given: 'a flacky dataSource'
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer, _ as Integer) >> Mock(PreparedStatement) {
                setString(_ as int, _ as String) >> {}
                setInt(_ as int, _ as int) >> {}
                executeQuery() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }

        and: 'postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(mockedDataSource, mockedDataSource)

        when: 'calling getPlayer with a valid parameter'
        postgresPlayerDAO.getPlayerByAlter(30)

        then: 'an exception is thrown from service layer'
        thrown(DatabaseAccessFailed)
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'PostgresPlayerDAO can persist a player'() {

        log.info('PostgresPlayerDAO can persist a player')

        given: 'the postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        and:
        def player = fromPlayer

        when: 'calling savePlayer with a valid player'
        def playerId = postgresPlayerDAO.savePlayer(player)

        then:
        playerId != 0

        where:
        fromPlayer << [new Player('Toni', 29), new Player('Katja', 29)]
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'persisting leads to an error'() {

        log.info('persisting leads to an error')

        given: 'a flacky dataSource'
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer) >> Mock(PreparedStatement) {
                executeUpdate() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }

        and: 'postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(mockedDataSource, mockedDataSource)

        when: 'calling savePlayer with a valid player'
        postgresPlayerDAO.savePlayer(new Player('testi', 28))

        then:'an exception is thrown from service layer'
        thrown(DatabaseAccessFailed)
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'calling savePlayerLivesIn stores an address and then the player lives'() {

        log.info('PostgresPlayerDAO can persist a player')

        given: 'the postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        and: 'a simulated persisted address (id = 1 already exists in db)'
        def address = new Address()
        address.id = 1

        and: 'saving the player in the db'
        def playerId = postgresPlayerDAO.savePlayer(fromPlayer)
        fromPlayer.id = playerId

        when: 'calling savePlayer with a saved player and a persisted address'
        def success = postgresPlayerDAO.savePlayerLivesIn(fromPlayer, address)

        then:
        success

        where:
        fromPlayer << [new Player('richard', 30), new Player('lidia', 33)]
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'calling savePlayerLivesIn handles db exceptions'(){
        log.info('saveAddressForPlayer leads to an error')

        given: 'a flacky dataSource'
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer) >> Mock(PreparedStatement) {
                executeUpdate() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }

        and: 'postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(mockedDataSource, mockedDataSource)

        when: 'calling savePlayer with a valid player'
        postgresPlayerDAO.savePlayer(new Player('richard', 30))

        then: 'an exception is thrown from service layer'
        thrown(DatabaseAccessFailed)
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'trying to find by age =0 throws exception'() {

        log.info('trying to find by age =0 throws exception')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)
        def impossibleAge = 0

        when: 'calling getPlayerByAlter'
        def foundPlayers = postgresPlayerDAO.getPlayerByAlter(impossibleAge)

        then: 'always a player is returned'
        thrown(NotFoundException)

    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'updating an existing player works'() {

        log.info('trying to find by age =0 throws exception')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling updatePlayer with a name'
        def result = postgresPlayerDAO.updatePlayer(new Player('lidia', 30), 'lidia')

        then: 'result is successfull'
        result

        and:
        def changedPlayer = postgresPlayerDAO.getPlayer('lidia')
        changedPlayer.name == 'lidia'
        changedPlayer.alter == 30
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'updating an existing player with wrong name'() {

        log.info('trying to find by age =0 throws exception')

        given:
        def postgresPlayerDAO = new PostgresPlayerDAO(dataSource, dataSource)

        when: 'calling updatePlayer with a name'
        def result = postgresPlayerDAO.updatePlayer(new Player('arnold', 30), 'notExisting')

        then: 'result is NOT successfull'
        !result

    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'updating a player - unhappy path'() {

        log.info('updating a player - unhappy path')

        given: 'a flacky dataSource'
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer, _ as Integer) >> Mock(PreparedStatement) {
                setString(_ as int, _ as String) >> {}
                setInt(_ as int, _ as int) >> {}
                executeUpdate() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }

        and: 'postgresPlayerDAO'
        def postgresPlayerDAO = new PostgresPlayerDAO(mockedDataSource, mockedDataSource)

        when: 'calling savePlayer with a valid player'
        def success = postgresPlayerDAO.updatePlayer(new Player('richard', 31), 'richard')

        then: 'an exception is thrown from service layer'
        thrown(DatabaseAccessFailed)
    }

}
