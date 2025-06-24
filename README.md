# Piston Command

![Chest placed on top of an extended piston](./src/main/resources/pistoncommand.png)

Inspired by [Piston Control](https://www.curseforge.com/minecraft/mc-mods/piston-control)
from [the\_will\_bl](https://www.curseforge.com/members/the_will_bl/projects)

This mod gives you control over the way pistons move blocks with four new data pack tags:

* `pistoncommand:piston_behavior_normal` - pistons can push and pull the block like normal
* `pistoncommand:piston_behavior_block` - blocks pistons like obsidian or bedrock
* `pistoncommand:piston_behavior_destroy` - gets destroyed like beds and torches
* `pistoncommand:piston_behavior_push_only` - can be pushed but not pulled with a sticky piston, like glazed terracotta

Also lets you **move block entities** like chests and furnaces. You can turn this on for all blocks in the mod config
(`Mods > Piston Command > Config`) or by setting `push_block_entities` to `true` in your server's
`config/pistoncommand-server.toml`.

You can match Bedrock behavior by installing the `Bedrock Pistons` data pack and turning on `Push Block Entities`. This
makes all block entities pushable other than campfires, signs, banners and broken modded blocks.

Works with **vanilla and Fabric clients** when installed on the server. We're trying to be compatible with as many other
mods as possible. If you notice any problems like blocks not updating or visual bugs
please [report them on GitHub](https://github.com/tancop/PistonCommand/issues).

# Incompatible Blocks

Some modded blocks can stop working when pushed. You should never add these to `piston_behavior_normal` or
`piston_behavior_push_only`:

* Create mechanical belt - separates from other belt blocks, stops moving items even if pulled back and plays a broken
  animation
* Create fluid tank - breaks the multiblock structure
* Create mechanical piston - leaves behind useless and visually broken extension poles
* Anything tagged as `#c:relocation_not_supported`. Other mods use this tag for blocks that can trigger gamebreaking
  bugs or even **crash the server** when moved with a piston.