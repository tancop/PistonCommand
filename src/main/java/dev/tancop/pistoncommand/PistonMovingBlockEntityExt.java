package dev.tancop.pistoncommand;

import net.minecraft.world.level.block.entity.BlockEntity;

// Extension interface for PistonMovingBlockEntityMixin
public interface PistonMovingBlockEntityExt {
    BlockEntity getMovedEntity();

    void setMovedEntity(BlockEntity movedEntity);
}
