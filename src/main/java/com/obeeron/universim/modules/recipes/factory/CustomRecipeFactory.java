package com.obeeron.universim.modules.recipes.factory;

import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.modules.recipes.customRecipe.CustomRecipe;
import org.bukkit.configuration.ConfigurationSection;

public interface CustomRecipeFactory {
    CustomRecipe create(ConfigurationSection section) throws CustomRecipeParsingException;
}
