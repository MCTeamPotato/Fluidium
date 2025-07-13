package me.kall.fluidium.common.ftbchunks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface IChunkChecker {
    boolean isClaimed(ServerLevel level, BlockPos pos);
}
