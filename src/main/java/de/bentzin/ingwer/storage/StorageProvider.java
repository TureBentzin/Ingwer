package de.bentzin.ingwer.storage;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 07.10.2022
 */
public abstract class StorageProvider<T extends Storage> implements Supplier<T> {
    private final boolean stealth;
    private final boolean debugOnly;

    protected StorageProvider(boolean stealth, boolean debugOnly) {
        this.stealth = stealth;
        this.debugOnly = debugOnly;
    }

    public boolean isStealth() {
        return stealth;
    }

    /**
     * @return Storage or null if something goes really wrong
     */
    @Nullable
    @Override
    public abstract T get();

    public boolean isDebugOnly() {
        return debugOnly;
    }

    public T getAndInit() throws IOException {
        T get = get();
        get.init();
        return get;
    }
}
