package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Address;
import org.richard.home.model.Country;
import org.richard.home.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component
public class PostgresPlayerDAO implements PlayerDAO {
    private static final Logger log = LoggerFactory.getLogger(PostgresPlayerDAO.class);

    private DataSource master;
    private DataSource slave;

    @Autowired
    public PostgresPlayerDAO(@Qualifier("hikariDataSource") DataSource writeDataSource, @Qualifier("readDataSource") DataSource readDataSource) {
        log.debug("constructor with master dataSource {} and slave {}", writeDataSource, readDataSource);
        this.master = writeDataSource;
        this.slave = readDataSource;
    }

    @Override
    public Player getPlayer(String name) throws DatabaseAccessFailed {
        log.debug("entering getPlayer with name {}", name);
        try (Connection con = this.slave.getConnection()) {
            log.debug("connection to db: {}", !con.isClosed());
            logWarningsOfConnection(con);
            try (PreparedStatement preparedStatement = con.prepareStatement(FIND_PLAYER_BY_NAME, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                return mapResultSetToPlayer(resultSet, name);
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            throw new NotFoundException(String.format("player %s not found in db", name), e);
        }
    }

    @Override
    public List<Player> getPlayerByAlter(int alter) throws DatabaseAccessFailed {
        log.debug("getPlayerByAlter with alter {}", alter);
        try (Connection con = this.slave.getConnection()) {
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(FIND_PLAYERS_BY_AGE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pS.setInt(1, alter);
                log.debug(pS.toString());
                ResultSet rs = pS.executeQuery();
                return mapResultSetToList(rs, alter);
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DatabaseAccessFailed(String.format("player with alter %d not found in db", alter), e);
        }
    }

    @Override
    public Map<Player, Address> getAllPlayers() throws DatabaseAccessFailed {
        log.debug("entering getAllPlayers");
        try (Connection con = this.slave.getConnection()) {
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(GET_ALL_PLAYERS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                log.debug(pS.toString());
                ResultSet rs = pS.executeQuery();
                return mapPlayersWithAddressesToList(rs);
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DatabaseAccessFailed("players could not be fetched!", e);
        }
    }

    @Override
    public synchronized int savePlayer(Player toSave) throws DatabaseAccessFailed {
        log.debug("savePlayer with name {} and alter {}", toSave.getName(), toSave.getAlter());
        try (Connection con = this.master.getConnection()) {
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(PERSIST_PLAYER)) {
                pS.setInt(1, toSave.getId());
                pS.setString(2, toSave.getName());
                pS.setInt(3, toSave.getAlter());
                pS.setString(4, toSave.getPosition());
                pS.setDate(5, Date.valueOf(toSave.getDateOfBirth()));
                pS.setString(6, toSave.getCountryOfBirth().getValue());
                log.debug(pS.toString());
                return pS.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DatabaseAccessFailed("database access while savePlayer", e);
        }
    }

    @Override
    public List<Player> savePlayerList(List<Player> toSaveList) throws DatabaseAccessFailed {
        log.debug("savePlayerList withlist size: {}", toSaveList.size());
        try (Connection con = this.master.getConnection()) {
            con.setAutoCommit(false);
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(PERSIST_PLAYER)) {
                for (Player player : toSaveList){
                    try {
                        pS.setInt(1, player.getId());
                        pS.setString(2, player.getName());
                        pS.setInt(3, player.getAlter());
                        pS.setString(4, player.getPosition());
                        pS.setDate(5, Date.valueOf(player.getDateOfBirth()));
                        pS.setString(6, player.getCountryOfBirth().getValue());
                        log.debug(pS.toString());
                        pS.executeUpdate();
                        con.commit();
                    } catch (Exception e){
                        log.error(e.getClass().getName());
                        //ignores
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DatabaseAccessFailed("database access while savePlayer", e);
        }
        return toSaveList;
    }

    @Override
    public synchronized boolean updatePlayer(Player toBe, String nameWhere) throws DatabaseAccessFailed {
        log.debug("updatePlayer with name {} and alter {}", toBe.getName(), toBe.getAlter());
        int updRows = 0;
        try (Connection con = this.master.getConnection()) {
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(UPDATE_PLAYER,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                pS.setString(1, toBe.getName());
                pS.setInt(2, toBe.getAlter());
                pS.setString(3, nameWhere);
                log.debug(pS.toString());
                updRows = pS.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DatabaseAccessFailed("database access while savePlayer", e);
        }
        return updRows > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public boolean savePlayerLivesIn(Player toSave, Address whereLive) throws DatabaseAccessFailed {
        log.debug("savePlayerLivesIn with name {} and address {}", toSave.getName(), whereLive.toString());
        int updRows = 0;
        try (Connection con = this.master.getConnection()) {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            con.setAutoCommit(false);
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(SAVE_PLAYER_LIVES_IN,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                pS.setInt(1, toSave.getId());
                pS.setInt(2, whereLive.getId()); //
                log.debug(pS.toString());
                updRows = pS.executeUpdate();
                con.commit();
            }
        } catch (Exception e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DatabaseAccessFailed("database access while savePlayerLivesIn", e);
        }
        return updRows > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<Player> getPlayersFromTeam(int teamId) throws DatabaseAccessFailed {
        log.debug("getPlayersFromTeam with teamId {}", teamId);
        try (Connection con = this.slave.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_ALL_PLAYERS_FROM_TEAM, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                ps.setInt(1, teamId);
                ResultSet rs = ps.executeQuery();
                return mapResultSetToList(rs,0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private Player mapResultSetToPlayer(ResultSet rs, String name) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("player with name %s not found!", name));
        }
        // FORGOT TO CLOSE THE ResultSet rs!!!
        Player tmpPlayer = new Player(rs.getInt("id"),rs.getString("name"), rs.getInt("ALTER"), rs.getString("position"), rs.getDate("date_of_birth").toLocalDate(),Country.valueOf(rs.getString("country_of_birth").toUpperCase()));
        tmpPlayer.setId(rs.getInt("id"));
        return tmpPlayer;
    }

    private List<Player> mapResultSetToList(ResultSet rs, int alter) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("player with alter {} not found!", alter));
        }
        rs.beforeFirst();
        List<Player> playerList = new ArrayList<>();
        while (rs.next()) {
            Player tmpPl = new Player(rs.getInt("id"),rs.getString("name"), rs.getInt("ALTER"), rs.getString("position"), rs.getDate("date_of_birth").toLocalDate(),Country.valueOf(rs.getString("country_of_birth").toUpperCase()));
            playerList.add(tmpPl);
        }
        return playerList;
    }

    private Map<Player, Address> mapPlayersWithAddressesToList(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("players could not be fetched!"));
        }
        rs.beforeFirst();
        Map<Player, Address> playersWithAddresses = new HashMap<>();
        while (rs.next()) {
            Player tmpPl = new Player(rs.getInt("id"),rs.getString("name"), rs.getInt("ALTER"), rs.getString("position"), rs.getDate("date_of_birth").toLocalDate(),Country.valueOf(rs.getString("country_of_birth").toUpperCase()));
            tmpPl.setId(rs.getInt("id"));
            Address tmpAddr = new Address(rs.getInt("id"),rs.getString("city"),rs.getString("street"),
                    rs.getString("plz"), Country.valueOf(rs.getString("country")));

            playersWithAddresses.put(tmpPl, tmpAddr);
        }
        return playersWithAddresses;
    }

    private void logWarningsOfConnection(Connection con) throws SQLException {
        SQLWarning warn = con.getWarnings();
        while (warn != null) {
            log.warn("SQLState: {}", warn.getSQLState());
            log.warn("Message: {}", warn.getMessage());
            log.warn("Vendor: {}", warn.getErrorCode());
            warn = warn.getNextWarning();
        }
    }
}
