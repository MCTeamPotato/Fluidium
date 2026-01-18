package me.kall.fluidium.common.mixin;

import me.kall.duplicationless.util.Positions;
import me.kall.fluidium.Fluidium;
import me.kall.fluidium.common.api.IFluid;
import me.kall.fluidium.common.config.FluidiumConfig;
import me.kall.fluidium.common.integration.ClaimManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "tickFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    private void fluidium$onTick(BlockPos pos, Fluid fluid, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (ClaimManager.isClaimed(level, pos) || level.getForcedChunks().contains(Positions.toChunk(pos)) || ((IFluid)fluid).fluidium$shouldAlwaysTick()) return;
        if (ThreadLocalRandom.current().nextFloat() < FluidiumConfig.DELAY_CHANCE && Fluidium.canDelay(level, pos)) {
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
            ci.cancel();
        }
    }
}
