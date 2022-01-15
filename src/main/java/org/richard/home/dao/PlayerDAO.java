package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.model.Address;
import org.richard.home.model.Player;

import java.util.List;
import java.util.Map;

public interface PlayerDAO {
     String FIND_PLAYER_BY_NAME = "SELECT * FROM PLAYERS WHERE name = ?";
     String PERSIST_PLAYER = "INSERT INTO PLAYERS VALUES (?, ?, ?, ?, ?, ?)";
     String FIND_PLAYERS_BY_AGE = "SELECT * FROM PLAYERS WHERE ALTER = ?";
     String UPDATE_PLAYER = "UPDATE PLAYERS SET name = ?, ALTER = ? WHERE name = ?";
     String SAVE_PLAYER_LIVES_IN = "INSERT INTO LIVES_IN VALUES (?, ?)";
     String GET_ALL_PLAYERS = "SELECT P.*, A.* FROM PLAYERS P INNER JOIN LIVES_IN LI ON P.ID = LI.PLAYER_ID INNER JOIN ADDRESSES A ON LI.ADDRESS_ID = A.ID";
     String GET_ALL_PLAYERS_FROM_TEAM = "SELECT * FROM PLAYERS p INNER JOIN UNDER_CONTRACT uc ON p.id = uc.playerId WHERE uc.teamId = ?";
    
    Player getPlayer(String name) throws DatabaseAccessFailed;

    List<Player> getPlayerByAlter(int alter) throws DatabaseAccessFailed;

    Map<Player, Address> getAllPlayers() throws DatabaseAccessFailed;

    List<Player> getPlayersFromTeam(int teamId) throws DatabaseAccessFailed;

    int savePlayer(Player toSave) throws DatabaseAccessFailed;

    List<Player> savePlayerList(List<Player> toSaveList) throws DatabaseAccessFailed;

    boolean updatePlayer(Player toBe, String nameWhere) throws DatabaseAccessFailed;

    boolean savePlayerLivesIn(Player toSave, Address whereLive) throws DatabaseAccessFailed;
}
