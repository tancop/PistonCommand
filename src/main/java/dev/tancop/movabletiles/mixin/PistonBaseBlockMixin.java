package dev.tancop.movabletiles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tancop.movabletiles.MovableTiles;
import dev.tancop.movabletiles.PistonMovingBlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
    @Unique
    ArrayList<BlockEntity> immersiveMagic$movedEntities = new ArrayList<>();

    // Collect every moved block entity into a list
    @Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;getOpposite()Lnet/minecraft/core/Direction;"))
    private void storeMovedEntities(Level level, BlockPos pos, Direction facing, boolean extending, CallbackInfoReturnable<Boolean> cir,
                                    @Local(ordinal = 0) List<BlockPos> list, @Local(ordinal = 1) List<BlockState> list1) {
        immersiveMagic$movedEntities.clear();

        for (int i = 0; i < list.size(); i++) {
            BlockPos blockEntityPos = list.get(i);
            BlockEntity blockEntity = (list1.get(i).hasBlockEntity()) ? level.getBlockEntity(blockEntityPos) : null;
            immersiveMagic$movedEntities.add(blockEntity);
            if (blockEntity != null) {
                level.removeBlockEntity(blockEntityPos);
            }
        }
    }


    // Set moved entity field on every moving block
    @Redirect(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;" +
            "newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;" + "Lnet/minecraft/world/level" + "/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)" + "Lnet/minecraft/world/level/block/entity/BlockEntity;", ordinal = 0))
    private BlockEntity loadMovedIntoEntity(BlockPos pos, BlockState blockState, BlockState movedState, Direction direction, boolean extending,
                                            boolean isSourcePiston, @Local(name = "k") int k) {
        BlockEntity entity = MovingPistonBlock.newMovingBlockEntity(pos, movedState, blockState, direction, extending, isSourcePiston);
        ((PistonMovingBlockEntityExt) entity).setMovedEntity(immersiveMagic$movedEntities.get(k));
        return entity;
    }

    // Inverted because the result of `hasBlockEntity` is negated in the original method
    @Redirect(method = "isPushable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world" + "/level/block/state/BlockState;hasBlockEntity()Z"))
    private static boolean isBlockEntityUnPushable(BlockState blockState) {
        if (blockState.hasBlockEntity()) {
            System.out.println("Checking block entity " + blockState.getBlock().getName().getString());
            if (blockState.is(MovableTiles.MOVABLE_BLOCK_ENTITIES)) {
                System.out.println("Block entity can move");
            } else {
                System.out.println("Block entity can't move");
            }
            return !blockState.is(MovableTiles.MOVABLE_BLOCK_ENTITIES);
        }
        return false;
    }
}
