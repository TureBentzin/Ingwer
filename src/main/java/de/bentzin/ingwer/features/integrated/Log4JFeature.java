package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.PatternedMiniMessageMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.plugins.tiff.TIFFTagSet;

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
        return false;
    }

    public Logger getRootLogger4J() {
        return rootLogger4J;
    }

    public class Mute4JCommand extends IngwerCommand {

        /**
         * false = logging is on
         * true = logging is disabled
         */
        private boolean status = false;

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
            MessageBuilder builder = MessageBuilder.prefixed().add(C.C,"Log4J was ").add(C.A,"{0}").add(C.C,"!");
            PatternedMiniMessageMessage patternedMiniMessageMessage = builder.toCompletableMessage();
            BooleanUtils.flip(status,() -> {
                patternedMiniMessageMessage.insert(0,"disabled");
                patternedMiniMessageMessage.get().send(commandSender);
            },() -> {
                patternedMiniMessageMessage.insert(0,"enabled");
                patternedMiniMessageMessage.get().send(commandSender);
            });
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.INGAME};
        }
    }
}
