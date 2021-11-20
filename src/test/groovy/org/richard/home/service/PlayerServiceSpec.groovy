package org.richard.home.service

import org.richard.home.dao.AddressDAO
import org.richard.home.dao.PlayerDAO
import org.richard.home.exception.DatabaseAccessFailed
import org.richard.home.exception.InvalidInputException
import org.richard.home.exception.NotFoundException
import org.richard.home.model.Player
import spock.lang.Specification

class PlayerServiceSpec extends Specification {

    def "test fetchSinglePlayer"() {

        given:
        def mockedAddressDAO = Mock(AddressDAO.class)
        def mockedPlayerDAO = Mock(PlayerDAO.class){
            1 * getPlayer(_ as String) >> new Player('richard', 30)
        }

        def playerService = new PlayerService(mockedPlayerDAO, mockedAddressDAO)

        when:
        def result = playerService.fetchSinglePlayer('richard')

        then:
        result.name == 'richard'
        result.alter == 30
    }

    def "test fetchSinglePlayer with invalid input"() {

        given:
        def mockedPlayerDAO = Mock(PlayerDAO.class)
        def mockedAddressDAO = Mock(AddressDAO.class)
        def playerService = new PlayerService(mockedPlayerDAO, mockedAddressDAO)

        when:
        def result = playerService.fetchSinglePlayer(badInput as String)

        then:
        thrown(InvalidInputException)

        where:
        badInput << [null, '', '  ']
    }

    def 'test fetchSinglePlayer that cant be found'(){
        given:
        def mockedPlayerDAO = Mock(PlayerDAO.class){
            getPlayer(_) >> { throw  new NotFoundException()}
        }
        def mockedAddressDAO = Mock(AddressDAO.class)
        def playerService = new PlayerService(mockedPlayerDAO, mockedAddressDAO)

        when:
        def result = playerService.fetchSinglePlayer('notExisting')

        then:
        thrown(NotFoundException)
    }

    def 'fetchPlayers by age'(){

        given:
        def mockedAddressDAO = Mock(AddressDAO.class)
        def mockedPlayerDAO = Mock(PlayerDAO){
            1 * getPlayerByAlter(_ as Integer) >> List.of(
                    new Player('richard', 30),
                    new Player('waldemar', 30)
            )
        }

        def playerService = new PlayerService(mockedPlayerDAO, mockedAddressDAO)

        when:
        def result = playerService.fetchPlayersByAlter(30)

        then:
        result.size() == 2

        and:
        with(result[0]){
            name == 'richard'
            alter == 30
        }
    }

    def 'test savePlayer with valid player'(){

        given:
        def mockedAddressDAO = Mock(AddressDAO.class)
        def mockedPlayerDAO = Mock(PlayerDAO.class){
            1 * savePlayer(_ as Player) >> 1
        }

        def playerService = new PlayerService(mockedPlayerDAO, mockedAddressDAO)

        when:
        def result = playerService.savePlayer(new Player('richard', 30))

        then:
        result != 0
    }

    def 'test updatePlayer - happy path'(){

        given:
        def mockedAddressDAO = Mock(AddressDAO.class)
        def mockedPlayerDAO = Mock(PlayerDAO.class){
            updatePlayer(_, 'lidia') >> new Player('lidia',28)
            updatePlayer(_, 'NONE') >> {
                throw new DatabaseAccessFailed()
            }
        }

        def playerService = new PlayerService(mockedPlayerDAO, mockedAddressDAO)

        when:
        def result = playerService.updatePlayer(new Player('lidia', 28), matchedName)

        then:
        result == updateResult

        where:
        updateResult << [true, false]
        matchedName << ['lidia', 'NONE']
    }

    def 'saveAddressForPlayer saves the address and player information'(){

    }
}
