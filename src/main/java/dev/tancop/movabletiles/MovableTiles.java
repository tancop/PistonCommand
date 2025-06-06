package dev.tancop.movabletiles;

import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MovableTiles.MOD_ID)
public class MovableTiles {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "movabletiles";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final TagKey<Block> MOVABLE_BLOCK_ENTITIES = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "movable_block_entities"));

    public MovableTiles(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        modEventBus.addListener(this::gatherData);
    }

    private void gatherData(final GatherDataEvent event) {
        System.out.println("Gathering data for MovableTiles...");
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        event.getGenerator().addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookupProvider, existingFileHelper));
    }
}
