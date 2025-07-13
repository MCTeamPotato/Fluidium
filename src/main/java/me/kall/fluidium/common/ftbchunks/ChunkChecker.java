package me.kall.fluidium.common.ftbchunks;

import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class ChunkChecker implements IChunkChecker {
    @Override
    public boolean isClaimed(ServerLevel level, BlockPos pos) {
        FTBChunksAPI.API api = FTBChunksAPI.api();
        if (api.isManagerLoaded()) {
            ClaimedChunk chunk = api.getManager().getChunk(new ChunkDimPos(level, pos));
            return chunk != null;
        }
        return false;
    }
}
