package dev.tancop.movabletiles.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.tancop.movabletiles.MovableTiles;
import dev.tancop.movabletiles.PistonMovingBlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.level.block.Block.dropResources;

// This is where the magic happens.
@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
    @Final
    @Shadow
    private boolean isSticky;

    // This method is copied from vanilla because we're making 3 changes at different
    // points in the code. I really hope this counts as fair use.
    @Inject(method = "moveBlocks", at = @At("HEAD"), cancellable = true)
    private void onMoveBlocksStart(Level level, BlockPos pos, Direction facing, boolean extending, CallbackInfoReturnable<Boolean> cir) {
        // CHANGE: create a list to store moved block entities
        ArrayList<BlockEntity> movedEntities = new ArrayList<>();
        // END CHANGE

        BlockPos blockpos = pos.relative(facing);
        if (!extending && level.getBlockState(blockpos).is(Blocks.PISTON_HEAD)) {
            level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 20);
        }

        PistonStructureResolver pistonstructureresolver = new PistonStructureResolver(level, pos, facing, extending);
        if (!pistonstructureresolver.resolve()) {
            cir.setReturnValue(false);
        } else {
            Map<BlockPos, BlockState> map = Maps.newHashMap();
            List<BlockPos> list = pistonstructureresolver.getToPush();
            List<BlockState> list1 = Lists.newArrayList();

            for (BlockPos blockpos1 : list) {
                BlockState blockstate = level.getBlockState(blockpos1);
                list1.add(blockstate);
                map.put(blockpos1, blockstate);
            }

            // CHANGE: store moved block entities into the list
            for (int i = 0; i < list.size(); i++) {
                BlockPos blockEntityPos = list.get(i);
                BlockEntity blockEntity = (list1.get(i).hasBlockEntity()) ? level.getBlockEntity(blockEntityPos) : null;
                movedEntities.add(blockEntity);
                if (blockEntity != null) {
                    level.removeBlockEntity(blockEntityPos);
                }
            }
            // END CHANGE

            List<BlockPos> list2 = pistonstructureresolver.getToDestroy();
            BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
            Direction direction = extending ? facing : facing.getOpposite();
            int i = 0;

            for (int j = list2.size() - 1; j >= 0; j--) {
                BlockPos blockpos2 = list2.get(j);
                BlockState blockstate1 = level.getBlockState(blockpos2);
                BlockEntity blockentity = blockstate1.hasBlockEntity() ? level.getBlockEntity(blockpos2) : null;
                dropResources(blockstate1, level, blockpos2, blockentity);
                blockstate1.onDestroyedByPushReaction(level, blockpos2, direction, level.getFluidState(blockpos2));
                if (!blockstate1.is(BlockTags.FIRE)) {
                    level.addDestroyBlockEffect(blockpos2, blockstate1);
                }

                ablockstate[i++] = blockstate1;
            }

            for (int k = list.size() - 1; k >= 0; k--) {
                BlockPos blockpos3 = list.get(k);
                BlockState blockstate5 = level.getBlockState(blockpos3);
                blockpos3 = blockpos3.relative(direction);
                map.remove(blockpos3);
                BlockState blockstate8 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, facing);
                level.setBlock(blockpos3, blockstate8, 68);
                // CHANGE: store moved entity into the MovingBlockEntity
                BlockEntity entity = MovingPistonBlock.newMovingBlockEntity(blockpos3, blockstate8, list1.get(k), facing, extending, false);
                ((PistonMovingBlockEntityExt) entity).setMovedEntity(movedEntities.get(k));
                level.setBlockEntity(entity);
                // END CHANGE
                ablockstate[i++] = blockstate5;
            }

            if (extending) {
                PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState blockstate4 = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, facing)
                        .setValue(PistonHeadBlock.TYPE, pistontype);
                BlockState blockstate6 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, facing)
                        .setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
                map.remove(blockpos);
                level.setBlock(blockpos, blockstate6, 68);
                level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockpos, blockstate6, blockstate4, facing, true, true));
            }

            BlockState blockstate3 = Blocks.AIR.defaultBlockState();

            for (BlockPos blockpos4 : map.keySet()) {
                level.setBlock(blockpos4, blockstate3, 82);
            }

            for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
                BlockPos blockpos5 = entry.getKey();
                BlockState blockstate2 = entry.getValue();
                blockstate2.updateIndirectNeighbourShapes(level, blockpos5, 2);
                blockstate3.updateNeighbourShapes(level, blockpos5, 2);
                blockstate3.updateIndirectNeighbourShapes(level, blockpos5, 2);
            }

            i = 0;

            for (int l = list2.size() - 1; l >= 0; l--) {
                BlockState blockstate7 = ablockstate[i++];
                BlockPos blockpos6 = list2.get(l);
                blockstate7.updateIndirectNeighbourShapes(level, blockpos6, 2);
                level.updateNeighborsAt(blockpos6, blockstate7.getBlock());
            }

            for (int i1 = list.size() - 1; i1 >= 0; i1--) {
                level.updateNeighborsAt(list.get(i1), ablockstate[i++].getBlock());
            }

            if (extending) {
                level.updateNeighborsAt(blockpos, Blocks.PISTON_HEAD);
            }

            cir.setReturnValue(true);
        }
    }

    // Replaces the final `state.hasBlockEntity()` check in `isPushable`.
    // Inverted because the result is negated in the original method
    @Redirect(method = "isPushable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world" + "/level/block/state/BlockState;hasBlockEntity()Z"))
    private static boolean isBlockEntityUnPushable(BlockState blockState) {
        return blockState.is(MovableTiles.PISTONS_CANNOT_MOVE);
    }
}
