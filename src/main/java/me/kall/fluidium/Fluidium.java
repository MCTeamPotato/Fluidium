package me.kall.fluidium;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(Fluidium.MOD_ID)
public final class Fluidium {
    public static final String MOD_ID = "fluidium";

    private static double getNearestPlayerDistSq(@NotNull ServerLevel level, BlockPos pos) {
        double minDistSq = Double.MAX_VALUE;

        for (ServerPlayer player : level.players()) {
            double dx = player.getX() - pos.getX();
            double dy = player.getY() - pos.getY();
            double dz = player.getZ() - pos.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < minDistSq) minDistSq = distSq;
        }

        return minDistSq;
    }

    public static boolean shouldOptimize(ServerLevel level, BlockPos pos, int optDist) {
        if (optDist <= 0) return false;

        double distSq = getNearestPlayerDistSq(level, pos);
        double thresholdSq = optDist * optDist;

        return distSq > thresholdSq;
    }
}
