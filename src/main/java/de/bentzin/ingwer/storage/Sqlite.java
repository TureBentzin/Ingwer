package de.bentzin.ingwer.storage;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Sqlite {

    public Sqlite() throws URISyntaxException, IOException, SQLException {
        logger = Ingwer.getLogger().adopt("Storage");
        db = getDefaultFile();

        //INIT

        init();
        connect();
        setupDB();

    }


    public  Logger logger;

    public static @NotNull File getDefaultFile() throws URISyntaxException {
        File jar;

        jar = new File(Ingwer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File parent = jar.getParentFile();
        File db;
        db = new File(parent,"data.sqlite");
        return db;
    }



    private  File db;


    private  Connection connection;


    public  void init() throws URISyntaxException, IOException {

        logger.debug("database File: " + db);
        db.createNewFile();

    }

    public  boolean isConnected(){
        try {
            return  !connection.isClosed();
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return false;
    }

    private  void setupDB() throws SQLException {
        if(isConnected()) {

            connection.createStatement().execute("create table if not exists identity\n" +
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
                IngwerThrower.acceptS(e, ThrowType.STORAGE);
            }

        }
    }

    public  void connect() throws SQLException {
        logger.debug("establishing connection to: " + db.getName());
        connection = DriverManager.getConnection("jdbc:sqlite:" + db.getPath());

    }

    public  void close(){
        if(connection != null)
        try {
            connection.close();
        } catch (SQLException e) {
            IngwerThrower.acceptS(e);
        }
    }

    public static void main(String[] args) {

        Ingwer.start(new Preferences(Identity.DEVELOPER_UUID,
                '+',StartType.DEBUG_STANDALONE,
                new File("E:\\WorkSpace\\Ich versuche es nochmal\\Ich versuche es nochmal\\WORKSPACE 2020\\Ingwer\\out\\artifacts\\ingwer_storage_jar\\data.sqlite"),
                new SystemLogger("Ingwer")
                ));

        Sqlite storage = Ingwer.getStorage();
        try {

            storage.init();
            storage.connect();
            storage.setupDB();

            //HARDCODE

            Identity identity = new Identity("TEST", null, new IngwerPermissions(IngwerPermission.USE));
            storage.saveIdentity(identity);

            //<HARDCODE
        } catch (URISyntaxException | IOException e) {
            IngwerThrower.acceptS(e);
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
    }


    public  File getDb() {
        return db;
    }

    public void setDb(File db) {
        this.db = db;
    }


    public  Identity saveIdentity(@NotNull Identity identity) {
        try {
            Statement statement = connection.createStatement();

            statement.execute(
                    "INSERT INTO identity (user_name, player_uuid, user_permissions)" +
                    "VALUES (" + a(identity.getName()) + "," + a(identity.getUUID())
                            + "," + a(identity.getCodedPermissions()) + ")");



        } catch (SQLException e) {
            IngwerThrower.acceptS(e,ThrowType.STORAGE);
        }

        return identity;
    }

    /**
     *
     * @param s
     * @return s but with "'"
     */
    @Contract(pure = true)
    private @NotNull <S> String a(S s) {
        return "'" + s + "'";
    }

}
