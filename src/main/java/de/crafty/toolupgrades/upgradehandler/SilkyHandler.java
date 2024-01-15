package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class SilkyHandler implements Listener {


    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {


        Player player = event.getPlayer();
        Block block = event.getBlock();

        ItemStack usedStack = player.getInventory().getItemInMainHand();


        if (block.getType() != Material.SPAWNER || !ToolManager.hasUpgrade(usedStack, ToolUpgrade.SILKY))
            return;

        if (usedStack.getItemMeta() instanceof Damageable meta && player.getGameMode() != GameMode.CREATIVE) {

            if (meta.getDamage() > usedStack.getType().getMaxDurability() - 499) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a7cThis Tool does not have enough durability"));
                event.setCancelled(true);
                return;
            }

            meta.setDamage(meta.getDamage() + 499);
            usedStack.setItemMeta(meta);

        }

        World world = player.getWorld();


        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        ItemStack spawnerStack = new ItemStack(Material.SPAWNER);
        boolean isEmpty = spawner.getSpawnedType() == null;

        ItemMeta meta = spawnerStack.getItemMeta();
        meta.setDisplayName("\u00a75Spawner \u00a77(\u00a7b" + (!isEmpty ? spawner.getSpawnedType().name().toLowerCase() + "\u00a77)" : "Empty\u00a77)"));
        meta.getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "type"), PersistentDataType.STRING, !isEmpty ? spawner.getSpawnedType().name() : "");
        spawnerStack.setItemMeta(meta);

        world.dropItemNaturally(block.getLocation(), spawnerStack);

        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {

                    world.spawnParticle(Particle.REDSTONE, new Location(world, block.getX() + (x * 0.5D), block.getY() + (y * 0.5D), block.getZ() + (z * 0.5D)), 100, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(140, 0, 255), 0.75F));

                }
            }

        }

        event.setExpToDrop(0);
    }


    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();


        if (block.getType() != Material.SPAWNER)
            return;


        if (!event.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "type"), PersistentDataType.STRING))
            return;

        CreatureSpawner spawner = (CreatureSpawner) block.getState();

        if (!event.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "type"), PersistentDataType.STRING).equals("")) {
            spawner.setSpawnedType(EntityType.valueOf(event.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "type"), PersistentDataType.STRING)));
            spawner.update();
        }
    }


    @EventHandler
    public void onDragonEggBreak(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null)
            return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (block.getType() != Material.DRAGON_EGG || !ToolManager.hasUpgrade(usedStack, ToolUpgrade.SILKY))
            return;


        World world = player.getWorld();


        ItemStack eggStack = new ItemStack(Material.DRAGON_EGG);

        if (ToolManager.hasUpgrade(usedStack, ToolUpgrade.MAGNETISM)) {

            HashMap<Integer, ItemStack> map = player.getInventory().addItem(eggStack);
            if (map.size() > 0) {
                Item item = world.dropItem(player.getLocation(), eggStack);
                item.setVelocity(player.getVelocity());

            }

        }

        if (!ToolManager.hasUpgrade(usedStack, ToolUpgrade.MAGNETISM))
            world.dropItemNaturally(block.getLocation(), eggStack);


        world.setBlockData(block.getLocation(), Material.AIR.createBlockData());

        player.playSound(block.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);

        event.setCancelled(true);
    }

}
