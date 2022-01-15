package org.richard.home.service;

import org.richard.home.dao.AddressDAO;
import org.richard.home.dao.PlayerDAO;
import org.richard.home.dao.PostgresAddressDAO;
import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Address;
import org.richard.home.model.Player;
import org.richard.home.model.dto.PlayerWithAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PlayerService {
    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

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

    public Optional<List<PlayerWithAddress>> getAllPlayers(){
        List<PlayerWithAddress> playerList = new ArrayList<>();
        try {
            Map<Player, Address> playersWithAddresses = playerDAO.getAllPlayers();
            playersWithAddresses.forEach((player, addr) -> {
                playerList.add(new PlayerWithAddress(player.getName(), String.valueOf(player.getAlter()), addr.getCity(), 
                        addr.getStreet(), addr.getPlz(), addr.getCountry().getValue()));
            });
        } catch (DatabaseAccessFailed databaseAccessFailed) {
            log.error("error while getting all players");
        }
        return Optional.ofNullable(playerList);
    }

    public List<Player> getPlayersOfTeam(int teamId){
        try {
            log.info("entering getPlayersOfTeam");
            return this.playerDAO.getPlayersFromTeam(teamId);
        } catch ( DatabaseAccessFailed e) {
            log.error("exception while fetching the players");
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
            this.playerDAO.savePlayer(toSave);
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

    public boolean saveAddressForPlayer(Player player, Address toSave) throws SQLException {
        log.debug("entering saveAddress with Address {}", toSave);
        if (toSave == null || player == null){
            throw new InvalidInputException("Address or player cannot be null");
        }
        Connection connection = ((PostgresAddressDAO)addressDAO).getWriteDataSource().getConnection();
        connection.setAutoCommit(false);
        boolean  resultLivesIn;
        try {
            int storedId = this.addressDAO.saveAddress(toSave);
            toSave.setId(storedId);
            resultLivesIn = this.playerDAO.savePlayerLivesIn(player, toSave);
            log.info("stored address {} for player {} successfully: {}", toSave, player, resultLivesIn);
            connection.commit();
            return resultLivesIn;
        } catch (DatabaseAccessFailed de){
            log.warn("saving address failed", de);
            connection.rollback();
            return false;
        } finally {
            connection.close();
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
