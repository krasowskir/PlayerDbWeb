package org.richard.home.service;

import org.richard.home.dao.PlayerDAO;
import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        } catch (NotFoundException e){
            return new Player("unknown", 0);
        }
    }

    public List<Player> fetchPlayersByAlter(int alter){
        try {
            List<Player> foundPlayers = playerDAO.getPlayerByAlter(alter);
            return foundPlayers;
        } catch (NotFoundException  ne){
            log.warn("no found players for age {}. Will return empty list.", alter, ne);
            return List.of();
        }
    }

    public boolean savePlayer(Player toSave) {
        log.debug("entering savePlayer with player {}", toSave);
        if (toSave == null){
            throw new InvalidInputException("Player cannot be null");
        }
        boolean result;
        try {
            result = this.playerDAO.savePlayer(toSave);
            log.info("stored player {} successfully: {}", toSave, result);
            return result;
        } catch (DatabaseAccessFailed de){
            log.error("saving player failed", de);
            return false;
        }
    }

    public boolean updatePlayer(Player toBe, String nameWhere){
        log.debug("entering updatePlayer with player {}", toBe);
        if (toBe == null){
            throw new InvalidInputException("Player cannot be null");
        }
        boolean result;
        try {
            result = this.playerDAO.updatePlayer(toBe, nameWhere);
            log.info("updated player {} successfully: {}", toBe, result);
            return result;
        } catch (DatabaseAccessFailed de){
            log.error("saving player failed", de);
            return false;
        }
    }



}
