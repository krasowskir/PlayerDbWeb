package org.richard.home.dao;

import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class TeamDAO {

    private static final Logger log = LoggerFactory.getLogger(TeamDAO.class);

    private DataSource master;
    private DataSource slave;

    static final String FETCH_TEAMS = "SELECT * FROM TEAMS";

    @Autowired
    public TeamDAO(@Qualifier("hikariDataSource") DataSource master, @Qualifier("readDataSource") DataSource slave) {
        this.master = master;
        this.slave = slave;
    }

    public List<Team> selectAllTeams() throws SQLException {
        log.debug("entering selectAllTeams");
        try (Statement con = slave.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            log.debug("connection could be established");
            ResultSet rs = con.executeQuery(FETCH_TEAMS);
            return mapResultSetToTeams(rs);
        }
    }

    private List<Team> mapResultSetToTeams(ResultSet rs){
        try {
            if (!rs.next()){
                log.error("no resultset");
                throw new NotFoundException("no teams found!");
            } else {
                log.debug("query contained teams");
                List<Team> teams = new ArrayList<>();
                do {
                    Team tmpTeam = new Team();
                    tmpTeam.setId(rs.getInt(1));
                    tmpTeam.setName(rs.getString(2));
                    tmpTeam.setBudget(rs.getInt(3));
                    tmpTeam.setLogo(rs.getBytes(4));
                    tmpTeam.setOwner(rs.getString(5));
                    teams.add(tmpTeam);
                } while (rs.next());
                return teams;
            }
        } catch (SQLException e) {
            log.error("exception while converting the results", e);
            log.debug("returning empty list");
            return List.of();
        }
    }
}
