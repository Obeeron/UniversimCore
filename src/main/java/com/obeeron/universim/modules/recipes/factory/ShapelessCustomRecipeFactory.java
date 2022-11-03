package com.obeeron.universim.modules.recipes.factory;

import com.obeeron.universim.modules.recipes.customRecipe.ShapelessCustomRecipe;
import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.modules.recipes.customRecipe.CustomRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class ShapelessCustomRecipeFactory implements CustomRecipeFactory {
    @Override
    public CustomRecipe create(ConfigurationSection section) throws CustomRecipeParsingException {
        return new ShapelessCustomRecipe(section);
    }
}
