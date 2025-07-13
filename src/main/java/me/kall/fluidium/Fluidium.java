package me.kall.fluidium;

import me.kall.fluidium.common.config.FluidiumConfig;
import me.kall.fluidium.common.ftbchunks.ChunkChecker;
import me.kall.fluidium.common.ftbchunks.FakeChecker;
import me.kall.fluidium.common.ftbchunks.IChunkChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod(Fluidium.MOD_ID)
public final class Fluidium {
    public static final String MOD_ID = "fluidium";

    public static final IChunkChecker CHECKER = FMLLoader.getLoadingModList().getModFileById("ftbchunks") != null ? new ChunkChecker() : new FakeChecker();

    public Fluidium(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, FluidiumConfig.INSTANCE);
    }

    public static boolean shouldOptimize(@NotNull List<ServerPlayer> players, BlockPos pos, int optDist) {
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