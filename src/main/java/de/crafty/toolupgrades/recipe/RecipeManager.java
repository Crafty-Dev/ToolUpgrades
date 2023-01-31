package de.crafty.toolupgrades.recipe;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Material.*;

public class RecipeManager {


    public static final HashMap<Inventory, UpgradeRecipe.CreationData> CREATORS = new HashMap<>();

    private static final File FILE = new File("plugins/ToolUpgrades/recipes");
    private static final List<UpgradeRecipe> RECIPES = new ArrayList<>();

    public static void init() {

        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "\u00a7aLoading Recipes...");

        if (FILE.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Recipe Folder could not be found, initializing...");
            initDefaults();
        }

        loadRecipes();

        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "\u00a7aRecipes have been loaded");
    }

    private static void initDefaults() {
        defaults().forEach(RecipeManager::saveRecipe);
    }

    private static void loadRecipes() {

        File[] files = FILE.listFiles();

        if (files == null)
            return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml"))
                continue;

            try {
                RECIPES.add(UpgradeRecipe.load(file));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Found bad recipe file: " + file.getName() + "; Skipping...");
            }

        }

        addToMinecraft();

    }


    public static void addRecipe(UpgradeRecipe recipe) {
        RECIPES.add(recipe);
        switch (recipe.getType()) {
            case SHAPED -> addShaped(recipe);
            case SHAPELESS -> addShapeless(recipe);
            case SMELTING -> addSmelting(recipe);
            case BLASTING -> addBlasting(recipe);
            case SMOKING -> addSmoking(recipe);
        }

        RecipeManager.saveRecipe(recipe);
    }

    public static void deleteRecipe(UpgradeRecipe recipe) {
        RECIPES.remove(recipe);
        Bukkit.removeRecipe(new NamespacedKey(ToolUpgrades.getInstance(), "recipes/" + recipe.getId()));

        File file = new File(FILE, recipe.getId() + ".yml");
        if (!file.delete())
            Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Failed to delete Recipe File: " + recipe.getId() + ".yml");
    }


    public static void saveRecipes() {
        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Saving Upgrade Recipes...");
        RECIPES.forEach(RecipeManager::saveRecipe);
        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Upgrade Recipes have been saved");
    }

    public static void reset() {
        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Resetting Upgrade Recipes...");
        new ArrayList<>(RECIPES).forEach(RecipeManager::deleteRecipe);
        RecipeManager.initDefaults();
        RecipeManager.loadRecipes();
        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Upgrade Recipes have been resetted");
    }

    private static void saveRecipe(UpgradeRecipe recipe) {
        File file = new File(FILE, recipe.getId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        recipe.save(config);

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Failed to save recipe: " + recipe.getId());
        }
    }

    private static List<UpgradeRecipe> defaults() {

        List<UpgradeRecipe> defaults = new ArrayList<>();

        UpgradeRecipe auto_smelting = new UpgradeRecipe("auto_smelting", UpgradeItem.AUTO_SMELTING, UpgradeRecipe.Type.SHAPED,
                LAVA_BUCKET, OBSIDIAN, LAVA_BUCKET,
                OBSIDIAN, FURNACE, OBSIDIAN,
                LAVA_BUCKET, OBSIDIAN, LAVA_BUCKET
        );
        defaults.add(auto_smelting);

        UpgradeRecipe ender_mask = new UpgradeRecipe("ender_mask", UpgradeItem.ENDER_MASK, UpgradeRecipe.Type.SHAPED,
                END_STONE, END_STONE, END_STONE,
                END_STONE, CARVED_PUMPKIN, END_STONE,
                END_STONE, END_STONE, END_STONE
        );
        defaults.add(ender_mask);

        UpgradeRecipe fadeless = new UpgradeRecipe("fadeless", UpgradeItem.FADELESS, UpgradeRecipe.Type.SHAPED,
                GLOWSTONE, GOLD_INGOT, GLOWSTONE,
                GOLD_INGOT, WITHER_SKELETON_SKULL, GOLD_INGOT,
                GLOWSTONE, GOLD_INGOT, GLOWSTONE
        );
        defaults.add(fadeless);

        UpgradeRecipe life_bonus = new UpgradeRecipe("life_bonus", UpgradeItem.LIFE_BONUS, UpgradeRecipe.Type.SHAPED,
                BLAZE_POWDER, GLISTERING_MELON_SLICE, BLAZE_POWDER,
                GLISTERING_MELON_SLICE, NETHERITE_INGOT, GLISTERING_MELON_SLICE,
                BLAZE_POWDER, GLISTERING_MELON_SLICE, BLAZE_POWDER
        );
        defaults.add(life_bonus);

        UpgradeRecipe magnetism = new UpgradeRecipe("magnetism", UpgradeItem.MAGNETISM, UpgradeRecipe.Type.SHAPED,
                DIAMOND_SWORD, DIAMOND, DIAMOND_PICKAXE,
                DIAMOND, ENDER_PEARL, DIAMOND,
                DIAMOND_PICKAXE, DIAMOND, DIAMOND_SWORD
        );
        defaults.add(magnetism);

        UpgradeRecipe mob_capture = new UpgradeRecipe("mob_capture", UpgradeItem.MOB_CAPTURE, UpgradeRecipe.Type.SHAPED,
                COBWEB, ROTTEN_FLESH, COBWEB,
                ROTTEN_FLESH, BUCKET, ROTTEN_FLESH,
                COBWEB, ROTTEN_FLESH, COBWEB
        );
        defaults.add(mob_capture);

        UpgradeRecipe multi_miner = new UpgradeRecipe("multi_miner", UpgradeItem.MULTI_MINER, UpgradeRecipe.Type.SHAPED,
                DEEPSLATE, COBBLESTONE, DEEPSLATE,
                COBBLESTONE, NETHERITE_INGOT, COBBLESTONE,
                DEEPSLATE, COBBLESTONE, DEEPSLATE
        );
        defaults.add(multi_miner);

        UpgradeRecipe silky = new UpgradeRecipe("silky", UpgradeItem.SILKY, UpgradeRecipe.Type.SHAPED,
                WHITE_WOOL, STRING, WHITE_WOOL,
                STRING, NETHER_STAR, STRING,
                WHITE_WOOL, STRING, WHITE_WOOL
        );
        defaults.add(silky);

        UpgradeRecipe soft_fall = new UpgradeRecipe("soft_fall", UpgradeItem.SOFT_FALL, UpgradeRecipe.Type.SHAPED,
                POWDER_SNOW_BUCKET, FEATHER, POWDER_SNOW_BUCKET,
                FEATHER, DIAMOND_BOOTS, FEATHER,
                POWDER_SNOW_BUCKET, FEATHER, POWDER_SNOW_BUCKET
        );
        defaults.add(soft_fall);

        UpgradeRecipe teleportation = new UpgradeRecipe("teleportation", UpgradeItem.TELEPORTATION, UpgradeRecipe.Type.SHAPED,
                END_STONE, ENDER_PEARL, END_STONE,
                ENDER_PEARL, DIAMOND_SWORD, ENDER_PEARL,
                END_STONE, ENDER_PEARL, END_STONE
        );
        defaults.add(teleportation);

        return defaults;
    }


    private static void addToMinecraft() {
        shapedRecipes().forEach(RecipeManager::addShaped);
        shapelessRecipes().forEach(RecipeManager::addShapeless);
        smeltingRecipes().forEach(RecipeManager::addSmelting);
        blastingRecipes().forEach(RecipeManager::addBlasting);
        smokingRecipes().forEach(RecipeManager::addSmoking);

    }

    private static void addShaped(UpgradeRecipe recipe) {
        StringBuilder shape = new StringBuilder();

        HashMap<Material, Character> ingredients = new HashMap<>();
        char i = 65;

        for (Material mat : recipe.getIngredients()) {

            if (mat == null) {
                shape.append(' ');
                continue;
            }

            if (!ingredients.containsKey(mat))
                ingredients.put(mat, i++);

            shape.append(ingredients.get(mat));
        }

        char[] c_shape = shape.toString().toCharArray();
        String[] s_shape = new String[9];
        for (int j = 0; j < 9; j++) {
            s_shape[j] = String.valueOf(c_shape[j]);
        }

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(ToolUpgrades.getInstance(), "recipes/" + recipe.getId()), recipe.getUpgrade().getStack());
        shapedRecipe.shape(s_shape[0] + s_shape[1] + s_shape[2], s_shape[3] + s_shape[4] + s_shape[5], s_shape[6] + s_shape[7] + s_shape[8]);

        ingredients.forEach((material, character) -> shapedRecipe.setIngredient(character, material));
        Bukkit.addRecipe(shapedRecipe);
    }

    private static void addShapeless(UpgradeRecipe recipe) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(ToolUpgrades.getInstance(), "recipes/" + recipe.getId()), recipe.getUpgrade().getStack());
        Arrays.stream(recipe.getIngredients()).filter(Objects::nonNull).forEach(shapelessRecipe::addIngredient);

        Bukkit.addRecipe(shapelessRecipe);
    }

    private static void addSmelting(UpgradeRecipe recipe) {
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(ToolUpgrades.getInstance(), "recipes/" + recipe.getId()), recipe.getUpgrade().getStack(), recipe.getIngredients()[0], 0.0F, 200);
        Bukkit.addRecipe(furnaceRecipe);
    }

    private static void addBlasting(UpgradeRecipe recipe) {
        BlastingRecipe blastingRecipe = new BlastingRecipe(new NamespacedKey(ToolUpgrades.getInstance(), "recipes/" + recipe.getId()), recipe.getUpgrade().getStack(), recipe.getIngredients()[0], 0.0F, 100);
        Bukkit.addRecipe(blastingRecipe);
    }

    private static void addSmoking(UpgradeRecipe recipe) {
        SmokingRecipe smokingRecipe = new SmokingRecipe(new NamespacedKey(ToolUpgrades.getInstance(), "recipes/" + recipe.getId()), recipe.getUpgrade().getStack(), recipe.getIngredients()[0], 0.0F, 100);
        Bukkit.addRecipe(smokingRecipe);
    }


    public static List<UpgradeRecipe> recipes() {
        return RECIPES;
    }

    public static List<UpgradeRecipe> shapedRecipes() {
        return RECIPES.stream().filter(recipe -> recipe.getType() == UpgradeRecipe.Type.SHAPED).toList();
    }

    public static List<UpgradeRecipe> shapelessRecipes() {
        return RECIPES.stream().filter(recipe -> recipe.getType() == UpgradeRecipe.Type.SHAPELESS).toList();
    }

    public static List<UpgradeRecipe> smeltingRecipes() {
        return RECIPES.stream().filter(recipe -> recipe.getType() == UpgradeRecipe.Type.SMELTING).toList();
    }

    public static List<UpgradeRecipe> blastingRecipes() {
        return RECIPES.stream().filter(recipe -> recipe.getType() == UpgradeRecipe.Type.BLASTING).toList();
    }

    public static List<UpgradeRecipe> smokingRecipes() {
        return RECIPES.stream().filter(recipe -> recipe.getType() == UpgradeRecipe.Type.SMOKING).toList();
    }

}
