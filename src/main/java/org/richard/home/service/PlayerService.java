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
        if (name.trim().equals("")){
            throw new InvalidInputException("provided name was empty");
        }
        Player foundPlayer = this.playerDAO.getPlayer(name);
        log.info("player found {}", foundPlayer);
        if (foundPlayer.getName().equals("unknown") || foundPlayer == null){
            throw new NotFoundException(String.format("for Player with name %s no result was found!", name));
        } else {
            return foundPlayer;
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
