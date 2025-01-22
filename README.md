# UniversimCore
Core plugin for Universim Minecraft RP server (1.19.4)

## Features

- **Custom Item Manager**
  - Add new items with custom textures based on *optifine CIT* and *custom universim ids* stored as NBT
  - support for custom boat (based on *optifine CEM*). Name of the custom item is copy to the boat entity
  - Items description stored in configuration file ```univItems.yml```
- **Recipe Manager**
  - Craft Manager
    - Add new recipes combining vanilla and or custom items
  - Recipes description stored in configuration file ```customRecipes.yml```
- ItemHolder : universim:item_holder
  - Custom item frame that turns invisible when containing an item
- **Commands**
  - ```/universim``` command for staff

## Configuration

- **universimId**: A string that identifies the item.
- **itemId**: Either a minecraft ("*iron_ingot*", "*diamond_chestplate*") or ```universimId```.
- **metaInfos**: Describes meta-data to apply to the item. Format can be found in Bukkit sources https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/inventory/CraftMetaItem.java or this website that explains some of it https://dev.bukkit.org/projects/mobmanager/pages/configuration/item-formatting

If there is a collision between a universim and a minecraft id then the minecraft id is used first. To avoid collisions, namespacedkeys are supported. For example "*universim:itemid*" or "*minecraft:itemid*".

### univItems.yml

Describe new items

```yml
<universimId>:
  material: <itemId>
  meta:
    <metaInfos>
```

- ```material```: Optional field that describes the material the custom item is based on.
  - If material is not set, defaults to "*STICK*".
- ```meta```: Optional section that describes additional meta-datas applied to the custom item.
  - If no ```meta``` section or if no ```display-name``` field is set in the ```meta``` section. The item name will be the ```universimId``` capatalized with spaces instead of underscores.

#### Example:
```yml
enriched_diamond:
  material: diamond
  meta:
    lore:
      - "A heavy diamond."
      - "Glows white from the inside."
    
coin:
  material: gold_ingot

coin_pile:
  material: gold_ingot
  meta:
    display-name: Pile of coins
```

### customRecipes.yml

Describe recipes.  
Supports shaped and shapeless recipes for crafting table and player inventory crafting.

```yml
# Shaped recipes
shaped:
  <recipeId>:
    shape:
      - " a "
      - "aba"
      - " a "
    ingredients:
      a: <itemId>
      b: <itemId>
    result:
      id: <itemId>
      amount: <amount>
      meta:
        <metaInfos>

# Shapeless recipes  
shapeless:
  <recipeId>:
    ingredients:
      <itemId>: <amount>
      <itemId>: <amount>
    result:
      id: <itemId>
      amount: <amount>
      meta:
        <metaInfos>
```

- ```recipeId```: A string that defines the recipe, can be chosen randomly.
  - If ```result.id``` is not, set, ```recipeId``` is used as the result item id.
- ```shape```: This section describes the shape of the recipe with character used as keys later defined in the ```ingredients``` section.
  - Must be rectangular. Horizontal symmetry is automacally handled.
- ```ingredients```: This section maps a character key from the ```shape``` section to an ```itemId```.
- ```result: This optional section gives additional informations for to the result of a recipe.
- ```result.id```: Optional field that defines the ```itemId``` of the result. If not set, then the ```recipeId``` is used.
- ```result.amount```: Optional field that sets the quantity of the recipe results. If not set, default is 1.

**Important**: 2 recipes can not have the same recipeId, if you wish to create 2 recipes for the same itemId, then use ```result.id```

#### Example:
```yml
# Shaped recipes
shaped:
  enriched_diamond:
    shape:
      - " * "
      - "*q*"
      - " * "
    ingredients:
      "*": diamond
      q: quartz

# Shapeless recipes
shapeless:
  coin:
    ingredients:
      gold_ingot: 1
      iron_ingot: 1

  coinFromCoinPile:
    ingredients:
      coin_pile: 1
    result:
      id: coin
      amount: 8

  coin_pile:
    ingredients:
      coin: 8
```

## Commands

```
/universim <reload | give | id | showmeta>
```
aliases: univ, uvs

Supports tab completion for sub-commands and universim items.

### /universim reload

> Usage: /universim reload

Reloads the universim plugin and its configuration files.

### /universim give

> Usage: /universim give <universimId> [amount]

Give yourself a universim custom item using its ```universimId```.

### /universim id

> Usage: /universim id <get|remove|set \<universimId\>>

Gets, removes or sets the universim id of the held item.

### /universim showmeta

> Usage: /universim showmeta

Shows the meta-data of the held item in the serialized configuration format. Usefull for knowing what to put in the ```meta``` section.
