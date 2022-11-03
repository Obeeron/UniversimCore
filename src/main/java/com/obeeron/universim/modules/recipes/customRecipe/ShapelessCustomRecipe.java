package com.obeeron.universim.modules.recipes.customRecipe;

import com.obeeron.universim.Universim;
import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.modules.universimItems.UnivItemManager;
import com.obeeron.universim.config.parsers.CustomRecipeParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShapelessCustomRecipe extends ShapelessRecipe implements CustomRecipe {
    private final String recipeId;

    HashMap<NamespacedKey, Integer> ingredientsMap;

    public ShapelessCustomRecipe(ConfigurationSection section) throws CustomRecipeParsingException {
        super(new NamespacedKey(Universim.getInstance(), section.getName()), CustomRecipeParser.parseResult(section.getConfigurationSection("result"), section.getName()));
        this.recipeId = section.getName();

        ingredientsMap = CustomRecipeParser.parseShapelessIngredients(section);
        for (Map.Entry<NamespacedKey, Integer> entry : ingredientsMap.entrySet()) {
            NamespacedKey ingredientKey = entry.getKey();
            if (ingredientKey.getNamespace().equals("minecraft"))
                addIngredient(entry.getValue(), Objects.requireNonNull(Material.getMaterial(ingredientKey.getKey().toUpperCase())));
            else
                addIngredient(entry.getValue(), UnivItemManager.getInstance().getUnivItem(ingredientKey).getType());
        }
    }

    // TODO : Implement validate()
    @Override
    public void validate() throws CustomRecipeParsingException {
        // with entry set
        for (Map.Entry<NamespacedKey, Integer> entry : ingredientsMap.entrySet() ) {
            if ( entry.getValue() <= 0 )
                throw new CustomRecipeParsingException("For ingredient '"+entry.getKey()+"', amount must be > 0");
        }
    }

    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public int hash() {
        return ingredientsMap.hashCode();
    }
}
