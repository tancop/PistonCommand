package dev.tancop.movabletiles;

import net.minecraft.world.level.block.entity.BlockEntity;

// Extension interface for PistonMovingBlockEntityMixin
public interface PistonMovingBlockEntityExt {
    BlockEntity getMovedEntity();

    void setMovedEntity(BlockEntity movedEntity);
}
