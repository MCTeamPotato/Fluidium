package me.kall.fluidium.common.mixin;

import me.kall.fluidium.common.cache.PlayerRegionCache;
import me.kall.fluidium.common.event.ServerTickHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onFluidTick(Level level, BlockPos pos, FluidState state, CallbackInfo ci) {
        double skipChance = 0.5;//TODO: configurable

        double blockX = pos.getX() + 0.5;
        double blockY = pos.getY() + 0.5;
        double blockZ = pos.getZ() + 0.5;

        PlayerRegionCache cache = ServerTickHandler.getCache();
        boolean isNearPlayer = cache.isNearPlayer(level, blockX, blockY, blockZ);

        if (isNearPlayer) return;

        long seed = pos.asLong() ^ level.getGameTime();
        double rand = (double) (Math.abs(seed % 10000)) / 10000.0;

        if (rand < skipChance) {
            ci.cancel();
            level.scheduleTick(pos, state.getType(), 1);
        }
    }
}
