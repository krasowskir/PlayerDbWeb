package org.richard.home.service

import org.richard.home.dao.PlayerDAO
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
}
