package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.model.Address;
import org.richard.home.model.Player;

import java.util.List;
import java.util.Map;

public interface PlayerDAO {

    Player getPlayer(String name) throws DatabaseAccessFailed;

    List<Player> getPlayerByAlter(int alter) throws DatabaseAccessFailed;

    Map<Player, Address> getAllPlayers() throws DatabaseAccessFailed;

    int savePlayer(Player toSave) throws DatabaseAccessFailed;

    List<Player> savePlayerList(List<Player> toSaveList) throws DatabaseAccessFailed;

    boolean updatePlayer(Player toBe, String nameWhere) throws DatabaseAccessFailed;

    boolean savePlayerLivesIn(Player toSave, Address whereLive) throws DatabaseAccessFailed;
}
