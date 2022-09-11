package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.PatternedMiniMessageMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.BooleanUtils;
import de.bentzin.ingwer.utils.Hardcode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.plugins.tiff.TIFFTagSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class Log4JFeature extends SimpleFeature {

    private final Logger rootLogger4J;
    private Mute4JCommand mute4JCommand;

    public Log4JFeature() {
        super("log4j", "make use of papers internal log4j installation!");
        rootLogger4J = (Logger) LogManager.getRootLogger();
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {
        mute4JCommand = new Mute4JCommand();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return true;
    }

    public Logger getRootLogger4J() {
        return rootLogger4J;
    }

    public class Mute4JCommand extends IngwerCommand {

        /**
         * false = logging is on
         * true = logging is disabled
         */
        private boolean status = true;

        /**
         *
         * @return
         * false = logging is on
         * true = logging is disabled
         *
         */
        public boolean getStatus() {
            return status;
        }

        public Mute4JCommand() {
            super("mute4j","Toggles Papers logging on or off");
        }

        @Override
        public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
            identityCommand(commandSender, senderType, identity -> {
            PatternedMiniMessageMessage message1 = MessageBuilder.prefixed().add(C.C,"Log4J was ").add(C.A,"{0}").add(C.C,"!").toCompletableMessage();
            PatternedMiniMessageMessage message2 = MessageBuilder.informMessageBuilder().add(C.A,commandSender.getName()).add(C.A," {0} ").add(C.C,"Log4J!").toCompletableMessage();
            status = BooleanUtils.flip(status,() -> {
                log4j(false);
                message1.insert(0,"disabled");
                message2.insert(0,"disabled");
                message1.get().send(commandSender);
            },() -> {
                log4j(true);
                message1.insert(0,"enabled");
                message2.insert(0,"enabled");
                message1.get().send(commandSender);
                IngwerMessage.inform(IngwerPermission.TRUST,message2.get(),identity);
            });
            });
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.INGAME};
        }

        private Map<String,Level> levelMap = new HashMap<>();

        /**
         * Benny
         * @param enabled
         */
        protected void log4j(boolean enabled) {
            if(enabled) {
                rootLogger4J.setLevel(Level.INFO);
                getLoggers().forEach(logger -> logger.setLevel(levelMap.getOrDefault(logger.getName(), Level.INFO)));
            }else {
                getLoggers().forEach(logger -> {
                    levelMap.put(logger.getName(),logger.getLevel());
                    getLogger().debug("saved logger: " + logger.getName() + "@" + logger.getLevel().name());
                    logger.setLevel(Level.OFF);
                });
                rootLogger4J.setLevel(Level.OFF);
            }
        }


        protected Collection<Logger> getLoggers() {
            LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
            return logContext.getLoggers();
        }

    }
}
