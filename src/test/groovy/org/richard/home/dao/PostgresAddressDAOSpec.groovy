package org.richard.home.dao

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import org.richard.home.exception.DatabaseAccessFailed
import org.richard.home.exception.NotFoundException
import org.richard.home.model.Address
import org.richard.home.model.Country
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

@Execution(ExecutionMode.SAME_THREAD)
class PostgresAddressDAOSpec extends Specification {

    private static Logger log = LoggerFactory.getLogger(PostgresAddressDAOSpec.class);

    @Shared
    def dataSource

    @Shared
    def con

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

        log.info('migration successful?: {}', flyway.migrate().success)
    }

    def cleanup(){
        con.close()
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def "calling getAddress yields an address, stored in the db"() {

        given:
        PostgresAddressDAO addressDAO = new PostgresAddressDAO(dataSource, dataSource)

        when:
        def foundAddress = addressDAO.getAddress(1l)

        then:
        foundAddress != null
        foundAddress.city == 'Dresden'
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def "calling getAddress throws NotFoundException for a not existing address"() {

        given:
        PostgresAddressDAO addressDAO = new PostgresAddressDAO(dataSource, dataSource)

        when: 'id is valid and logic is correct'
        def foundAddress = addressDAO.getAddress(100l)

        then: 'because resultSet.next() is false'
        thrown(NotFoundException)
    }

    def 'calling getAddress with a flawed dataSource'(){

        given:
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer, _ as Integer) >> Mock(PreparedStatement) {
                setLong(_ as Integer, _ as Long) >> {}
                executeQuery() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }

        and: 'PostgressAddressDAO'
        PostgresAddressDAO addressDAO = new PostgresAddressDAO(mockedDataSource, mockedDataSource)

        when: ''
        def foundAddress = addressDAO.getAddress(2l)

        then: 'SQLException is mapped to DatabaseAccessFailed exception'
        thrown(DatabaseAccessFailed)
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'calling saveAddress stores a new address'(){

        given:
        PostgresAddressDAO addressDAO = new PostgresAddressDAO(dataSource, dataSource)
        def toBeStoredAddr = new Address("Bonn", "Königswinterer Straße 419", '53123', Country.GERMANY)

        when:
        def resultId = addressDAO.saveAddress(toBeStoredAddr)

        then:
        resultId != null

        and:
        def existingAddr = addressDAO.getAddress(resultId)
        existingAddr.city == 'Bonn'
    }

    @Execution(ExecutionMode.SAME_THREAD)
    def 'calling saveAddress with a flawed dataSource'(){

        given:
        Connection mockedConnection = Mock(Connection) {
            prepareStatement(_ as String, _ as Integer) >> Mock(PreparedStatement) {
                setString(_ as Integer, _ as String) >> {}
                executeUpdate() >> {
                    throw new SQLException()
                }
            }
        }
        def mockedDataSource = Mock(DataSource) {
            getConnection() >> mockedConnection
        }
        PostgresAddressDAO addressDAO = new PostgresAddressDAO(mockedDataSource, mockedDataSource)
        def toBeStoredAddr = new Address("Bonn", "Königswinterer Straße 419", '53123', Country.GERMANY)

        when:
        addressDAO.saveAddress(toBeStoredAddr)

        then:
        thrown(DatabaseAccessFailed)
    }
}
