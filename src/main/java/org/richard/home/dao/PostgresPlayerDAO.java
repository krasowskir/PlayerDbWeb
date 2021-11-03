package org.richard.home.dao;

import org.richard.home.model.Player;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PostgresPlayerDAO implements PlayerDAO {

    private static final String FIND_PLAYER_BY_NAME = "SELECT * FROM PLAYER where name = ?";

    private DataSource dataSource;
    private String username, password;

    public PostgresPlayerDAO(DataSource dataSource, String username, String password) {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
    }

    @Override
    public Player getPlayer(String name) {
        try {
            Connection con = this.dataSource.getConnection(username, password);
            PreparedStatement preparedStatement = con.prepareStatement(FIND_PLAYER_BY_NAME);
            preparedStatement.setString(0, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            return mapResultSetToPlayer(resultSet);
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

        return new Player(rs.getInt("ALTER"), rs.getString("NAME"));
    }
}
