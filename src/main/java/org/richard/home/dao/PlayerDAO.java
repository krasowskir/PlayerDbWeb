package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.model.Player;

import java.util.List;

public interface PlayerDAO {

    Player getPlayer(String name);

    List<Player> getPlayerByAlter(int alter);

    boolean savePlayer(Player toSave) throws DatabaseAccessFailed;

    boolean updatePlayer(Player toBe, String nameWhere) throws DatabaseAccessFailed;
}
