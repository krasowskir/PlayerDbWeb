package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.util.Comparator.comparing;

public class V4__INSERT_TEAMS extends BaseJavaMigration {

    private static final Logger log = LoggerFactory.getLogger(V4__INSERT_TEAMS.class);
    private static int indx = 0;

    @Override
    public void migrate(Context context) throws Exception {
        log.info("entering migrate method of V4__INSERT_TEAMS");
        try (PreparedStatement update = context.getConnection().prepareStatement("UPDATE TEAMS SET LOGO = ? WHERE ID = ?")){
            Files.list(Path.of("./target/classes/images"))
                    .sorted(comparing(p -> p.getFileName().toString()))
                    .forEach(p -> {
                log.debug("found file: {}", p.toString());
                try {
                    byte[] logo = Files.readAllBytes(p);
                    try (ByteArrayInputStream bin = new ByteArrayInputStream(logo)){
                        update.setBinaryStream(1, bin, logo.length);
                        update.setInt(2, ++indx);
                        update.execute();
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
