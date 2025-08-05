package me.kall.fluidium.common.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class FluidiumConfig {
    public static final ForgeConfigSpec INSTANCE;

    public static final ForgeConfigSpec.IntValue OPT_DIST;
    public static final ForgeConfigSpec.DoubleValue TICK_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST, MOD_ID_LIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Fluidium");
        OPT_DIST = builder.defineInRange("FluidOptimizableDist", 32, 0, 63);
        TICK_CHANCE = builder.defineInRange("FluidTickDelayChance", 0.50, 0.00, 1.00);
        BLACKLIST = builder.defineList("FluidsAlwaysTick(RegistryName)", Lists.newArrayList(), Predicates.alwaysTrue());
        MOD_ID_LIST = builder.defineList("FluidsAlwaysTick(ModID)", Lists.newArrayList(), Predicates.alwaysTrue());
        builder.pop();
        INSTANCE = builder.build();
    }
}
