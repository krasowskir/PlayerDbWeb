package org.richard.home.dao

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import org.richard.home.model.Address
import org.richard.home.model.Country
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.model.parallel.ExecutionMode
import spock.lang.Execution
import spock.lang.Shared
import spock.lang.Specification

@Execution(ExecutionMode.SAME_THREAD)
class PostgresAddressDAOSpec extends Specification {

    Logger log = LoggerFactory.getLogger(PostgresAddressDAOSpec.class);

    @Shared
    def dataSource

    def setup() {

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
    def 'calling saveAddress stores a new address'(){

        given:
        PostgresAddressDAO addressDAO = new PostgresAddressDAO(dataSource, dataSource)
        def toBeStoredAddr = new Address("Bonn", "Königswinterer Straße 419", '53123', Country.GERMANY)

        when:
        def result = addressDAO.saveAddress(toBeStoredAddr)

        then:
        result

        and:
        def existingAddr = addressDAO.getAddress(4)
        existingAddr.city == 'Bonn'
    }

}
