package dev.tancop.movabletiles;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Datagen provider for `#movabletiles:movable_block_entities`.
public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MovableTiles.MOD_ID, existingFileHelper);
    }

    // Banners, signs and campfires are the only immovable block entities on Bedrock
    @Override
    protected void addTags(@NotNull HolderLookup.Provider lookupProvider) {
        tag(MovableTiles.PISTONS_CANNOT_MOVE).addTag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).addTag(BlockTags.CAMPFIRES).addTag(BlockTags.BANNERS)
                .addTag(BlockTags.ALL_SIGNS);
    }
}