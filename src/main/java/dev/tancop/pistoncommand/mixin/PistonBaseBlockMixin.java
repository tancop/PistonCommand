package dev.tancop.pistoncommand.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.tancop.pistoncommand.Config;
import dev.tancop.pistoncommand.PistonCommand;
import dev.tancop.pistoncommand.PistonMovingBlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.level.block.piston.PistonBaseBlock.EXTENDED;

// This is where the magic happens.
@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin extends DirectionalBlock {
    @Final
    @Shadow
    private boolean isSticky;

    protected PistonBaseBlockMixin(Properties p_52591_) {
        super(p_52591_);
    }

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

    // Replaces `isPushable` to respect new tags.
    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private static void isBlockEntityUnPushable(BlockState state, Level level, BlockPos pos, Direction movementDirection, boolean allowDestroy,
                                                Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getMaxBuildHeight() - 1 || !level.getWorldBorder().isWithinBounds(pos)) {
            cir.setReturnValue(false);
        } else if (state.isAir()) {
            cir.setReturnValue(true);
        } else if (state.is(Blocks.OBSIDIAN) || state.is(Blocks.CRYING_OBSIDIAN) || state.is(Blocks.RESPAWN_ANCHOR) || state.is(
                Blocks.REINFORCED_DEEPSLATE)) {
            cir.setReturnValue(false);
        } else if (movementDirection == Direction.DOWN && pos.getY() == level.getMinBuildHeight()) {
            cir.setReturnValue(false);
        } else if (movementDirection == Direction.UP && pos.getY() == level.getMaxBuildHeight() - 1) {
            cir.setReturnValue(false);
        } else {
            if (!state.is(Blocks.PISTON) && !state.is(Blocks.STICKY_PISTON)) {
                if (state.getDestroySpeed(level, pos) == -1.0F) {
                    cir.setReturnValue(false);
                    return;
                }

                // Tags override vanilla/mod default behavior
                if (state.is(PistonCommand.PISTON_BEHAVIOR_NORMAL)) {
                    cir.setReturnValue(true);
                    return;
                } else if (state.is(PistonCommand.PISTON_BEHAVIOR_BLOCK)) {
                    cir.setReturnValue(false);
                    return;
                } else if (state.is(PistonCommand.PISTON_BEHAVIOR_DESTROY)) {
                    cir.setReturnValue(allowDestroy);
                    return;
                } else if (state.is(PistonCommand.PISTON_BEHAVIOR_PUSH_ONLY)) {
                    cir.setReturnValue(movementDirection == pistonFacing);
                    return;
                }

                switch (state.getPistonPushReaction()) {
                    case BLOCK:
                        cir.setReturnValue(false);
                        return;
                    case DESTROY:
                        cir.setReturnValue(allowDestroy);
                        return;
                    case PUSH_ONLY:
                        cir.setReturnValue(movementDirection == pistonFacing);
                        return;
                }
            } else if (state.getValue(EXTENDED)) {
                cir.setReturnValue(false);
                return;
            }

            if (Config.PUSH_BLOCK_ENTITIES.get()) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(!state.hasBlockEntity());
            }
        }
    }
}
