package dev.tancop.pistoncommand;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Datagen provider that makes block entity behavior match Bedrock.
public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PistonCommand.MOD_ID, existingFileHelper);
    }

    // Helper to make addTags less verbose
    private ResourceLocation res(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    // Banners, signs and campfires break instead of blocking on Bedrock.
    // Also tags some modded blocks that don't work after move
    @Override
    protected void addTags(@NotNull HolderLookup.Provider lookupProvider) {
        tag(PistonCommand.PISTON_BEHAVIOR_BLOCK).addTag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).addOptional(res("create", "fluid_tank"))
                .addOptional(res("create", "creative_fluid_tank"));

        tag(PistonCommand.PISTON_BEHAVIOR_DESTROY).addTag(BlockTags.CAMPFIRES).addTag(BlockTags.BANNERS).addTag(BlockTags.ALL_SIGNS)
                .addOptionalTag(res("create", "brittle")).addOptional(res("create", "belt"));
    }
}