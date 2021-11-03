package org.richard.home.service;

import org.richard.home.dao.PlayerDAO;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Player;

public class PlayerService {

    private PlayerDAO playerDAO;

    public PlayerService(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    public Player fetchSinglePlayer(String name){
        if (name.trim().equals("")){
            throw new InvalidInputException("provided name was empty");
        }
        Player foundPlayer = this.playerDAO.getPlayer(name);
        if (foundPlayer.getName().equals("unknown") || foundPlayer == null){
            throw new NotFoundException(String.format("for Player with name %s no result was found!", name));
        } else {
            return foundPlayer;
        }
    }
}
