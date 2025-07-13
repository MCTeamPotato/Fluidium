package me.kall.fluidium.common.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.kall.fluidium.common.kdtree.KDTree;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerRegionCache {
    private final Map<UUID, KDTree.PlayerRegion> playerRegionMap = new Object2ObjectOpenHashMap<>();
    private final Map<Level, KDTree> dimensionCache = new Object2ObjectOpenHashMap<>();
    private double radius = 32.0;//TODO: configurable
    private long lastUpdateTick = 0;

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void update() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        long currentTick = server.getTickCount();
        if (currentTick == lastUpdateTick) return;

        lastUpdateTick = currentTick;

        dimensionCache.clear();
        Set<UUID> activePlayers = new ObjectOpenHashSet<>();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerLevel level = player.serverLevel();

            UUID playerId = player.getUUID();
            activePlayers.add(playerId);

            KDTree.PlayerRegion region = playerRegionMap.computeIfAbsent(playerId, id -> new KDTree.PlayerRegion(id, player.getX(), player.getY(), player.getZ(), radius));

            if (region.centerX() != player.getX() || region.centerY() != player.getY() || region.centerZ() != player.getZ()) {
                playerRegionMap.put(playerId, new KDTree.PlayerRegion(playerId, player.getX(), player.getY(), player.getZ(), radius));
            }

            KDTree tree = dimensionCache.computeIfAbsent(level, k -> new KDTree());
            tree.insert(playerRegionMap.get(playerId));
        }

        playerRegionMap.keySet().removeIf(id -> !activePlayers.contains(id));
    }

    public boolean isNearPlayer(@NotNull Level level, double x, double y, double z) {
        if (level.isClientSide) return false;

        KDTree tree = dimensionCache.get(level);
        if (tree == null || tree.size() == 0) return false;

        return tree.containsAnyInRange(x, y, z, radius);
    }
}