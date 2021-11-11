package org.richard.home.service;

import org.richard.home.dao.PlayerDAO;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerService {
    Logger log = LoggerFactory.getLogger(PlayerService.class);

    private PlayerDAO playerDAO;

    public PlayerService(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    public Player fetchSinglePlayer(String name){
        log.debug("entering fetchSinglePlayer with name {}", name);
        if (name == null || name.trim().equals("")){
            throw new InvalidInputException("provided name was empty");
        }
        try {
            Player foundPlayer = this.playerDAO.getPlayer(name);
            log.info("player returned {}", foundPlayer);
            return foundPlayer;
        } catch (NotFoundException ne){
            return new Player("unknown", 0);
        }
    }

    public boolean savePlayer(Player toSave){
        log.debug("entering savePlayer with player {}", toSave);
        if (toSave == null){
            throw new InvalidInputException("Player cannot be null");
        }
        boolean result = this.playerDAO.savePlayer(toSave);
        log.info("stored player {} successfully: {}", toSave, result);
        return result;
    }


}
