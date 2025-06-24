package dev.tancop.pistoncommand;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Datagen provider that adds `#c:relocation_not_supported` to the block list.
public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PistonCommand.MOD_ID, existingFileHelper);
    }

    // Helper to make addTags less verbose
    private ResourceLocation res(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    // Sets up default tag contents
    @Override
    protected void addTags(@NotNull HolderLookup.Provider lookupProvider) {
        tag(PistonCommand.PISTON_BEHAVIOR_BLOCK).addTag(Tags.Blocks.RELOCATION_NOT_SUPPORTED)
                .addOptional(res("create", "fluid_tank")) // Fluid tanks are multiblock structures that leave a hole when moved
                .addOptional(res("create", "creative_fluid_tank"))
                .addOptional(res("create", "mechanical_piston")) // Mechanical pistons get disconnected and break
                .addOptional(res("create", "sticky_mechanical_piston"))
                .addOptional(res("create", "belt")); // Belts stop working and play a broken animation
    }
}