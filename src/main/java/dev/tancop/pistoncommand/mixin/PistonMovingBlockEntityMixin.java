package dev.tancop.pistoncommand.mixin;

import dev.tancop.pistoncommand.BlockEntityExt;
import dev.tancop.pistoncommand.PistonMovingBlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Extends PistonMovingBlockEntity to store block entities and move them over to their new position
@Mixin(PistonMovingBlockEntity.class)
@Implements(@Interface(iface = PistonMovingBlockEntityExt.class, prefix = "ext$"))
public class PistonMovingBlockEntityMixin extends BlockEntity {
    @Unique
    BlockEntity ext$movedEntity;

    public PistonMovingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public BlockEntity ext$getMovedEntity() {
        return ext$movedEntity;
    }

    public void ext$setMovedEntity(BlockEntity entity) {
        if (entity != null) {
            entity.setChanged();
        }
        this.ext$movedEntity = entity;
    }

    // Moves the stored block entity and changes its internal position
    @Inject(method = "tick", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/Level;neighborChanged(Lnet/minecraft/core/BlockPos;" + "Lnet/minecraft/world/level/block/Block;Lnet" +
                    "/minecraft/core/BlockPos;)V", shift = At.Shift.AFTER))
    private static void replaceBlockEntity(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        BlockEntity movedEntity = ((PistonMovingBlockEntityExt) blockEntity).getMovedEntity();
        if (movedEntity != null) {
            ((BlockEntityExt) movedEntity).setBlockPos(pos);

            level.setBlockEntity(movedEntity);
        }
    }
}
