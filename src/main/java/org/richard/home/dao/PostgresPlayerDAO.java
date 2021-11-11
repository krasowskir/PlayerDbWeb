package org.richard.home.dao;

import org.richard.home.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class PostgresPlayerDAO implements PlayerDAO {
    Logger log = LoggerFactory.getLogger(PostgresPlayerDAO.class);

    private static final String FIND_PLAYER_BY_NAME = "SELECT * FROM PLAYERS where first_name = ?";
    private static final String PERSIST_PLAYER = "insert into PLAYERS values (default, ?, ?)";

    private DataSource master;
    private DataSource slave;

    @Autowired
    public PostgresPlayerDAO(@Qualifier("hikariDataSource") DataSource writeDataSource, @Qualifier("readDataSource") DataSource readDataSource) {
        log.debug("constructor with master dataSource {} and slave {}", master, readDataSource);
        this.master = writeDataSource;
        this.slave = readDataSource;
    }

    @Override
    public Player getPlayer(String name) {
        log.debug("entering getPlayer with name {}", name);
        try (Connection con = this.slave.getConnection()) {
            log.debug("connection to db: {}", !con.isClosed());
            try (PreparedStatement preparedStatement = con.prepareStatement(FIND_PLAYER_BY_NAME)) {
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                return mapResultSetToPlayer(resultSet);
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
        }
        return new Player("unknown", 0);
    }

    @Override
    public List<Player> getPlayerByAlter(int alter) {
        return null;
    }

    @Override
    public boolean savePlayer(Player toSave) {
        log.debug("savePlayer with name {} and alter {}", toSave.getName(), toSave.getAlter());
        int updRows = 0;
        try (Connection con = this.master.getConnection()) {
            log.debug("connection established? : {}", con.isValid(200));
            try (PreparedStatement pS = con.prepareStatement(PERSIST_PLAYER)) {
                pS.setString(1, toSave.getName());
                pS.setInt(2, toSave.getAlter());
                log.debug(pS.toString());
                updRows = pS.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return updRows > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        rs.next();

        return new Player(rs.getString("FIRST_NAME"), rs.getInt("ALTER"));
    }
}
