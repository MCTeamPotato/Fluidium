package me.kall.fluidium.common.integration.ftbchunks;

import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import me.kall.fluidium.common.integration.IChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class FTBChecker implements IChecker {
    @Override
    public boolean isClaimed(Level level, BlockPos pos) {
        if (!FTBChunksAPI.isManagerLoaded()) return false;
        return FTBChunksAPI.getManager().getChunk(new ChunkDimPos(level, pos)) != null;
    }
}
