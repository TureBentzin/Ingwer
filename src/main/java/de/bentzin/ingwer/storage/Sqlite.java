package de.bentzin.ingwer.storage;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import de.bentzin.ingwer.utils.LoggingClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class Sqlite extends LoggingClass implements Storage {


    @Contract(value = " -> new", pure = true)
    public static @NotNull StorageProvider<Sqlite> getProvider() {
        return new StorageProvider<>(false, false) {
            @Override
            @Nullable
            public Sqlite get() {
                try {
                    return new Sqlite();
                } catch (URISyntaxException | IOException | SQLException e) {
                    IngwerThrower.acceptS(e, ThrowType.STORAGE);
                }
                return null;
            }
        };
    }

    private File db;
    private Connection connection;


    public Sqlite() throws URISyntaxException, IOException, SQLException {
        super(Ingwer.getLogger().adopt("Storage"));
        db = getDefaultFile();

        //INIT

    }

    public static @NotNull File getDefaultFile() throws URISyntaxException {
        File jar;

        jar = new File(Ingwer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File parent = jar.getParentFile();
        File db;
        db = new File(parent, "data.sqlite");
        return db;
    }

    @Override
    public void init() {
        getLogger().debug("database File: " + db);
        try {
            db.createNewFile();
        } catch (IOException e) {
           IngwerThrower.acceptS(e,ThrowType.STORAGE);
        }

        try {
            connect();
            setupDB();
        } catch (SQLException e) {
            IngwerThrower.acceptS(e,ThrowType.STORAGE);
        }

    }

    public boolean isConnected() {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return false;
    }

    private void setupDB() throws SQLException {
        if (isConnected()) {

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

        } else {
            try {
                throw new IllegalStateException("Database needs to be connected!");
            } catch (Exception e) {
                IngwerThrower.acceptS(e, ThrowType.STORAGE);
            }

        }
    }

    public void connect() throws SQLException {
        getLogger().debug("establishing connection to: " + db.getName());
        connection = DriverManager.getConnection("jdbc:sqlite:" + db.getPath());

    }

    @Override
    public void close() {
        if (connection != null)
            try {
                connection.close();
                getLogger().warning("closed connection to database!");
            } catch (SQLException e) {
                getLogger().warning("suspicious behavior of database detected! Check your storage and the conditions Ingwer is getting executed under immediately!");
                IngwerThrower.acceptS(e);
            }
    }

    public File getDb() {
        return db;
    }

    public void setDb(File db) {
        this.db = db;
    }


    @Override
    public Identity saveIdentity(@NotNull Identity identity) {
        try {
            Statement statement = connection.createStatement();

            statement.execute(
                    "INSERT INTO identity (user_name, player_uuid, user_permissions)" +
                            "VALUES (" + a(identity.getName()) + "," + a(identity.getUUID())
                            + "," + a(identity.getCodedPermissions()) + ")");
            getLogger().info("saved: " + identity.getName());
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }

        return identity;
    }

    @Override
    public @Nullable Identity getIdentityByName(String name) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "SELECT * FROM identity WHERE user_name =" + a(name));
            ResultSet resultSet = statement.getResultSet();
            Identity identity = new Identity(resultSet.getString("user_name"),
                    UUID.fromString(resultSet.getString("player_uuid")),
                    IngwerPermission.decodePermissions(resultSet.getLong("user_permissions")));
            return identity;
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return null;
    }

    @Override
    public @Nullable Identity getIdentityByUUID(String uuid) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "SELECT * FROM identity WHERE player_uuid =" + a(uuid));
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isClosed()) {
                return null;
            }
            Identity identity = new Identity(resultSet.getString("user_name"),
                    UUID.fromString(resultSet.getString("player_uuid")),
                    IngwerPermission.decodePermissions(resultSet.getLong("user_permissions")));
            return identity;
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return null;
    }

    @Override
    public @Nullable Collection<Identity> getAllIdentities() {
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "SELECT * FROM identity");
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.isClosed()) {
                return null;
            }
            Collection<Identity> identities = new ArrayList<>();
            while (resultSet.next()) {
                identities.add(new Identity(resultSet.getString("user_name"),
                        UUID.fromString(resultSet.getString("player_uuid")),
                        IngwerPermission.decodePermissions(resultSet.getLong("user_permissions"))));
            }
            return identities;

        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return null;
    }

    @Override
    public @Nullable Identity getIdentityByID(int id) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "SELECT * FROM identity WHERE user_id = " + a(id));
            ResultSet resultSet = statement.getResultSet();
            Identity identity = new Identity(resultSet.getString("user_name"),
                    UUID.fromString(resultSet.getString("player_uuid")),
                    IngwerPermission.decodePermissions(resultSet.getLong("user_permissions")));
            return identity;
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return null;
    }

    @Override
    public void removeIdentity(@NotNull Identity identity) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "DELETE FROM identity WHERE user_name = " + a(identity.getName()));
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
    }

    @Override
    public boolean containsIdentityWithUUID(String uuid) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "SELECT * FROM identity WHERE player_uuid = " + a(uuid));
            ResultSet resultSet = statement.getResultSet();
            return resultSet != null && !resultSet.isClosed(); // resultSet != null
        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }
        return false;
    }

    @Override
    @Contract("_, _, _, _ -> param1")
    public Identity updateIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions) {

        String suuid = uuid.toString();
        try {
            Statement statement = connection.createStatement();

            //noinspection SqlResolve
            statement.execute(
                    "UPDATE identity\n" +
                            "SET user_name = " + a(name) + ", player_uuid = " + a(suuid) +
                            ", user_permissions = " + a(IngwerPermission.generatePermissions(ingwerPermissions)) +
                            "WHERE user_name = " + a(identity.getName()));

        } catch (SQLException e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
        }

        return getIdentityByUUID(suuid);
    }




    /**
     * @param s s
     * @return s but with "'"
     */
    @Contract(pure = true)
    private @NotNull <S> String a(S s) {
        return "'" + s + "'";
    }

}
