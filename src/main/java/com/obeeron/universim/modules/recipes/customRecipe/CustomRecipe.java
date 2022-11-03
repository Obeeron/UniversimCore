package com.obeeron.universim.modules.recipes.customRecipe;

import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe, Keyed {
    void validate() throws CustomRecipeParsingException;
    int hash();

    String getRecipeId();
}
