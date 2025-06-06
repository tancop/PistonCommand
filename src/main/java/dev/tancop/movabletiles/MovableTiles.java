package dev.tancop.movabletiles;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(MovableTiles.MOD_ID)
public class MovableTiles {
    public static final String MOD_ID = "movabletiles";

    // Pistons can't move blocks in this tag
    public static final TagKey<Block> PISTONS_CANNOT_MOVE = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "pistons_cannot_move"));

    public MovableTiles(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::gatherData);
    }

    // Registers the new tag for datagen
    private void gatherData(final GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        event.getGenerator().addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookupProvider, existingFileHelper));
    }
}
