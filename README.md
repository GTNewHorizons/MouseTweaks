# MouseTweaks

Mouse Tweaks replaces the standard RMB dragging mechanic, adds two new LMB dragging mechanics and an ability to quickly move items with the scroll wheel.

### About this fork

All modloader support has been removed, except for FML. This means that just using LiteLoader or OptiFine will not load this mod. You are welcome to open a pull request if you figure out how to get LiteLoader mods to build.

## Tweaks

(The majority of this section was taken from Mouse Tweaks' [CurseForge listing](https://www.curseforge.com/minecraft/mc-mods/mouse-tweaks).)

Configuration file: `.minecraft/config/MouseTweaks.cfg`

### RMB Tweak

Very similar to the standard RMB dragging mechanic, with one difference: if you drag over a slot multiple times, an item will be put there multiple times.

**Configuration setting:** `RMBTweak=...` (`0` off / `1` on (default))

Hold down the right mouse button:

![rmb1](https://i.imgur.com/Uo7xF.png)

Drag your mouse around the crafting grid:

![rmb2](https://i.imgur.com/NCRED.png)

You can drag your mouse on top of existing items:

![rmb3](https://i.imgur.com/6MQv6.png)

### LMB Tweak (with item)

Lets you quickly pick up or move items of the same type.

**Configuration setting:** `LMBTweakWithItem=...` (`0` off / `1` on (default))

Hold your left mouse button to pick up an item:

![lmbitem1](https://i.imgur.com/ziuGG.png)

Drag your mouse across the inventory. Items of the same type will be picked up:

![lmbitem2](https://i.imgur.com/JDjsE.png)

Hold shift and drag. Items of the same type will be moved to the other inventory ("shift-clicked"):

![lmbitem3](https://i.imgur.com/YrvmT.png)

### LMB Tweak (without item)

Quickly move items into another inventory.

**Configuration setting:** `LMBTweakWithoutItem=...` (`0` off / `1` on (default))

Hold shift, then hold your left mouse button:

![lmbnoitem1](https://i.imgur.com/f9Ejp.png)

Drag your mouse across the inventory. Items will be moved to the other inventory ("shift-clicked"):

![lmbnoitem2](https://i.imgur.com/qBu6k.png)

### Wheel Tweak

Scroll to quickly move items between inventories. When you scroll down on an item stack, its items will be moved one by one. When you scroll up, items will be moved into it from another inventory.

**Configuration settings:**

`WheelTweak=...` (`0` off / `1` on (default))

`WheelSearchOrder=...`: When moving items from an inventory, the mod will search for items...
- `0` = first to last
- `1` = last to first (default)

`WheelScrollDirection=...`:
- `0` = scroll <ins>up</ins> to <ins>pull</ins> items (to cursor), scroll <ins>down</ins> to <ins>push</ins> items (from cursor) (default)
- `1` = inverse of `1`; scroll <ins>up</ins> to <ins>push</ins> items, scroll <ins>down</ins> to <ins>pull</ins> items
- `2` = "Inventory Position Aware"
  - mouse cursor in **player** (bottom) inventory: scroll <ins>up</ins> to <ins>push</ins> items, scroll <ins>down</ins> to <ins>pull</ins> items
  - mouse cursor in **other** (top) inventory: scroll <ins>up</ins> to <ins>pull</ins> items, scroll <ins>down</ins> to <ins>push</ins> items
- `3` = inverse of `2`
  - mouse cursor in **player** (bottom) inventory: scroll <ins>up</ins> to <ins>pull</ins> items, scroll <ins>down</ins> to <ins>push</ins> items
  - mouse cursor in **other** (top) inventory: scroll <ins>up</ins> to <ins>push</ins> items, scroll <ins>down</ins> to <ins>push</ins> items

`ScrollItemScaling=...`:
- `0` = "Multiple Wheel Clicks Move Multiple Items" (default)
- `1` = "Always Move One Item (macOS Compatibility)"
