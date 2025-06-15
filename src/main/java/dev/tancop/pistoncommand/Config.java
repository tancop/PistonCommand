package dev.tancop.pistoncommand;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Boolean> PUSH_BLOCK_ENTITIES = builder.comment("Are block entities pushable by default?")
            .translation("config.pistoncommand.push_block_entities")
            .define("push_block_entities", true);

    public static final ModConfigSpec SPEC = builder.build();
}
