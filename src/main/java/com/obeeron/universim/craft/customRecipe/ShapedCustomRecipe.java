package com.obeeron.universim.craft.customRecipe;

import com.obeeron.universim.Universim;
import com.obeeron.universim.common.UnivItemManager;
import com.obeeron.universim.craft.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.config.parsers.CustomRecipeParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class ShapedCustomRecipe extends ShapedRecipe implements CustomRecipe {
    private final String recipeId;
    HashMap<Character, NamespacedKey> ingredientsMap;

    public ShapedCustomRecipe(ConfigurationSection section) throws CustomRecipeParsingException, IllegalArgumentException {
        super(new NamespacedKey(Universim.getInstance(), section.getName()), CustomRecipeParser.parseResult(section.getConfigurationSection("result"), section.getName()));
        this.recipeId = section.getName();

        // Parse shape subsection
        shape(CustomRecipeParser.parseShape(section));
        // Parse ingredients subsection
        ingredientsMap = CustomRecipeParser.parseShapedIngredients(section);

        for (Map.Entry<Character, NamespacedKey> entry : ingredientsMap.entrySet()) {
            NamespacedKey ingredientKey = entry.getValue();
            if (ingredientKey.getNamespace().equals("minecraft"))
                setIngredient(entry.getKey(), Objects.requireNonNull(Material.getMaterial(ingredientKey.getKey().toUpperCase())));
            else
                setIngredient(entry.getKey(), UnivItemManager.getInstance().getUnivItem(ingredientKey).getType());
        }
    }

    @Override
    public void validate() throws CustomRecipeParsingException {
        // Check that all ingredients in the this.shape() are in the ingredientsMap
        for (String row : getShape()) {
            for (char c : row.toCharArray()) {
                if (c == ' ') continue;
                if (!ingredientsMap.containsKey(c))
                    throw new CustomRecipeParsingException("Invalid shape, ingredient '" + c + "' is not mapped in the ingredients section");
            }
        }
    }

    @Override
    public int hash() {
        String[] rows = this.getShape();
        NamespacedKey[][] ingredientMatrix = new NamespacedKey[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                ingredientMatrix[i][j] = ingredientsMap.get(rows[i].charAt(j));
            }
        }
        return Arrays.deepHashCode(ingredientMatrix);
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }
}