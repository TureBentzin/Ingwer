package de.bentzin.ingwer.message;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.utils.LoggingClass;

public class IngwerMessageManager extends LoggingClass {

    public static IngwerMessageManager getInstance() {
        return Ingwer.getMessageManager();
    }

    public IngwerMessageManager() {
        super(Ingwer.getLogger().adopt("MSG"));
    }
}
