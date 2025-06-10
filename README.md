# Piston Command

![Chest placed on top of an extended piston](./src/main/resources/pistoncommand.png)

Inspired by [Piston Control](https://www.curseforge.com/minecraft/mc-mods/piston-control)
from [the\_will\_bl](https://www.curseforge.com/members/the_will_bl/projects)

This mod lets you **move block entities** with a piston - chests, furnaces and everything else
that's [movable on Bedrock](https://minecraft.wiki/w/Piston#Limitations). Works with
**vanilla and Fabric clients** when installed on the server. Also adds some **new tags** to control the way blocks move
with data packs:

* `pistoncommand:piston_behavior_normal` - pistons can push and pull the block like normal
* `pistoncommand:piston_behavior_block` - blocks pistons like obsidian or bedrock
* `pistoncommand:piston_behavior_destroy` - gets destroyed like beds and torches
* `pistoncommand:piston_behavior_push_only` - can be pushed but not pulled with a sticky piston, like glazed terracotta

**Campfires, banners and signs break** instead of moving to match Bedrock. Everything tagged with
`#c:relocation_not_supported` blocks pistons by default.

Piston Command is **compatible with Create** and as many other mods as possible. If you notice any problems like blocks
not updating or visual bugs please [report them on GitHub](https://github.com/tancop/PistonCommand/issues).