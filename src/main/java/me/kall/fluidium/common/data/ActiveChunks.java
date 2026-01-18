package me.kall.fluidium.common.data;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.kall.fluidium.Fluidium;
import me.kall.fluidium.common.config.FluidiumConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Fluidium.MOD_ID)
public class ActiveChunks {
    public static final Object2ObjectMap<ResourceLocation, Long2ObjectMap<Range>> ACTIVE_CHUNKS = new Object2ObjectOpenHashMap<>();
    public static final Set<ResourceLocation> UPDATE_REQUIRED = new ObjectOpenHashSet<>();

    @SubscribeEvent
    public static void login(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)event.getEntity() ;
            player.server.execute(() -> UPDATE_REQUIRED.add(player.level.dimension().location()));
        }
    }

    @SubscribeEvent
    public static void dimChange(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)event.getEntity() ;
            player.server.execute(() -> UPDATE_REQUIRED.add(event.getTo().location()));
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)event.getEntity() ;
            player.server.execute(() -> UPDATE_REQUIRED.add(player.level.dimension().location()));
        }
    }

    @SubscribeEvent
    public static void tickLevel(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side != LogicalSide.SERVER) return;
        Level level = event.world;
        ResourceLocation dimID = level.dimension().location();

        if (!UPDATE_REQUIRED.remove(dimID)) return;

        Long2ObjectMap<Range> chunkMap = ACTIVE_CHUNKS.computeIfAbsent(dimID, k -> new Long2ObjectOpenHashMap<>());
        chunkMap.clear();

        int horizontal = FluidiumConfig.HORIZONTAL_CHUNK_RADIUS;
        int height = FluidiumConfig.VERTICAL_CHUNK_RADIUS * 16;

        for (Player player : level.players()) {
            int chunkX = player.blockPosition().getX() >> 4;
            int chunkZ = player.blockPosition().getZ() >> 4;
            int y = player.blockPosition().getY();
            int minY = y - height;
            int maxY = y + height;

            for (int dx = -horizontal; dx <= horizontal; dx++) {
                for (int dz = -horizontal; dz <= horizontal; dz++) {
                    long chunk = ChunkPos.asLong(chunkX + dx, chunkZ + dz);

                    Range yRange = chunkMap.get(chunk);
                    if (yRange == null) {
                        yRange = new Range(minY, maxY);
                        chunkMap.put(chunk, yRange);
                    } else {
                        yRange.min = Math.min(yRange.min, minY);
                        yRange.max = Math.max(yRange.max, maxY);
                    }
                }
            }
        }
    }

    public static boolean include(ResourceLocation dimID, int height, long chunk) {
        final Long2ObjectMap<Range> chunkMap = ACTIVE_CHUNKS.get(dimID);
        if (chunkMap == null) return false;
        final Range range = chunkMap.get(chunk);
        if (range == null) return false;
        return height >= range.min && height <= range.max;
    }

    public static final class Range {
        public int min;
        public int max;

        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}