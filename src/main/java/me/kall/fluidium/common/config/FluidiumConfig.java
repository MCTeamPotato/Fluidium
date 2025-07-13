package me.kall.fluidium.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class FluidiumConfig {
    public static final ForgeConfigSpec INSTANCE;

    public static final ForgeConfigSpec.IntValue OPT_DIST;
    public static final ForgeConfigSpec.DoubleValue TICK_CHANCE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Fluidium");
        OPT_DIST = builder.defineInRange("FluidOptimizableDist", 32, 0, 63);
        TICK_CHANCE = builder.defineInRange("FluidTickDelayChance", 0.50, 0.00, 1.00);
        builder.pop();
        INSTANCE = builder.build();
    }
}
