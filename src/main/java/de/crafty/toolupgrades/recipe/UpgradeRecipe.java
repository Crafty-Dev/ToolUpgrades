package de.crafty.toolupgrades.recipe;

import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpgradeRecipe {


    private final String id;
    private final UpgradeItem upgrade;
    private final Type type;
    private final Material[] ingredients;

    public UpgradeRecipe(String id, UpgradeItem upgrade, Type type, Material... ingredients) {
        this.id = id;
        this.upgrade = upgrade;
        this.type = type;

        this.ingredients = ingredients;
    }


    public UpgradeItem getUpgrade() {
        return this.upgrade;
    }

    public Type getType() {
        return this.type;
    }

    public Material[] getIngredients() {
        return this.ingredients;
    }

    public String getId() {
        return this.id;
    }

    public void save(FileConfiguration config) {

        config.set("upgrade", this.upgrade.getId());
        config.set("recipeType", this.type.name());

        for (int i = 0; i < this.ingredients.length; i++) {
            Material item = this.ingredients[i];

            config.set("ingredients." + i, item != null ? item.toString() : "NONE");
        }

    }

    public static UpgradeRecipe load(File file) {

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        UpgradeItem upgrade = UpgradeItem.byName(config.getString("upgrade"));
        Type type = Type.valueOf(config.getString("recipeType"));


        List<Material> ingredients = new ArrayList<>();
        int i = 0;
        while (config.contains("ingredients." + i)) {
            if(config.getString("ingredients." + i).equals("NONE")){
                ingredients.add(null);
                i++;
                continue;
            }
            ingredients.add(Material.valueOf(config.getString("ingredients." + i++)));
        }

        return new UpgradeRecipe(file.getName().substring(0, file.getName().length() - 4), upgrade, type, ingredients.toArray(new Material[0]));
    }


    public enum Type {

        SHAPED,
        SHAPELESS,
        SMELTING,
        BLASTING,
        SMOKING

    }

    public record CreationData(String recipeId, Type recipeType, UpgradeItem upgradeItem) {
    }


}
