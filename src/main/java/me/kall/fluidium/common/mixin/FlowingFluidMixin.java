package me.kall.fluidium.common.mixin;

import me.kall.fluidium.Fluidium;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTick(Level level, BlockPos pos, FluidState state, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        int fluidDelay = ((FlowingFluid) (Object) this).getTickDelay(level);

        if (Fluidium.shouldOptimize(serverLevel, pos, 32) && ThreadLocalRandom.current().nextFloat() < 0.5f) {
            level.scheduleTick(pos, state.getType(), fluidDelay);
            ci.cancel();
        }
    }
}
