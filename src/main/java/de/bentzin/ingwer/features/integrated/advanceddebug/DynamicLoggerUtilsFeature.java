package de.bentzin.ingwer.features.integrated.advanceddebug;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.EnumNameNode;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.logging.*;
import de.bentzin.ingwer.logging.dynamic.DynamicLoggerContainer;
import de.bentzin.ingwer.message.FramedMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.thrower.IngwerException;
import de.bentzin.ingwer.thrower.ThrowType;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */
public class DynamicLoggerUtilsFeature extends SimpleFeature {
    public DynamicLoggerUtilsFeature() {
        super("DynamicLoggerUtils","This Feature is active in the case that a DynamicLoggerContainer is used for Ingwer Logging.");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.ADMIN;
    }

    @Override
    public void onEnable() {
        getLogger().info(getClass().getSimpleName() + " enabled because of a DynamicLoggerContainer is set in Ingwers Main Logger");
        new SwitchLoggerCommand();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return Ingwer.getLogger() instanceof DynamicLoggerContainer;
    }


    public static class SwitchLoggerCommand extends IngwerNodeCommand {
        public static final String COMMAND_DESCRIPTION = "This command provides you with a debug and test option to switch between some pre-defined loggers for Ingwer!";
        public SwitchLoggerCommand() {
            super(CommandTarget.SAVE.fullfill(), "switchLogger", COMMAND_DESCRIPTION,
                    (data, nodeTrace) -> {
                        de.bentzin.ingwer.logging.@NotNull Logger main = Ingwer.getLogger();

                        ArrayList<OneLinedMessage> oneLinedMessages = new ArrayList<>();
                        oneLinedMessages.add(MessageBuilder.empty().add(COMMAND_DESCRIPTION).build());
                        oneLinedMessages.add(MessageBuilder.empty().add(C.C, "Current Logger:")
                                .add(C.A, main.getName() + " ").add(C.C, "Instance: ").add(C.A, main.getClass().getSimpleName()).build());
                        oneLinedMessages.add(MessageBuilder.empty().add(C.C, "debug: ").add(C.A, (main.isDebugEnabled()) ? "<green>enabled" : "<red>disabled").build());
                        new FramedMessage(oneLinedMessages).send(data.commandSender());
                    });
                getCommandNode().append(new EnumNameNode<>("loggerType",Logger.class) {
                    @Override
                    public void execute(CommandData commandData, NodeTrace nodeTrace, Logger logger) throws NodeTrace.NodeParser.NodeParserException {
                        try {
                            logger.applyDefault((DynamicLoggerContainer) Ingwer.getLogger());
                        }catch (ClassCastException e) {
                            throw new IngwerException("Seems like Ingwer is not using a DynamicLoggerContainer!", e, ThrowType.LOGGING);
                        }

                        MessageBuilder.prefixed().add("Switched Logger to: ").add(C.A, logger.name()).build().send(commandData.commandSender());
                    }
                }).finish();

        }

        public enum Logger{
            /**
             * [0] - name
             */
            APACHE(objects -> new ApacheLogger((String) objects[0], LogManager.getRootLogger()), () ->  new ApacheLogger("Ingwer", LogManager.getRootLogger())),
            /**
             * [0] - name
             */
            SYSTEM(objects -> new SystemLogger((String) objects[0]), () -> new SystemLogger("Ingwer")),
            /**
             * [0] - name
             */
            CONSOLE(objects -> new ConsoleCommandSenderLogger((String) objects[0]), () -> new ConsoleCommandSenderLogger("Ingwer")),
            /**
             * [0] - name
             * [1] - Supplier of CommandSender[]
             */
            COMMAND_SENDER(objects -> {
                return new CommandSenderLogger((String) objects[0], (Supplier<CommandSender[]>) objects[1]);
            }, () -> {
                return new CommandSenderLogger("Ingwer", Bukkit.getOnlinePlayers().toArray(new CommandSender[0]));
            })
            ;
            private final Function<Object[],de.bentzin.ingwer.logging.Logger> loggerFactory;
            private final Supplier<de.bentzin.ingwer.logging.Logger> defaultLogger;

            //Definition on what the objects are is given in the fields - 0 is always a name
            public void apply(@NotNull DynamicLoggerContainer container, Object... args){
                try {
                    container.setHeart(loggerFactory.apply(args));
                }catch (Exception e){
                    throw new InvalidParameterException("seems like the objects[] are malformed - please check documentation of the Logger your using! :: " + e.getMessage());
                }
            }

            public void applyDefault(@NotNull DynamicLoggerContainer container) {
                try {
                container.setHeart(defaultLogger.get());
                }catch (Exception e){
                    throw new RuntimeException("seems like there was an issue with generating a default logger - please check documentation of the Logger your using! :: " + e.getMessage(), e);
                }
            }

            public void apply(@NotNull DynamicLoggerContainer container){
                try {
                    container.setHeart(loggerFactory.apply(new Object[]{"Ingwer"}));
                }catch (Exception e){
                    throw new InvalidParameterException("seems like the objects[] is not set - please check documentation of the Logger your using! :: " + e.getMessage());
                }
            }

            Logger(de.bentzin.ingwer.logging.Logger logger, Supplier<de.bentzin.ingwer.logging.Logger> defaultLogger) {
                this.loggerFactory = (objects) -> logger;
                this.defaultLogger = defaultLogger;
            }

            Logger(Supplier<de.bentzin.ingwer.logging.Logger> loggerSupplier, Supplier<de.bentzin.ingwer.logging.Logger> defaultLogger) {
                this.loggerFactory = objects -> loggerSupplier.get();
                this.defaultLogger = defaultLogger;
            }
            Logger(Function<Object[], de.bentzin.ingwer.logging.Logger> loggerFactory, Supplier<de.bentzin.ingwer.logging.Logger> defaultLogger) {
                this.loggerFactory = loggerFactory;
                this.defaultLogger = defaultLogger;
            }
        }
    }
}
