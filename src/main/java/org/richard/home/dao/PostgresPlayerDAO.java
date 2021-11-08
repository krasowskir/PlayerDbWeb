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
import java.util.List;

@Component
public class PostgresPlayerDAO implements PlayerDAO {
    Logger log = LoggerFactory.getLogger(PostgresPlayerDAO.class);

    private static final String FIND_PLAYER_BY_NAME = "SELECT * FROM PLAYERS where first_name = ?";

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
            try (PreparedStatement preparedStatement = con.prepareStatement(FIND_PLAYER_BY_NAME)){
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                return mapResultSetToPlayer(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Player(0, "unknown");
    }

    @Override
    public List<Player> getPlayerByAlter(int alter) {
        return null;
    }

    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        rs.next();

        return new Player(rs.getInt("ALTER"), rs.getString("FIRST_NAME"));
    }
}
