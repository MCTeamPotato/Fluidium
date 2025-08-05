package me.kall.fluidium.common.mixin;

import me.kall.fluidium.common.api.IFluid;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Fluid.class)
public class FluidMixin implements IFluid {
    @Unique private boolean fluidium$shouldAlwaysTick;

    @Override
    public boolean fluidium$shouldAlwaysTick() {
        return this.fluidium$shouldAlwaysTick;
    }

    @Override
    public void fluidium$setShouldAlwaysTick(boolean tickable) {
        this.fluidium$shouldAlwaysTick = tickable;
    }
}
