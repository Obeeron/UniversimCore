package com.obeeron.universim.modules.recipes;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeHasher {
    public static Integer hashIngredients(Recipe recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe)
            return hashIngredients(shapedRecipe);
        if (recipe instanceof ShapelessRecipe shapelessRecipe)
            return hashIngredients(shapelessRecipe);
        else
            Universim.getInstance().getLogger().warning("Trying to hash a recipe which neither a shaped nor shapeless recipe.");
        return null;
    }

    public static int hashIngredients(ShapelessRecipe shapelessRecipe) {
        HashMap<String, Integer> ingredientMap = new HashMap<>();
        for (RecipeChoice choice : shapelessRecipe.getChoiceList()) {
            if (choice instanceof MaterialChoice) {
                for (Material material : ((MaterialChoice)choice).getChoices()) {
                    String materialId = material.getKey().toString();
                    ingredientMap.put(materialId, ingredientMap.getOrDefault(materialId, 0) + 1);
                }
            } else if (choice instanceof ExactChoice) {
                for (ItemStack ingredient : ((ExactChoice)choice).getChoices()) {
                    String ingredientId = ingredient.getType().getKey().toString();
                    ingredientMap.put(ingredientId, ingredientMap.getOrDefault(ingredientId, 0) + 1);
                }
            }
        }
        return ingredientMap.hashCode();
    }

    private static int hashIngredients(ShapedRecipe shapedRecipe) {
        String[] shape = shapedRecipe.getShape();
        String[][] ingredientMatrix = new String[shape.length][shape[0].length()];
        Map<Character, RecipeChoice> ingredientMap = shapedRecipe.getChoiceMap();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length(); j++) {
                RecipeChoice choice = ingredientMap.get(shape[i].charAt(j));
                if (choice instanceof MaterialChoice) {
                    List<Material> material = ((MaterialChoice)choice).getChoices();
                    ingredientMatrix[i][j] = material.get(0).getKey().toString();
                } else if (choice instanceof ExactChoice) {
                    List<ItemStack> ingredient = ((ExactChoice)choice).getChoices();
                    ingredientMatrix[i][j] += ingredient.get(0).getType().getKey().toString();
                }
            }
        }
        return Arrays.deepHashCode(ingredientMatrix);
    }

    public static int hashShapelessCraftInventory(CraftingInventory inventory) {
        HashMap<NamespacedKey, Integer> ingredientMap = new HashMap<>();
        for (ItemStack ingredient : inventory.getMatrix()) {
            NamespacedKey ingredientId = UVSCore.getItemId(ingredient);
            if (ingredientId == null) continue;
            ingredientMap.put(ingredientId, ingredientMap.getOrDefault(ingredientId, 0) + 1);
        }
        return ingredientMap.hashCode();
    }

    public static int hashShapedCraftInventory(CraftingInventory inventory){
        ItemStack[] matrix = inventory.getMatrix();

        // Get matrix shape, 3x3 or 2x2
        int matrixSide;
        if (matrix.length == 9) {
            matrixSide = 3;
        } else if (matrix.length == 4) {
            matrixSide = 2;
        } else {
            throw new IllegalArgumentException("Invalid matrix size: " + matrix.length);
        }

        // Get recipe sub-matrix borders
        int matrixMinX = matrixSide-1, matrixMaxX = 0;
        int matrixMinY = matrixSide-1, matrixMaxY = 0;

        // Find the minimum and maximum x and y coordinates of the matrix where there is an ingredient
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) continue;

            int x = i % matrixSide;
            int y = i / matrixSide;

            if (x < matrixMinX) matrixMinX = x;
            if (x > matrixMaxX) matrixMaxX = x;
            if (y < matrixMinY) matrixMinY = y;
            if (y > matrixMaxY) matrixMaxY = y;
        }

        // If the matrix is empty, stop here
        if (matrixMinX > matrixMaxX || matrixMinY > matrixMaxY)
            return Arrays.deepHashCode(new NamespacedKey[0][0]);

        // Create a recipe sub-matrix containing the ingredients ids
        int matrixWidth = matrixMaxX - matrixMinX + 1;
        int matrixHeight = matrixMaxY - matrixMinY + 1;
        NamespacedKey[][] ingredientMatrix = new NamespacedKey[matrixHeight][matrixWidth];
        for (int i = matrixMinX; i <= matrixMaxX; i++) {
            for (int j = matrixMinY; j <= matrixMaxY; j++) {
                ingredientMatrix[j-matrixMinY][i-matrixMinX] = UVSCore.getItemId(matrix[j*matrixSide+i]);
            }
        }

        // Return the hash of the recipe sub-matrix
        return Arrays.deepHashCode(ingredientMatrix);
    }
}
