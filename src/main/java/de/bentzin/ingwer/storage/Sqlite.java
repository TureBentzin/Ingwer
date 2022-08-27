package de.bentzin.ingwer.storage;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.thow.IngwerException;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Sqlite {

    public static File getDefaultFile() throws URISyntaxException {
        File jar;

        jar = new File(Ingwer.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI());
        File parent = jar.getParentFile();
        db = new File(parent,"data.sqlite");
        return db;
    }

    private static File db;

    static {
        try {
            db = getDefaultFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection connection;


    public static void init() throws URISyntaxException, IOException {

        System.out.println("db = " + db);
        db.createNewFile();

    }

    public static boolean isConnected(){
        try {
            return  connection.isValid(0);
        } catch (SQLException e) {
            IngwerThrower.accept(e, ThrowType.STORAGE);
        }
        return false;
    }

    private static void setupDB() throws SQLException {
        if(isConnected()) {

            connection.createStatement().execute("create table if not exist identity\n" +
                    "(\n" +
                    "    user_id          integer     not null\n" +
                    "        constraint identity_pk\n" +
                    "            primary key autoincrement,\n" +
                    "    user_name        varchar(16) not null,\n" +
                    "    player_uuid      varchar(182),\n" +
                    "    user_permissions integer default 0 not null\n" +
                    ");\n" +
                    "\n" +
                    "create unique index identity_player_uuid_uindex\n" +
                    "    on identity (player_uuid);\n" +
                    "\n" +
                    "create unique index identity_user_id_uindex\n" +
                    "    on identity (user_id);\n" +
                    "\n" +
                    "create unique index identity_user_name_uindex\n" +
                    "    on identity (user_name);\n" +
                    "\n");

        }else {
            try {
                throw new IllegalStateException("Database needs to be connected!");
            }catch (Exception e) {
                IngwerThrower.accept(e, ThrowType.STORAGE);
            }

        }
    }

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + db.getPath());
    }

    public static void close(){
        if(connection != null)
        try {
            connection.close();
        } catch (SQLException e) {
            IngwerThrower.accept(e);
        }
    }

    public static void main(String[] args) {
        Ingwer.start(new Preferences(Identity.DEVELOPER_UUID,
                '+',StartType.DEBUG_STANDALONE,
                new File("E:\\WorkSpace\\Ich versuche es nochmal\\Ich versuche es nochmal\\WORKSPACE 2020\\Ingwer\\out\\artifacts\\ingwer_storage_jar\\data.sqlite")));

        try {
            init();
            connect();
            setupDB();
        } catch (URISyntaxException | IOException e) {
            IngwerThrower.accept(e);
        } catch (SQLException e) {
            IngwerThrower.accept(e, ThrowType.STORAGE);
        }
    }


    public static File getDb() {
        return db;
    }

    public static void setDb(File db) {
        Sqlite.db = db;
    }
}
