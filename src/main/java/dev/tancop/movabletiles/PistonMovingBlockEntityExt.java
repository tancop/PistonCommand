package dev.tancop.movabletiles;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface PistonMovingBlockEntityExt {
    BlockEntity getMovedEntity();

    void setMovedEntity(BlockEntity movedEntity);
}
