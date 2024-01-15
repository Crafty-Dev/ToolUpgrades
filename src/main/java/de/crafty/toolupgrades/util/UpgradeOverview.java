package de.crafty.toolupgrades.util;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.recipe.RecipeManager;
import de.crafty.toolupgrades.recipe.UpgradeRecipe;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UpgradeOverview implements Listener {


    public static void open(Player player) {

        Inventory inv = Bukkit.createInventory(null, 9 * 6, "\u00a78Upgrade Overview");

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
            inv.setItem(i + 36 + 9, filler);
        }

        for (int i = 0; i < UpgradeItem.list().size(); i++) {
            UpgradeItem upgradeItem = UpgradeItem.list().get(i);
            ItemStack stack = upgradeItem.getStack().clone();

            ItemMeta upgradeMeta = stack.getItemMeta();
            List<String> upgradeLore = upgradeMeta.getLore();

            upgradeLore.add(" ");
            upgradeLore.add("\u00a78\u00a7oRight Click to get Upgrade");

            upgradeMeta.setLore(upgradeLore);
            stack.setItemMeta(upgradeMeta);

            inv.setItem(9 + i, stack);
        }

        player.openInventory(inv);

    }


    public static void printAll() {

        Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Available Upgrade Items: ");

        UpgradeItem.list().forEach(upgradeItem -> {
            Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + upgradeItem.getUpgrade().getDisplayName() + " \u00a77(\u00a75" + upgradeItem.getUpgrade().getType().displayName() + "\u00a77)");
        });

    }


    @EventHandler
    public void onOverviewClick(InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();
        InventoryView inventoryView = event.getView();
        Player player = (Player) event.getWhoClicked();

        if (!inventoryView.getTitle().equals("\u00a78Upgrade Overview"))
            return;


        event.setCancelled(true);

        if(event.getCurrentItem() == null)
            return;

        if (event.getClick().isRightClick()) {

            UpgradeItem upgradeItem = UpgradeItem.getByStack(removeAdditionalInfo(event.getCurrentItem().clone()));
            if (upgradeItem != null) {
                ItemStack stack = upgradeItem.getStack().clone();
                if(event.isShiftClick())
                    stack.setAmount(stack.getMaxStackSize());


                player.sendMessage(ToolUpgrades.PREFIX + "Gave \u00a7a" + stack.getAmount() + "\u00a77x " + upgradeItem.getStack().getItemMeta().getDisplayName() + " \u00a77to \u00a7b" + player.getDisplayName());

                player.getInventory().addItem(stack);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }
        }

        if (event.getClick().isLeftClick()) {
            UpgradeItem upgradeItem = UpgradeItem.getByStack(removeAdditionalInfo(event.getCurrentItem().clone()));
            if (upgradeItem != null) {
                openRecipeOverview(player, upgradeItem);
            }


        }

    }


    private static ItemStack removeAdditionalInfo(ItemStack stack){

        if(!stack.hasItemMeta() || !stack.getItemMeta().hasLore() || stack.getItemMeta().getLore().size() < 2)
            return stack;

        ItemMeta currentMeta = stack.getItemMeta();
        List<String> currentLore = currentMeta.getLore();
        currentLore.remove(currentLore.size() - 1);
        currentLore.remove(currentLore.size() - 1);
        currentMeta.setLore(currentLore);
        stack.setItemMeta(currentMeta);

        return stack;
    }


    private static void openRecipeOverview(Player player, UpgradeItem upgradeItem) {
        List<UpgradeRecipe> recipes = RecipeManager.getRecipesFor(upgradeItem);

        Inventory inv = Bukkit.createInventory(null, 9 * 3, "\u00a78Recipes - " + upgradeItem.getStack().getItemMeta().getDisplayName());

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
            inv.setItem(i + 18, filler);
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("\u00a7cClose");
        back.setItemMeta(backMeta);

        inv.setItem(22, back);

        for (int i = 0; i < recipes.size(); i++) {

            UpgradeRecipe upgradeRecipe = recipes.get(i);


            ItemStack recipeStack = new ItemStack(Material.PAPER);
            ItemMeta recipeMeta = recipeStack.getItemMeta();
            recipeMeta.setDisplayName("\u00a7b" + upgradeRecipe.getId());

            List<String> recipeLore = new ArrayList<>();
            recipeLore.add("\u00a77Ingredients: ");
            recipeLore.add("\u00a78" + upgradeRecipe.getType().displayName());
            recipeLore.add(" ");
            collectIngredients(upgradeRecipe).forEach((ingredient, amount) -> {
                recipeLore.add("\u00a77" + amount + "x \u00a7a" + capitalizeString(ingredient.name()));
            });

            recipeLore.add("");
            recipeLore.add("\u00a78Right Click to get Ingredients");

            recipeMeta.setLore(recipeLore);
            recipeStack.setItemMeta(recipeMeta);

            inv.setItem(9 + i, recipeStack);
        }

        player.openInventory(inv);
    }


    private static HashMap<Material, Integer> collectIngredients(UpgradeRecipe upgradeRecipe){
        HashMap<Material, Integer> ingredients = new HashMap<>();

        for (Material mat : upgradeRecipe.getIngredients()) {
            if (!ingredients.containsKey(mat))
                ingredients.put(mat, 1);
            else
                ingredients.put(mat, ingredients.get(mat) + 1);
        }

        return ingredients;
    }

    private static String capitalizeString(String string){
        String[] name_parts = string.toLowerCase().split("_");
        for (int j = 0; j < name_parts.length; j++) {
            name_parts[j] = name_parts[j].replaceFirst(String.valueOf(name_parts[j].charAt(0)), String.valueOf(name_parts[j].charAt(0)).toUpperCase());
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (String s : name_parts) {
            if (!nameBuilder.isEmpty())
                nameBuilder.append(" ");

            nameBuilder.append(s);
        }

        return nameBuilder.toString();
    }

    @EventHandler
    public void onRecipeOverviewClick(InventoryClickEvent event) {

        Inventory inv = event.getInventory();
        InventoryView inventoryView = event.getView();

        Player player = (Player) event.getWhoClicked();


        if (!inventoryView.getTitle().startsWith("\u00a78Recipes - "))
            return;


        event.setCancelled(true);

        if(event.getCurrentItem() == null)
            return;

        if(event.getCurrentItem().getType() == Material.BARRIER){
            open(player);
        }

        if (event.getCurrentItem().getType() != Material.PAPER)
            return;

        String id = event.getCurrentItem().getItemMeta().getDisplayName().replace("\u00a7b", "");
        for(UpgradeRecipe recipe : RecipeManager.recipes()){
            if(recipe.getId().equals(id)){

                if(event.isLeftClick())
                    RecipeManager.openRecipeView(player, recipe);
                else {

                    AtomicInteger i = new AtomicInteger();
                    collectIngredients(recipe).forEach((ingredient, amount) -> {
                        i.getAndIncrement();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ToolUpgrades.getInstance(), () -> {
                            player.getInventory().addItem(new ItemStack(ingredient, amount));
                            player.sendMessage(ToolUpgrades.PREFIX + "Gave \u00a7a" + amount + "\u00a77x " + capitalizeString(ingredient.name()) + " \u00a77to \u00a7b" + player.getDisplayName());
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
                        }, i.get());
                    });

                }
                break;
            }
        }
    }
}
