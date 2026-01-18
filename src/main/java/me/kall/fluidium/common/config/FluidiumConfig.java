package me.kall.fluidium.common.config;

import me.kall.duplicationless.config.JsonConfig;
import me.kall.fluidium.Fluidium;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class FluidiumConfig {
    private static final JsonConfig CONFIG = JsonConfig.create(Fluidium.MOD_ID, "1")
            .put("HorizontalChunkRadius", 2)
            .put("VerticalChunkRadius", 1)
            .put("FluidTickDelayChance", 0.50)
            .put("FluidsThatAlwaysTick(RegistryName)", new ArrayList<>())
            .put("FluidsThatAlwaysTick(ModID)", new ArrayList<>())
            .initialize();

    public static final int HORIZONTAL_CHUNK_RADIUS = CONFIG.getInt("HorizontalChunkRadius");
    public static final int VERTICAL_CHUNK_RADIUS = CONFIG.getInt("VerticalChunkRadius");
    public static final float DELAY_CHANCE = (float) CONFIG.getDouble("FluidTickDelayChance");
    public static final Set<ResourceLocation> ALWAYS_TICK_FLUIDS = CONFIG.getStream("FluidsThatAlwaysTick(RegistryName)", String.class).map(ResourceLocation::parse).collect(Collectors.toSet());
    public static final Set<String> ALWAYS_TICK_MODS = CONFIG.getSet("FluidsThatAlwaysTick(ModID)", String.class);
}
