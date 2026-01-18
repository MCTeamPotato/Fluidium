package me.kall.fluidium;

import me.kall.duplicationless.util.Positions;
import me.kall.fluidium.common.api.IFluid;
import me.kall.fluidium.common.config.FluidiumConfig;
import me.kall.fluidium.common.data.ActiveChunks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod(Fluidium.MOD_ID)
public final class Fluidium {
    public static final String MOD_ID = "fluidium";

    public Fluidium(@NotNull IEventBus modBus, Dist dist, ModContainer container) {
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(Fluidium::updateFluidTickable));
    }

    private static void updateFluidTickable() {
        BuiltInRegistries.FLUID.entrySet().forEach(entry -> {
            ResourceLocation id = entry.getKey().location();
            ((IFluid) entry.getValue()).fluidium$setShouldAlwaysTick(FluidiumConfig.ALWAYS_TICK_FLUIDS.contains(id) || FluidiumConfig.ALWAYS_TICK_MODS.contains(id.getNamespace()));
        });
    }

    public static boolean canDelay(@NotNull ServerLevel level, @NotNull BlockPos pos) {
        return !ActiveChunks.include(level.dimension().location(), pos.getY(), Positions.toChunk(pos));
    }
}