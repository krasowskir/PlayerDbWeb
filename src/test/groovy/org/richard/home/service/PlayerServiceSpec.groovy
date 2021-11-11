package org.richard.home.service

import org.richard.home.dao.PlayerDAO
import org.richard.home.dao.PostgresPlayerDAO
import org.richard.home.exception.InvalidInputException
import org.richard.home.model.Player
import spock.lang.Specification

class PlayerServiceSpec extends Specification {

    def "test fetchSinglePlayer"() {

        given:
        def mockedPlayerDAO = Mock(PlayerDAO.class){
            1 * getPlayer(_ as String) >> new Player('richard', 30)
        }

        def playerService = new PlayerService(mockedPlayerDAO)

        when:
        def result = playerService.fetchSinglePlayer('richard')

        then:
        result.name == 'richard'
        result.alter == 30
    }

    def "test fetchSinglePlayer with invalid input"() {

        given:
        def mockedPlayerDAO = Mock(PlayerDAO.class)

        def playerService = new PlayerService(mockedPlayerDAO)

        when:
        def result = playerService.fetchSinglePlayer(badInput as String)

        then:
        thrown(InvalidInputException)

        where:
        badInput << [null, '', '  ']
    }

    def 'test savePlayer with valid player'(){

        given:
        def mockedPlayerDAO = Mock(PlayerDAO.class){
            1 * savePlayer(_ as Player) >> persistenceResult
        }

        def playerService = new PlayerService(mockedPlayerDAO)

        when:
        def result = playerService.savePlayer(new Player('richard', 30))

        then:
        result == persistenceResult

        where:
        persistenceResult << [true, false]
    }
}
