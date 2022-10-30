package com.obeeron.universim.craft.factory;

import com.obeeron.universim.craft.customRecipe.ShapelessCustomRecipe;
import com.obeeron.universim.craft.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.craft.customRecipe.CustomRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class ShapelessCustomRecipeFactory implements CustomRecipeFactory {
    @Override
    public CustomRecipe create(ConfigurationSection section) throws CustomRecipeParsingException {
        return new ShapelessCustomRecipe(section);
    }
}
