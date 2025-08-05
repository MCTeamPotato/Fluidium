package me.kall.fluidium.common.mixin;

import me.kall.fluidium.Fluidium;
import me.kall.fluidium.common.config.FluidiumConfig;
import me.kall.fluidium.common.integration.ClaimManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow public abstract @NotNull List<ServerPlayer> players();

    @Inject(method = "tickFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    private void fluidium$onTick(BlockPos pos, Fluid fluid, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (ClaimManager.isClaimed(level, pos) || Fluidium.isChunkForced(pos, level) || Fluidium.isFluidAlwaysTick(fluid)) return;

        int fluidDelay = fluid.getTickDelay(level);

        if (Fluidium.shouldOptimize(this.players(), pos, FluidiumConfig.OPT_DIST.get()) && ThreadLocalRandom.current().nextFloat(0.0F, 1.0F) < FluidiumConfig.TICK_CHANCE.get().floatValue()) {
            level.scheduleTick(pos, fluid, fluidDelay);
            ci.cancel();
        }
    }
}
