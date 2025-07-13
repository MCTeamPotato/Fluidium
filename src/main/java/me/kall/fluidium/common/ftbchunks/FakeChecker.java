package me.kall.fluidium.common.ftbchunks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class FakeChecker implements IChunkChecker {
    @Override
    public boolean isClaimed(ServerLevel level, BlockPos pos) {
        return false;
    }
}
