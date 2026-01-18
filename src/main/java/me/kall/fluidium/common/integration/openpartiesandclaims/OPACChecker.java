package me.kall.fluidium.common.integration.openpartiesandclaims;

import me.kall.fluidium.common.integration.IChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class OPACChecker implements IChecker {
    @Override
    public boolean isClaimed(Level level, BlockPos pos) {
        return false;
    }
}
