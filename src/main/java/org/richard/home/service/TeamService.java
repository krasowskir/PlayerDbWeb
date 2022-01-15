package org.richard.home.service;

import org.richard.home.dao.PlayerDAO;
import org.richard.home.dao.TeamDAO;
import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.model.Player;
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

    private PlayerDAO playerDAO;

    @Autowired
    public TeamService(TeamDAO teamDAO, PlayerDAO playerDAO) {
        this.teamDAO = teamDAO;
        this.playerDAO = playerDAO;
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

    public List<Player> getPlayersOfTeam(int teamId){
        try {
            log.info("entering getPlayersOfTeam");
            return this.playerDAO.getPlayersFromTeam(teamId);
        } catch ( DatabaseAccessFailed e) {
            log.error("exception while fetching the players");
            return List.of();
        }

    }
}
