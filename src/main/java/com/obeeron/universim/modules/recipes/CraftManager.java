package com.obeeron.universim.modules.recipes;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;
import com.obeeron.universim.config.Config;
import com.obeeron.universim.config.ConfigManager;
import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import com.obeeron.universim.modules.recipes.factory.CustomRecipeFactory;
import com.obeeron.universim.modules.recipes.factory.ShapelessCustomRecipeFactory;
import com.obeeron.universim.modules.recipes.listeners.CustomRecipeListener;
import com.obeeron.universim.modules.recipes.customRecipe.CustomRecipe;
import com.obeeron.universim.modules.recipes.factory.ShapedCustomRecipeFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;

import java.util.*;

public class CraftManager {

    private final Map<String, CustomRecipeFactory> customRecipeFactoryMap = new HashMap<>() {{
        put("shaped", new ShapedCustomRecipeFactory());
        put("shapeless", new ShapelessCustomRecipeFactory());
    }};

    private static final CraftManager instance = new CraftManager();

    private final Map<Integer, CustomRecipe> customRecipesHashMap = new HashMap<>();
    private final Map<Integer, Recipe> vanillaRecipesHashMap = new HashMap<>();

    private final Universim plugin;
    private final Config recipesConfig;

    private CraftManager() {
        this.plugin = Universim.getInstance();
        this.recipesConfig = ConfigManager.getInstance().getConfig("customRecipes.yml");
        recipesConfig.saveDefaultConfig();
    }

    public static CraftManager getInstance() {
        return instance;
    }

    public static void initialize() {
        Bukkit.getServer().getPluginManager().registerEvents(new CustomRecipeListener(), Universim.getInstance());
        getInstance().loadRecipesFromConfig();
    }

    private void loadRecipesFromConfig(){
        // get 'customRecipes.yml' from config
        FileConfiguration recipesConfig = this.recipesConfig.getConfig();

        // Fill vanilla recipe hashmap with all shaped and shapeless vanilla recipes
        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe)
                vanillaRecipesHashMap.put(RecipeHasher.hashIngredients(recipe), recipe);
        }

        int customRecipeNb = 0;
        for (String recipeType : recipesConfig.getKeys(false))
            if (this.customRecipeFactoryMap.containsKey(recipeType)) {
                registerRecipeSection(recipesConfig.getConfigurationSection(recipeType), customRecipeFactoryMap.get(recipeType));
                customRecipeNb += 1;                
            }
        Universim.getInstance().getLogger().info("Loaded " + customRecipeNb + " recipe(s)");
    }

    private void registerRecipeSection(ConfigurationSection section, CustomRecipeFactory customRecipeFactory) {
        if (section == null)
            return;

        for (String recipeId: section.getKeys(false)) {
            CustomRecipe customRecipe;
            try {
                customRecipe = customRecipeFactory.create(section.getConfigurationSection(recipeId));
                customRecipe.validate();
            }
            catch (CustomRecipeParsingException | IllegalArgumentException e) {
                plugin.getLogger().warning("Could not parse custom recipe '" + recipeId + "': " + e.getMessage());
                continue;
            } catch (Exception e) {
                plugin.getLogger().warning("Unknown error while parsing custom recipe '" + recipeId + "': " + e.getMessage());
                e.printStackTrace();
                continue;
            }

            registerCustomRecipe(customRecipe);
        }
    }

    public void registerCustomRecipe(CustomRecipe customRecipe) {
        // If the recipe does not override any vanilla recipe, register it to server
        if (vanillaRecipesHashMap.get(RecipeHasher.hashIngredients(customRecipe)) == null) {
            Universim.getInstance().getServer().addRecipe(customRecipe);
        }
        customRecipesHashMap.put(customRecipe.hash(), customRecipe);
        plugin.getLogger().info("Registered custom recipe '" + customRecipe.getRecipeId() + "'");
    }

    public void handlePrepareEvent(PrepareItemCraftEvent event) {
        // If the recipe crafting matrix is empty do nothing
        if (Arrays.stream(event.getInventory().getMatrix()).allMatch(Objects::isNull)) return;

        // Retrieve custom recipe
        CustomRecipe customRecipe = null;
        Recipe recipe = event.getRecipe();
        if (recipe instanceof ShapedRecipe)
            customRecipe = customRecipesHashMap.get(RecipeHasher.hashShapedCraftInventory(event.getInventory()));
        else if (recipe instanceof ShapelessRecipe)
            customRecipe = customRecipesHashMap.get(RecipeHasher.hashShapelessCraftInventory(event.getInventory()));

        // If the recipe is a custom recipe, set the result
        if (customRecipe != null)
            event.getInventory().setResult(customRecipe.getResult());
        // Otherwise if it contains universim ingredients, set the result to null as it is not a valid recipe
        else if (containsUnivIngredients(event.getInventory().getMatrix()))
            event.getInventory().setResult(null);
    }

    // If the ingredient has an universim id, return it, otherwise return its minecraft NamespacedKey
    private boolean containsUnivIngredients(ItemStack[] matrix) {
        for (ItemStack itemStack : matrix) {
            if (itemStack == null) continue;
            if (UVSCore.getItemId(itemStack).getNamespace().equals(Universim.getNamespace())) return true;
        }
        return false;
    }
}
