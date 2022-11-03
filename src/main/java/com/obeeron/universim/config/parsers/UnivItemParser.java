package com.obeeron.universim.config.parsers;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;
import com.obeeron.universim.modules.universimItems.UnivItemManager;
import com.obeeron.universim.modules.recipes.exceptions.CustomRecipeParsingException;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class UnivItemParser {

    public static HashMap<NamespacedKey, ItemStack> parseUnivItems(FileConfiguration config) {
        Map<String, Material> univMaterialMap = getUnivMaterialMap(config);
        HashMap<NamespacedKey, ItemStack> univItems = new HashMap<>();

        // For each univItem in the config
        for (String univId : config.getKeys(false)){
            ConfigurationSection univItemSection = config.getConfigurationSection(univId);
            if (univItemSection == null){
                continue;
            }

            try {
                NamespacedKey itemNSK = UVSCore.getNSKFromStr(univId);
                if (itemNSK == null)
                    throw new CustomRecipeParsingException("Invalid univ_id : "+univId);

                ItemStack univItem = UnivItemParser.parseUnivItem(univItemSection, univMaterialMap);
                univItems.put(UVSCore.univNSK(itemNSK.getKey()), univItem);
                Universim.getInstance().getLogger().info("Loaded univ item '" + univId + "'");
            } catch (CustomRecipeParsingException e) {
                Universim.getInstance().getLogger().warning("Could not parse univ item '" + univId + "' : " + e);
            } catch (Exception e) {
                Universim.getInstance().getLogger().warning("Unknown exception while parsing univ item '" + univId + "' : " + e);
                e.printStackTrace();
            }
        }

        return univItems;
    }

    private static ItemStack parseUnivItem(ConfigurationSection univItemSection, Map<String, Material> univMaterialMap) throws CustomRecipeParsingException {
        if (univItemSection == null) {
            throw new CustomRecipeParsingException("Empty universim item section");
        }

        // Retrieve Universim ID
        String univId = univItemSection.getName();

        // Retrieve material if provided, otherwise use default material
        String materialStr = univItemSection.getString("material");
        Material material = parseMaterial(materialStr, univMaterialMap);

        // Create result item
        ItemStack result = new ItemStack(material);

        // Add additional meta
        ConfigurationSection metaSection = univItemSection.getConfigurationSection("meta");
        if (metaSection != null){
            ItemMeta newMeta = parseItemMeta(metaSection, result);
            result.setItemMeta(newMeta);
        }
        ItemMeta meta = result.getItemMeta();
        if (meta!= null && !meta.hasDisplayName())
            meta.setDisplayName(ChatColor.RESET + WordUtils.capitalizeFully(univId.toLowerCase().replace("_", " ")));
        result.setItemMeta(meta);

        UVSCore.setUnivId(result, univId);

        return result;
    }

    private static Material parseMaterial(String materialStr, Map<String, Material> univMaterialMap) throws CustomRecipeParsingException {
        if (materialStr == null)
            return UnivItemManager.getDefaultMaterial();

        NamespacedKey materialNSK = UVSCore.getNSKFromStr(materialStr);
        if (materialNSK == null)
            throw new CustomRecipeParsingException("Invalid material : " + materialStr);

        Material material = (materialNSK.getNamespace().equals("universim")) ?
                univMaterialMap.get(materialStr.toUpperCase()) :
                Material.getMaterial(materialNSK.getKey().toUpperCase());

        if (material == null)
            throw new CustomRecipeParsingException("Invalid material : " + materialStr);

        return material;
    }

    public static ItemMeta parseItemMeta(ConfigurationSection metaSection, ItemStack itemStack) throws CustomRecipeParsingException {
        // Get the meta of the result
        ItemMeta resultBaseMeta = itemStack.getItemMeta();
        if (resultBaseMeta == null)
            resultBaseMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());

        try {
            // Get the meta-map of the result
            HashMap<String, Object> resultMetaMap = new HashMap<>(Objects.requireNonNull(resultBaseMeta).serialize());

            // Get the meta-map to add
            Map<String, Object> metaMap = configurationSectionToMap(metaSection);
            metaMap.put("==", "ItemMeta");

            // Add the meta to add to the result meta
            resultMetaMap.putAll(metaMap);
            // Deserialize and set the result meta
            ItemMeta resultMeta = (ItemMeta) ConfigurationSerialization.deserializeObject(resultMetaMap);
            if (resultMeta != null && resultMeta.hasDisplayName())
                resultMeta.setDisplayName(ChatColor.RESET + resultMeta.getDisplayName());
            return resultMeta;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomRecipeParsingException("Invalid meta : " + e);
        }
    }

    private static Map<String, Object> configurationSectionToMap(ConfigurationSection metaSection) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : metaSection.getKeys(false)) {
            Object value = metaSection.get(key);
            if (value instanceof MemorySection) {
                value = configurationSectionToMap((ConfigurationSection) value);
            }
            else if (value instanceof List) {
                value = configurationListToList((List<?>) value);
            }
            result.put(key, value);
        }
        return result;
    }

    private static List<?> configurationListToList(List<?> list) {
        List<Object> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof MemorySection) {
                object = configurationSectionToMap((ConfigurationSection) object);
            }
            if (object instanceof List) {
                object = configurationListToList((List<?>) object);
            }
            result.add(object);
        }
        return result;
    }

    private static Map<String, Material> getUnivMaterialMap(FileConfiguration config) {
        Map<String, Material> materialMap = new HashMap<>();

        for (String univId : config.getKeys(false)) {
            ConfigurationSection univItemSection = config.getConfigurationSection(univId);
            if (univItemSection == null)
                continue;

            String materialStr = univItemSection.getString("material");
            // Skip if no 'material' key
            if (materialStr == null)
                continue;

            Material material = Material.getMaterial(materialStr.toUpperCase());

            // Only add to map if valid material
            if (material != null)
                materialMap.put(univId, material);
        }

        return materialMap;
    }
}
