package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.logging.ApacheLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.storage.chunkdb.AsyncChunkDBManager;
import de.bentzin.ingwer.storage.chunkdb.ChunkDB;
import de.bentzin.ingwer.storage.chunkdb.SyncedChunkDBManager;
import de.bentzin.ingwer.utils.StopCode;
import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class IngwerPlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        super.onEnable();

        Ingwer.start(new Preferences(Identity.DEVELOPER_UUID, '+', StartType.JAVA_PLUGIN_STANDALONE,
                ChunkDB.getProvider(AsyncChunkDBManager.getDefault()),
                new ApacheLogger("Ingwer", LogManager.getRootLogger()), this, false));
    }

    @Override
    public void onDisable() {
        Ingwer.stop(StopCode.SHUTDOWN);
    }
}
