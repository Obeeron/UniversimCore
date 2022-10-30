package com.obeeron.universim.craft.factory;

import com.obeeron.universim.craft.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.craft.customRecipe.CustomRecipe;
import org.bukkit.configuration.ConfigurationSection;

public interface CustomRecipeFactory {
    CustomRecipe create(ConfigurationSection section) throws CustomRecipeParsingException;
}
