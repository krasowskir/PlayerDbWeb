package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PostgresPlayerDAO implements PlayerDAO {
    Logger log = LoggerFactory.getLogger(PostgresPlayerDAO.class);

    private static final String FIND_PLAYER_BY_NAME = "SELECT * FROM PLAYERS WHERE FIRST_NAME = ?";
    private static final String PERSIST_PLAYER = "INSERT INTO PLAYERS VALUES (DEFAULT, ?, ?)";
    private static final String FIND_PLAYERS_BY_AGE = "SELECT * FROM PLAYERS WHERE ALTER = ?";
    private static final String UPDATE_PLAYER = "UPDATE PLAYERS SET FIRST_NAME = ?, ALTER = ? WHERE FIRST_NAME = ?";

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
            throw new DatabaseAccessFailed(String.format("player %s not found in db", name), e);
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
    public synchronized boolean savePlayer(Player toSave) throws DatabaseAccessFailed {
        log.debug("savePlayer with name {} and alter {}", toSave.getName(), toSave.getAlter());
        int updRows = 0;
        try (Connection con = this.master.getConnection()) {
            log.debug("connection established? : {}", con.isValid(200));
            logWarningsOfConnection(con);
            try (PreparedStatement pS = con.prepareStatement(PERSIST_PLAYER)) {
                pS.setString(1, toSave.getName());
                pS.setInt(2, toSave.getAlter());
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
    public synchronized boolean updatePlayer(Player toBe, String nameWhere) throws DatabaseAccessFailed {
        log.debug("updatePlayer with name {} and alter {}", toBe.getName(), toBe.getAlter());
        int updRows = 0;
//        try (Connection con = this.slave.getConnection()) { PROBLEME BEIM TESTEN!!!
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

    private Player mapResultSetToPlayer(ResultSet rs, String name) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("player with name %s not found!", name));
        }
        // FORGOT TO CLOE THE ResultSet rs!!!
        return new Player(rs.getString("FIRST_NAME"), rs.getInt("ALTER"));
    }

    private List<Player> mapResultSetToList(ResultSet rs, int alter) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("player with alter {} not found!", alter));
        }
        rs.beforeFirst();
        List<Player> playerList = new ArrayList<>();
        while (rs.next()) {
            Player tmpPl = new Player(rs.getString("FIRST_NAME"), rs.getInt("ALTER"));
            playerList.add(tmpPl);
        }
        return playerList;
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
