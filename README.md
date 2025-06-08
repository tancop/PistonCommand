# Piston Command

![Chest placed on top of an extended piston](./src/main/resources/pistoncommand.png)

(Based on [Piston Control](https://www.curseforge.com/minecraft/mc-mods/piston-control)
by [the_will_bl](https://www.curseforge.com/members/the_will_bl/projects))

This mod lets you **move block entities** like chests or furnaces with a piston, just like on Bedrock. Works with
**vanilla or Fabric clients** when installed on the server. Also adds some **new tags** to control the way blocks move
with data packs:

- `pistoncommand:piston_behavior_normal` - pistons can push and pull the block like normal
- `pistoncommand:piston_behavior_block` - blocks pistons like obsidian or bedrock
- `pistoncommand:piston_behavior_destroy` - gets destroyed like beds and torches
- `pistoncommand:piston_behavior_push_only` - can be pushed but not pulled with a sticky piston, like glazed terracotta

**Campfires, banners and signs break** instead of moving to match Bedrock. You can override this with a data pack and
make them movable like this:

`data/pistoncommand/tags/block/piston_behavior_destroy.json`

```json
{
  "remove": [
    "#minecraft:campfires",
    "#minecraft:banners",
    "#minecraft:all_signs"
  ]
}
```