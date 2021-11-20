package org.richard.home.service;

import org.richard.home.dao.AddressDAO;
import org.richard.home.dao.PlayerDAO;
import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Address;
import org.richard.home.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlayerService {
    Logger log = LoggerFactory.getLogger(PlayerService.class);

    private PlayerDAO playerDAO;
    private AddressDAO addressDAO;

    @Autowired
    public PlayerService(PlayerDAO playerDAO, AddressDAO addressDAO) {
        this.playerDAO = playerDAO;
        this.addressDAO = addressDAO;
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
        } catch (NotFoundException | DatabaseAccessFailed e){
            log.warn("no found players for name {}. Will return unknown player.", name);
            throw new NotFoundException();
        }
    }

    public List<Player> fetchPlayersByAlter(int alter){
        try {
            List<Player> foundPlayers = playerDAO.getPlayerByAlter(alter);
            return foundPlayers;
        } catch (NotFoundException | DatabaseAccessFailed ne){
            log.warn("no found players for age {}. Will return empty list.", alter, ne);
            return List.of();
        }
    }


    public Player savePlayer(Player toSave) throws DatabaseAccessFailed {
        log.debug("entering savePlayer with player {}", toSave);
        if (toSave == null){
            throw new InvalidInputException("Player cannot be null");
        }
        boolean result;
        try {
            int genKey = this.playerDAO.savePlayer(toSave);
            toSave.setId(genKey);
            log.info("stored player {} successfully!", toSave);
            return toSave;
        } catch (DatabaseAccessFailed de){
            log.warn("saving player failed", de);
            throw de;
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
            log.warn("saving player failed", de);
            return false;
        }
    }

    public boolean saveAddressForPlayer(Player player, Address toSave){
        log.debug("entering saveAddress with Address {}", toSave);
        if (toSave == null || player == null){
            throw new InvalidInputException("Address or player cannot be null");
        }
        boolean  resultLivesIn;
        try {
            int storedId = this.addressDAO.saveAddress(toSave);
            toSave.setId(storedId);
            resultLivesIn = this.playerDAO.savePlayerLivesIn(player, toSave);
            log.info("stored address {} for player {} successfully: {}", toSave, player, resultLivesIn);
            return resultLivesIn;
        } catch (DatabaseAccessFailed de){
            log.warn("saving address failed", de);
            return false;
        }
    }

    public Optional<Address> getAddressById(long id){
        log.debug("entering getAddressById with id {}", id);
        try {
            Address foundAddress = this.addressDAO.getAddress(id);
            log.info("address returned {}", foundAddress);
            return Optional.of(foundAddress);
        } catch (DatabaseAccessFailed e){
            log.warn("DatabaseAccessFailed for id {}", id);
            return Optional.empty();
        } catch (NotFoundException ne){
            log.warn("no found addresses for id {}", id);
            return Optional.empty();
        }
    }

//    public Address fetchAddressesLike(String name){
//        log.debug("entering fetchAddressesLike with name {}", name);
//
//    }

}
