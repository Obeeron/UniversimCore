package com.obeeron.universim.modules.recipes.factory;

import com.obeeron.universim.modules.recipes.customRecipe.ShapedCustomRecipe;
import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.modules.recipes.customRecipe.CustomRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class ShapedCustomRecipeFactory implements CustomRecipeFactory {
    @Override
    public CustomRecipe create(ConfigurationSection section) throws CustomRecipeParsingException {
        return new ShapedCustomRecipe(section);
    }
}
