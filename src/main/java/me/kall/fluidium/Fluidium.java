package me.kall.fluidium;

import com.google.common.base.Suppliers;
import me.kall.fluidium.common.api.IFluid;
import me.kall.fluidium.common.config.FluidiumConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(Fluidium.MOD_ID)
public final class Fluidium {
    public static final String MOD_ID = "fluidium";

    private static final Supplier<Set<ResourceLocation>> BLACKLIST = Suppliers.memoize(() -> FluidiumConfig.BLACKLIST.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet()));
    private static final Supplier<Set<String>> MOD_ID_LIST = Suppliers.memoize(() -> new HashSet<>(FluidiumConfig.MOD_ID_LIST.get()));

    public Fluidium() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FluidiumConfig.INSTANCE);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> updateFluidTickable(true)));
        modBus.addListener((ModConfigEvent.Reloading event) -> updateFluidTickable(event.getConfig().getModId().equals(MOD_ID)));
    }

    private static void updateFluidTickable(boolean update) {
        if (update) {
            ForgeRegistries.FLUIDS.getEntries().forEach(entry -> {
                ResourceLocation id = entry.getKey().location();
                Fluid fluid = entry.getValue();
                boolean alwaysTick = BLACKLIST.get().contains(id) || MOD_ID_LIST.get().contains(id.getNamespace());
                ((IFluid) fluid).fluidium$setShouldAlwaysTick(alwaysTick);
            });
        }
    }

    public static boolean shouldOptimize(@NotNull List<ServerPlayer> players, BlockPos pos, int optDist) {
        double thresholdSq = optDist * optDist;
        for (ServerPlayer player : players) {
            double dx = player.getX() - pos.getX();
            double dy = player.getY() - pos.getY();
            double dz = player.getZ() - pos.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq <= thresholdSq) {
                return false;
            }
        }
        return true;
    }

    public static boolean isChunkForced(@NotNull BlockPos pos, @NotNull ServerLevel level) {
        long chunkPos = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        return level.getForcedChunks().contains(chunkPos);
    }

    public static boolean isFluidAlwaysTick(Fluid fluid) {
        return ((IFluid)fluid).fluidium$shouldAlwaysTick();
    }
}