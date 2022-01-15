package db.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.richard.home.model.Player;
import org.richard.home.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.util.Comparator.comparing;

// ToDo: write tests for that!!! Maybe refactor to use DAO layer for persisting players
public class V4__INSERT_TEAMS extends BaseJavaMigration {

    private static final Logger log = LoggerFactory.getLogger(V4__INSERT_TEAMS.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String BAYERN_TEAM_ID;
    private static String PLAYER_DATA_URL;
    private HttpClient httpClient;

    static {
        BAYERN_TEAM_ID = "5";
    }

    public V4__INSERT_TEAMS() throws IOException {
        this.httpClient = HttpClient.newBuilder().build();
        if (PLAYER_DATA_URL == null || PLAYER_DATA_URL.equals("")){
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(Files.readAllBytes(Path.of("target/classes/application.properties"))));
            PLAYER_DATA_URL = properties.getProperty("application.teamUrl");
        }
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void migrate(Context context) throws Exception {
        log.info("entering migrate method of V4__INSERT_TEAMS");
        Connection con = context.getConnection();
        con.setAutoCommit(false);

        Team foundTeam = fetchTeamByIdFromApiAndStoreInDb(BAYERN_TEAM_ID, con);
        Team foundTeamWithLogo = updateLogoOfFOundTeam(foundTeam, con);
//        Files.write(Path.of("foundTeam.json"), foundTeam.toString().getBytes());
        List<Player> playerList = fetchAndPersistPlayersOfTeamFromApi(foundTeamWithLogo, con);
        List<Integer> playerContractIds = addPLayersToTeam(playerList, foundTeamWithLogo, con);
//        log.info("{} players signed by {}", playerContractIds.size(), foundTeamWithLogo.getName());
        //con.close(); DO NOT CLOSE THE CONNECTION, SINCE FLYWAY NEEDS IT FOR FURTHER MIGRATIONS!!!

    }

    private Team fetchTeamByIdFromApiAndStoreInDb(String id, Connection con) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(PLAYER_DATA_URL + "/" + id))
                .header("X-Auth-Token", "d912ec84ca93444b98587cd9f6809d04")
                .GET()
                .build();

        //HttpResponse<String> resp = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String playerDataJson = new String(Files.readAllBytes(Path.of("target/classes/REST-API.json")));
        Team bayernMuenchen = new Team();
//        if (resp.statusCode() == 200) {
            bayernMuenchen = objectMapper.readValue(playerDataJson, Team.class);

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO TEAMS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?)")) {
                ps.setInt(1, bayernMuenchen.getId());
                ps.setString(2, bayernMuenchen.getName());
                ps.setInt(3, bayernMuenchen.getBudget());
                ps.setBytes(4, "test".getBytes());
                ps.setString(5, bayernMuenchen.getOwner());
                ps.setString(6, bayernMuenchen.getTla());
                ps.setString(7, String.valueOf(bayernMuenchen.getAddress()));
                ps.setString(8, String.valueOf(bayernMuenchen.getPhone()));
                ps.setString(9, String.valueOf(bayernMuenchen.getWebsite()));
                ps.setString(10, String.valueOf(bayernMuenchen.getEmail()));
                ps.setString(11, String.valueOf(bayernMuenchen.getVenue()));

                ps.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
//        } else if (resp.statusCode() >= 400) {
//            //do nothing
//        }
        return bayernMuenchen;
    }

    private Team updateLogoOfFOundTeam(Team foundTeam, Connection con) throws SQLException, IOException {
        try (PreparedStatement update = con.prepareStatement("UPDATE TEAMS SET LOGO = ? WHERE ID = ?")) {
            Files.list(Path.of("./target/classes/images"))
                    .sorted(comparing(p -> p.getFileName().toString()))
                    .forEach(p -> {
                        log.debug("found file: {}", p.toString());
                        try {
                            String fileName = p.getFileName().toString();
                            String teamName = fileName.substring(0, fileName.indexOf("."));
                            if (teamName.equals(foundTeam.getTla())){
                                byte[] logo = Files.readAllBytes(p);
                                try (ByteArrayInputStream bin = new ByteArrayInputStream(logo)) {
                                    update.setBinaryStream(1, bin, logo.length);
                                    update.setInt(2, foundTeam.getId());
                                    update.execute();
                                    foundTeam.setLogo(logo);
                                }
                            }
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
            con.commit();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return foundTeam;
    }

    private List<Player> fetchAndPersistPlayersOfTeamFromApi(Team fromTeam, Connection con) throws IOException, SQLException {
        Player[] playersFromBayern = objectMapper.treeToValue(fromTeam.getSquad(), Player[].class);
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO PLAYERS VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
//                ps.setString(1, String.valueOf(bayernMuenchen.getId()));
            for (Player elem : playersFromBayern){
                ps.setInt(1, elem.getId());
                ps.setString(2, elem.getName());
                ps.setInt(3, elem.getAlter());
                ps.setString(4, elem.getPosition());
                ps.setDate(5, Date.valueOf(elem.getDateOfBirth()));
                ps.setString(6, elem.getCountryOfBirth().getValue());

                ps.execute();
                con.commit();
            }
        }
        return Arrays.asList(playersFromBayern);
    }

    private List<Integer> addPLayersToTeam(List<Player> players, Team toTeam, Connection con) throws SQLException {
        List<Integer> contractIds = new ArrayList<>(players.size());
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO under_contract values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            for (Player player : players){
                ps.setInt(1, toTeam.getId());
                ps.setInt(2, player.getId());
                int playerContractId = ps.executeUpdate();
                contractIds.add(playerContractId);
                con.commit();
            }
        }
        return contractIds;
    }
}
