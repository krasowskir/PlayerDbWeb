package org.richard.home.service;

import org.richard.home.dao.TeamDAO;
import org.richard.home.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private TeamDAO teamDAO;

    @Autowired
    public TeamService(TeamDAO teamDAO) {
        this.teamDAO = teamDAO;
    }

    public List<Team> getAllTeams(){
        try {
            log.info("entering getAllTeams");
            List<Team> teams = this.teamDAO.selectAllTeams();
            return teams;
        } catch (SQLException e) {
            log.error("exception while fetching the teams");
            return List.of();
        }
    }
}
