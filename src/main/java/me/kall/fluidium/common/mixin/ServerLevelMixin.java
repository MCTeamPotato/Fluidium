package me.kall.fluidium.common.mixin;

import me.kall.fluidium.Fluidium;
import me.kall.fluidium.common.config.FluidiumConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    @Shadow public abstract @NotNull List<ServerPlayer> players();

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickFluid", at = @At("HEAD"), cancellable = true)
    private void fluidium$onTick(BlockPos pos, Fluid fluid, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (Fluidium.CHECKER.isClaimed(level, pos) || Fluidium.isChunkForced(pos, level)) return;

        int fluidDelay = fluid.getTickDelay(this);

        if (Fluidium.shouldOptimize(this.players(), pos, FluidiumConfig.OPT_DIST.get()) && ThreadLocalRandom.current().nextFloat(0.0F, 1.0F) < FluidiumConfig.TICK_CHANCE.get().floatValue()) {
            this.scheduleTick(pos, fluid, fluidDelay);
            ci.cancel();
        }
    }
}
