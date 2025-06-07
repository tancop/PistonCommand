package dev.tancop.pistoncommand.mixin;

import dev.tancop.pistoncommand.PistonCommand;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {
    @Redirect(method = "addBlockLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;" +
            "getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"))
    private PushReaction destroyInBlockLine(BlockState instance) {
        if (instance.is(PistonCommand.PISTON_BEHAVIOR_DESTROY)) {
            return PushReaction.DESTROY;
        }
        return instance.getPistonPushReaction();
    }

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;" + "getPistonPushReaction" +
            "()Lnet/minecraft/world/level/material/PushReaction;"))
    private PushReaction destroyInResolve(BlockState instance) {
        if (instance.is(PistonCommand.PISTON_BEHAVIOR_DESTROY)) {
            return PushReaction.DESTROY;
        }
        return instance.getPistonPushReaction();
    }
}
