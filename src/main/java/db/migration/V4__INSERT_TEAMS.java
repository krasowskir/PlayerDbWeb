package db.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.richard.home.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static java.util.Comparator.comparing;

public class V4__INSERT_TEAMS extends BaseJavaMigration {

    private static final Logger log = LoggerFactory.getLogger(V4__INSERT_TEAMS.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String BAYERN_TEAM_ID;
    private static String PLAYER_DATA_URL;
    private static int indx = 0;

    static {
        BAYERN_TEAM_ID = "5";
    }

    public V4__INSERT_TEAMS() throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(Files.readAllBytes(Path.of("target/classes/application.properties"))));
        if (PLAYER_DATA_URL.equals("")){
            PLAYER_DATA_URL = properties.getProperty("application.teamUrl");
        }
    }



    @Override
    public void migrate(Context context) throws Exception {
        log.info("entering migrate method of V4__INSERT_TEAMS");
        Connection con = context.getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement update = con.prepareStatement("UPDATE TEAMS SET LOGO = ? WHERE ID = ?")) {
            Files.list(Path.of("./target/classes/images"))
                    .sorted(comparing(p -> p.getFileName().toString()))
                    .forEach(p -> {
                        log.debug("found file: {}", p.toString());
                        try {
                            byte[] logo = Files.readAllBytes(p);
                            try (ByteArrayInputStream bin = new ByteArrayInputStream(logo)) {
                                update.setBinaryStream(1, bin, logo.length);
                                update.setInt(2, ++indx);
                                update.execute();
                            }
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
            con.commit();
        }
        fetchTeamByIdFromApi(BAYERN_TEAM_ID, con);
        //con.close(); DO NOT CLOSE THE CONNECTION, SINCE FLYWAY NEEDS IT FOR FURTHER MIGRATIONS!!!

    }

    public Team fetchTeamByIdFromApi(String id, Connection con) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(new URI(BAYERN_TEAM_ID + "/" + id))
                .header("X-Auth-Token", "d912ec84ca93444b98587cd9f6809d04")
                .GET()
                .build();
        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Team bayernMuenchen = null;
        if (resp.statusCode() == 200) {
            bayernMuenchen = objectMapper.readValue(resp.body(), Team.class);

            try (PreparedStatement ps = con.prepareStatement("UPDATE TEAMS SET TLA = ?, OWNER = ?, ADDRESS = ?, PHONE = ?, WEBSITE = ?, EMAIL = ?, VENUE = ? WHERE ID = 1")) {
//                ps.setString(1, String.valueOf(bayernMuenchen.getId()));
                ps.setString(1, bayernMuenchen.getTla());
                ps.setString(2, bayernMuenchen.getOwner());
                ps.setString(3, String.valueOf(bayernMuenchen.getAddress()));
                ps.setString(4, String.valueOf(bayernMuenchen.getPhone()));
                ps.setString(5, String.valueOf(bayernMuenchen.getWebsite()));
                ps.setString(6, String.valueOf(bayernMuenchen.getEmail()));
                ps.setString(7, String.valueOf(bayernMuenchen.getVenue()));

                ps.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (resp.statusCode() >= 400) {
            //do nothing
        }
        return bayernMuenchen;
    }
}
