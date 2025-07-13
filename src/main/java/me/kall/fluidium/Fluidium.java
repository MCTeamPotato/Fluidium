package me.kall.fluidium;

import me.kall.fluidium.common.config.FluidiumConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod(Fluidium.MOD_ID)
public final class Fluidium {
    public static final String MOD_ID = "fluidium";

    public Fluidium(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, FluidiumConfig.INSTANCE);
    }

    public static boolean shouldOptimize(List<ServerPlayer> players, BlockPos pos, int optDist) {
        if (optDist <= 0) return false;
        double thresholdSq = optDist * optDist;
        for (ServerPlayer player : players) {
            double dx = player.getX() - pos.getX();
            double dy = player.getY() - pos.getY();
            double dz = player.getZ() - pos.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq <= thresholdSq) {
                return false;
            }
        }
        return true;
    }
}