package dev.tancop.pistoncommand;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(PistonCommand.MOD_ID)
public class PistonCommand {
    public static final String MOD_ID = "pistoncommand";

    // Pistons move blocks in this tag like normal
    public static final TagKey<Block> PISTON_BEHAVIOR_NORMAL = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "piston_behavior_normal"));

    // Pistons can't move blocks in this tag
    public static final TagKey<Block> PISTON_BEHAVIOR_BLOCK = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "piston_behavior_block"));

    // Pistons destroy blocks in this tag
    public static final TagKey<Block> PISTON_BEHAVIOR_DESTROY = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "piston_behavior_destroy"));

    // Pistons can push blocks in this tag but not pull them
    public static final TagKey<Block> PISTON_BEHAVIOR_PUSH_ONLY = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "piston_behavior_push_only"));

    public PistonCommand(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::gatherData);
        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    // Registers the new tags for datagen
    private void gatherData(final GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        event.getGenerator().addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookupProvider, existingFileHelper));
    }
}
