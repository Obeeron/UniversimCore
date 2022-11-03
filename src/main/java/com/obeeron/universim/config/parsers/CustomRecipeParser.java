package com.obeeron.universim.config.parsers;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;
import com.obeeron.universim.modules.universimItems.UnivItemManager;
import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class CustomRecipeParser {
    public static ItemStack parseResult(ConfigurationSection resultSection, String recipeId) throws CustomRecipeParsingException {
        String resultId = recipeId;
        if (resultSection != null){
            resultId = resultSection.getString("id", recipeId);
        }

        // Get output namespaced key if provided or generate one
        NamespacedKey resultNSK = UVSCore.getNSKFromStr(resultId);
        if (resultNSK == null)
            throw new CustomRecipeParsingException("Invalid result id : "+resultId);

        ItemStack result;

        // If NSK is from Minecraft, check it and generate item from it
        if (resultNSK.getNamespace().equals(NamespacedKey.MINECRAFT)){
            Material material = Material.getMaterial(resultNSK.getKey().toUpperCase());
            if (material == null)
                throw new CustomRecipeParsingException("Invalid recipe id, no such minecraft id : "+resultNSK.getKey());
            result = new ItemStack(material);
        }
        // Else if NSK is universim the retrieve the item from the universim item manager
        else if (resultNSK.getNamespace().equals(Universim.getNamespace())){
            result = UnivItemManager.getInstance().getUnivItem(resultNSK);
            if (result == null)
                throw new CustomRecipeParsingException("Invalid recipe id, no such universim id : "+resultNSK.getKey());
        }
        else {
            throw new CustomRecipeParsingException("Invalid recipe id, no such namespace : "+resultNSK.getNamespace());
        }

        // Apply "result" section to the item
        if (resultSection != null){
            int amount = resultSection.getInt("amount");
            if(amount > 0 && amount <= result.getMaxStackSize()) {
                result.setAmount(amount);
            }

            ConfigurationSection metaSection = resultSection.getConfigurationSection("meta");
            if (metaSection != null){
                ItemMeta newMeta = UnivItemParser.parseItemMeta(metaSection, result);
                result.setItemMeta(newMeta);
            }
        }

        return result;
    }

    public static String[] parseShape(ConfigurationSection recipeSection) throws CustomRecipeParsingException {
        if (!recipeSection.contains("shape"))
            throw new CustomRecipeParsingException("Missing shape section");
        return recipeSection.getStringList("shape").toArray(new String[0]);
    }

    public static HashMap<Character, NamespacedKey> parseShapedIngredients(ConfigurationSection recipeSection) throws CustomRecipeParsingException {
        ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("ingredients");
        if (ingredientsSection == null)
            throw new CustomRecipeParsingException("Missing ingredients section");

        HashMap<Character, NamespacedKey> ingredientsMap = new HashMap<>();
        for (String item_id : ingredientsSection.getKeys(false)) {
            String ingredientIdStr = ingredientsSection.getString(item_id);
            if (ingredientIdStr == null)
                throw new CustomRecipeParsingException("Missing ingredient material for item_id: " + item_id);

            NamespacedKey ingredientNSK = UVSCore.getNSKFromStr(ingredientIdStr);
            Material ingredientMaterial = UnivItemManager.getInstance().getMaterialFromNSK(ingredientNSK);

            if (ingredientMaterial == null)
                throw new CustomRecipeParsingException("Invalid ingredient '"+ ingredientNSK +"', neither a minecraft id nor an universim id");

            ingredientsMap.put(item_id.charAt(0), ingredientNSK);
        }
        return ingredientsMap;
    }

    public static HashMap<NamespacedKey, Integer> parseShapelessIngredients(ConfigurationSection section) throws CustomRecipeParsingException {
        // Parse ingredients subsection
        ConfigurationSection ingredientsSection = section.getConfigurationSection("ingredients");
        if (ingredientsSection == null)
            throw new CustomRecipeParsingException("Missing ingredients section");

        HashMap<NamespacedKey, Integer> ingredientsMap = new HashMap<>();
        for (String itemId : ingredientsSection.getKeys(false)) {
            int ingredientAmount = ingredientsSection.getInt(itemId, 1);

            NamespacedKey ingredientNSK = UVSCore.getNSKFromStr(itemId);
            Material ingredientMaterial = UnivItemManager.getInstance().getMaterialFromNSK(ingredientNSK);

            if (ingredientMaterial == null)
                throw new CustomRecipeParsingException("Invalid ingredient '"+ itemId +"', neither a minecraft id nor an universim id");

            ingredientsMap.put(ingredientNSK, ingredientAmount);
        }
        return ingredientsMap;
    }
}
