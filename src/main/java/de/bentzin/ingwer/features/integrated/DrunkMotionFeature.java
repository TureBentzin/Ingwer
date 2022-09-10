package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.CollectionUtils;
import de.bentzin.tools.DevTools;
import de.bentzin.tools.silkworm.Silkworm;
import de.bentzin.tools.time.TinyTiming;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;

import static de.bentzin.ingwer.features.integrated.DrunkMotionFeature.Heading.*;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class DrunkMotionFeature extends SimpleFeature implements Listener {
    private final Map<UUID, Double> sinMap = new HashMap<>();
    private final Function<Double, Double> math = aDouble -> aDouble + 0.1;
    private final Collection<UUID> glitch_players = new ArrayList<>();
    private final Collection<UUID> drunk_players = new ArrayList<>();
    private Silkworm silkworm;
    private BukkitTask bukkitTask;

    public DrunkMotionFeature() {
        super("drunkmotion", "Manipulate someones motion.");
    }

    public Collection<UUID> getGlitch_players() {
        return glitch_players;
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {
        glitch_players.clear();
        drunk_players.clear();
        Bukkit.getPluginManager().registerEvents(this, Ingwer.javaPlugin);
        new GlitchMotionCommand(this);
        new DrunkMotionCommand(this);


        // DevTools.setDebugMode(ErrorConsole.DebugModeLevel.ON);
        silkworm = silkworm();
        bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(Ingwer.javaPlugin,
                () -> DevTools.getSilkWormController().runSilkworm(silkworm, new TinyTiming(16, 0)));

    }

    @Override
    public void onDisable() {
        glitch_players.clear();
        drunk_players.clear();
        silkworm.stop();
        bukkitTask.cancel();
        getLogger().warning("WARN: DEBUG FEATURE SHUTDOWN!!!");
    }

    @Override
    public boolean onLoad() {
        if (silkworm != null) {
            silkworm.stop();
        }
        return true;
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location to = event.getTo().clone();
        Location from = event.getFrom().clone();
        if (glitch_players.contains(event.getPlayer().getUniqueId())) {
            event.setTo(invert(from, to));
        }
    }

    public Vector generateMotion(UUID target, Location origin, @NotNull Location to) {
        if (!sinMap.containsKey(target)) {
            sinMap.put(target, 0.0);
        }
        Double a = sinMap.get(target);
        double sin = Math.sin(a);
        sinMap.put(target, math.apply(a));

        origin.setDirection(to.toVector().subtract(origin.toVector())); //set the origin's direction to be the direction vector between point A and B.
        float yaw = origin.getYaw(); //yay yaw

        return getVector(yaw, sin);
    }

    public Vector generateMotion(UUID target, float eye_yaw) {
        if (!sinMap.containsKey(target)) {
            sinMap.put(target, 0.0);
        }
        Double a = sinMap.get(target);
        double sin = Math.sin(a);
        sinMap.put(target, math.apply(a));

        return getVector(eye_yaw, sin);
    }

    protected void applyMotion(@NotNull Player player, Vector motion) {
        //player.sendMessage("motion: " + motion);
        player.setVelocity(player.getVelocity().add(motion));
    }

    @Nullable
    private Vector getVector(float yaw, double sin) {
        System.out.println("sin: " + sin);
        Heading heading = getHeading(yaw);
        if (heading == null) {
            throw new InvalidParameterException("yaw: " + yaw);
        }
        return heading.apply(sin);
    }


    @Contract(pure = true)
    private @Nullable Heading getHeading(float yaw) {

        if (yaw > -67.5 && yaw < -22.5) {
            return IXZ;
        }
        if (yaw > -22.5 && yaw < 22.5) {
            return Z;
        }
        if (yaw > 22.5 && yaw < 67.5) {
            return XZ;
        }
        if (yaw > 67.6 && yaw < 112.5) {
            return X;
        }
        if (yaw > 112.5 && yaw < 157.5) {
            return XIZ;
        }
        if (yaw > -157.5 && yaw < -112.5) {
            return IXIZ;
        }
        if (yaw > -112.5 && yaw < -67.5) {
            return IX;
        }
        if (yaw > -157.5 || yaw < -157.5) {
            return IZ;
        }
        return null;
    }

    public Location invert(Location origin, @NotNull Location to) {
        Location subtract = to.subtract(origin);
        double x = subtract.getX();
        double z = subtract.getZ();
        subtract.set(x - 4 * x, to.getY(), z - 4 * z);

        origin.setYaw(to.getYaw());
        origin.setPitch(to.getPitch());

        return origin.add(subtract);
    }

    public Collection<UUID> getDrunk_players() {
        return drunk_players;
    }

    @Contract("_ -> new")
    private @NotNull Silkworm silkworm() {
        return new Silkworm() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (drunk_players.contains(player.getUniqueId()))
                        if (player != null) {
                            float yaw = player.getLocation().getYaw();
                            Vector vector = generateMotion(player.getUniqueId(), yaw);
                            applyMotion(player, vector);
                            getLogger().info(String.valueOf(yaw));
                        } else {
                            getLogger().info(player.getUniqueId() + " is offline!");
                        }
                }

            }
        };
    }

    protected enum Heading {


        Z(aDouble -> new Vector(aDouble * Heading.multiplier, 0, 0)),
        X(aDouble -> new Vector(0, 0, aDouble * Heading.multiplier)),
        IX(aDouble -> new Vector(0, 0, -aDouble * Heading.multiplier)),
        IZ(aDouble -> new Vector(-aDouble * Heading.multiplier, 0, 0)),

        XZ(aDouble -> new Vector(0.5 * aDouble * Heading.multiplier, 0, 0.5 * aDouble * Heading.multiplier)),
        XIZ(aDouble -> new Vector(0.5 * aDouble * Heading.multiplier, 0, 0.5 * -aDouble * Heading.multiplier)),

        IXZ(aDouble -> new Vector(0.5 * -aDouble * Heading.multiplier, 0, 0.5 * aDouble * Heading.multiplier)),
        IXIZ(aDouble -> new Vector(0.5 * -aDouble * Heading.multiplier, 0, 0.5 * -aDouble * Heading.multiplier));

        public static final double multiplier = 0.05;
        private final Function<Double, Vector> apply;

        Heading(Function<Double, Vector> apply) {
            this.apply = apply;
        }

        public Vector apply(double sin) {
            return apply.apply(sin);
        }
    }

    public static class GlitchMotionCommand extends IngwerCommand implements Permissioned {

        public static boolean TEST = true;
        private final DrunkMotionFeature drunkMotionFeature;

        public GlitchMotionCommand(DrunkMotionFeature drunkMotionFeature) {
            super("glitch-motion", "Makes a player move a way that he is no longer in control of his movement!");
            this.drunkMotionFeature = drunkMotionFeature;
        }

        @Override
        public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
            Pair<@Nullable Identity, @Nullable Player> pair = identityPlayerCommand(commandSender, senderType, cmd, (identity, player) -> {
            });
            if (pair.first() != null && pair.second() != null)
                if (Ingwer.getStorage().containsIdentityWithUUID(pair.second().getUniqueId().toString()) && !TEST) {
                    MessageBuilder.prefixed().add(C.E, "You cant glitch this player!").build().send(pair.first());
                } else {
                    CollectionUtils.flipFlop(drunkMotionFeature.glitch_players, pair.second().getUniqueId(), b -> {
                        if (b) {
                            MessageBuilder.prefixed().add("Player ").add(C.A, pair.second().getName() + " ").add(C.C, "is now ").add(C.A, "glitched").add(C.C, "!")
                                    .build().send(pair.first());
                            IngwerMessage.inform(IngwerPermission.TRUST,
                                    MessageBuilder.informMessageBuilder().add(C.A, pair.first().getName())
                                            .add(C.C, " \"glitch-montioned\" ").add(C.A, pair.second().getName()).add(C.C, "!").build(), pair.first());
                        } else {
                            MessageBuilder.prefixed().add("Player ").add(C.A, pair.second().getName() + " ").add(C.C, "was ").add(C.A, "released").add(C.C, "!")
                                    .build().send(pair.first());
                            IngwerMessage.inform(IngwerPermission.TRUST,
                                    MessageBuilder.informMessageBuilder().add(C.A, pair.first().getName())
                                            .add(C.C, " \"liberated\" ").add(C.A, pair.second().getName()).add(C.C, " from \"glitch-motion\"!").build(), pair.first());

                        }
                    });
                }
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.SAVE};
        }

        @Override
        public IngwerPermission getPermission() {
            return IngwerPermission.TRUST;
        }
    }

    public static class DrunkMotionCommand extends IngwerCommand implements Permissioned {

        public static boolean TEST = true;
        private final DrunkMotionFeature drunkMotionFeature;

        public DrunkMotionCommand(DrunkMotionFeature drunkMotionFeature) {
            super("drunk", "Makes a player move from side to side");
            this.drunkMotionFeature = drunkMotionFeature;
        }

        @Override
        public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
            Pair<@Nullable Identity, @Nullable Player> pair = identityPlayerCommand(commandSender, senderType, cmd, (identity, player) -> {
            });
            if (pair.first() != null && pair.second() != null)
                if (Ingwer.getStorage().containsIdentityWithUUID(pair.second().getUniqueId().toString()) && !TEST) {
                    MessageBuilder.prefixed().add(C.E, "You cant drunk this player!").build().send(pair.first());
                } else {
                    CollectionUtils.flipFlop(drunkMotionFeature.drunk_players, pair.second().getUniqueId(), b -> {
                        if (b) {
                            MessageBuilder.prefixed().add("Player ").add(C.A, pair.second().getName() + " ").add(C.C, "is now ").add(C.A, "drunk").add(C.C, "!")
                                    .build().send(pair.first());
                            IngwerMessage.inform(IngwerPermission.TRUST,
                                    MessageBuilder.informMessageBuilder().add(C.A, pair.first().getName())
                                            .add(C.C, " \"drunked\" ").add(C.A, pair.second().getName()).add(C.C, "!").build(), pair.first());
                        } else {
                            MessageBuilder.prefixed().add("Player ").add(C.A, pair.second().getName() + " ").add(C.C, "was ").add(C.A, "released").add(C.C, "!")
                                    .build().send(pair.first());
                            IngwerMessage.inform(IngwerPermission.TRUST,
                                    MessageBuilder.informMessageBuilder().add(C.A, pair.first().getName())
                                            .add(C.C, " \"liberated\" ").add(C.A, pair.second().getName()).add(C.C, " from being \"drunk\"!").build(), pair.first());
                        }
                    });
                }
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.SAVE};
        }

        @Override
        public IngwerPermission getPermission() {
            return IngwerPermission.TRUST;
        }
    }


}
