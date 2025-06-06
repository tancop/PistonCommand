package dev.tancop.movabletiles.mixin;

import dev.tancop.movabletiles.BlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockEntity.class)
@Implements(@Interface(iface = BlockEntityExt.class, prefix = "ext$"))
public class BlockEntityMixin extends net.neoforged.neoforge.attachment.AttachmentHolder {
    @Mutable
    @Shadow
    @Final
    protected BlockPos worldPosition;

    public void ext$setBlockPos(BlockPos pos) {
        this.worldPosition = pos;
    }
}
