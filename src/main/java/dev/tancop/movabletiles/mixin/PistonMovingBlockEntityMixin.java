package dev.tancop.movabletiles.mixin;

import dev.tancop.movabletiles.PistonMovingBlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

import java.util.Objects;

@Mixin(PistonMovingBlockEntity.class)
@Implements(@Interface(iface = PistonMovingBlockEntityExt.class, prefix = "ext$"))
public class PistonMovingBlockEntityMixin extends BlockEntity {
    @Unique
    BlockEntity immersiveMagic$movedEntity;

    public PistonMovingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public BlockEntity ext$getMovedEntity() {
        return immersiveMagic$movedEntity;
    }

    public void ext$setMovedEntity(BlockEntity entity) {
        if (entity != null) {
            entity.setChanged();
        }
        this.immersiveMagic$movedEntity = entity;
    }

    /*@Inject(method = "tick", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/Level;neighborChanged(Lnet/minecraft/core/BlockPos;" + "Lnet/minecraft/world/level/block/Block;" + "Lnet" +
                    "/minecraft/core/BlockPos;)V", shift = At.Shift.AFTER))
    private static void replaceBlockEntityTick(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        BlockEntity movedEntity = ((PistonMovingBlockEntityExt) blockEntity).getMovedEntity();
        if (movedEntity != null) {
            CompoundTag tag = movedEntity.saveWithoutMetadata(level.registryAccess());

            Objects.requireNonNull(level.getBlockEntity(pos)).loadWithComponents(tag, level.registryAccess());
            ((PistonMovingBlockEntityExt) blockEntity).setMovedEntity(null);
        }
    }*/

    @Inject(method = "finalTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core" +
            "/BlockPos;)V", shift = At.Shift.AFTER))
    private void replaceBlockEntityFinal(CallbackInfo ci) {
        if (immersiveMagic$movedEntity != null) {
            System.out.println("Replacing block entity with " + immersiveMagic$movedEntity);
            CompoundTag tag = immersiveMagic$movedEntity.saveWithoutMetadata(level.registryAccess());

            Objects.requireNonNull(level.getBlockEntity(worldPosition)).loadWithComponents(tag, level.registryAccess());
            ext$setMovedEntity(null);
        }
    }
}
