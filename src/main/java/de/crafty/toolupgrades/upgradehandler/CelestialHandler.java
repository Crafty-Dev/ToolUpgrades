package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class CelestialHandler implements Listener {


    @EventHandler
    public void onCelestialUpdate$0(PlayerInteractEvent event) {
        this.checkArmor(event.getPlayer());
    }

    @EventHandler
    public void onCelestialUpdate$1(BlockDispenseArmorEvent event) {
        if (event.getTargetEntity() instanceof Player player)
            this.checkArmor(player);
    }

    @EventHandler
    public void onCelestialUpdate$2(InventoryClickEvent event) {
        if (!(event.getSlotType() == InventoryType.SlotType.CONTAINER && event.getWhoClicked().getGameMode() == GameMode.CREATIVE))
            this.checkArmor((Player) event.getWhoClicked());
    }


    @EventHandler
    public void onCelestialUpdate$3(PlayerItemBreakEvent event) {
        if (ToolManager.hasUpgrade(event.getBrokenItem(), ToolUpgrade.CELESTIAL))
            this.checkArmor(event.getPlayer());
    }

    @EventHandler
    public void onCelestialUpdate$4(PlayerRespawnEvent event) {
        this.checkArmor(event.getPlayer());
    }

    @EventHandler
    public void onCelestialUpdate$5(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player)
            this.checkArmor(player);
    }


    private void checkArmor(Player player) {

        PlayerInventory playerInv = player.getInventory();

        ItemStack oldHelmet = playerInv.getHelmet();
        ItemStack oldChestplate = playerInv.getChestplate();
        ItemStack oldLeggings = playerInv.getLeggings();
        ItemStack oldBoots = playerInv.getBoots();

        int prevPieces = ToolManager.getCelestialPieces(player);

        ToolUpgrades.runPostEventTask(() -> {

            ItemStack helmet = playerInv.getHelmet();
            ItemStack chestplate = playerInv.getChestplate();
            ItemStack leggings = playerInv.getLeggings();
            ItemStack boots = playerInv.getBoots();

            if ((helmet != null && helmet.equals(oldHelmet)) && (chestplate != null && chestplate.equals(oldChestplate)) && (leggings != null && leggings.equals(oldLeggings)) && (boots != null && boots.equals(oldBoots)))
                return;

            int pieces = 0;

            if (ToolManager.hasUpgrade(helmet, ToolUpgrade.CELESTIAL))
                pieces++;
            if (ToolManager.hasUpgrade(chestplate, ToolUpgrade.CELESTIAL))
                pieces++;
            if (ToolManager.hasUpgrade(leggings, ToolUpgrade.CELESTIAL))
                pieces++;
            if (ToolManager.hasUpgrade(boots, ToolUpgrade.CELESTIAL))
                pieces++;

            for (ItemStack stack : playerInv.getArmorContents()) {
                if (ToolManager.hasUpgrade(stack, ToolUpgrade.CELESTIAL))
                    ToolManager.addCelestialData(stack, pieces);
            }

            for (ItemStack stack : playerInv.getStorageContents()) {
                ToolManager.removeCelestialData(stack);
            }

            ToolManager.removeCelestialData(playerInv.getItemInOffHand());
            ToolManager.removeCelestialData(player.getItemOnCursor());

            if (pieces == 0) {
                ToolManager.removeCelestialData(player);
                return;
            }

            ToolManager.setCelestialData(player, pieces);

            if (prevPieces < pieces) {
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.5F);


                World world = player.getWorld();

                int particles = 12;
                double distance = 0.5D;
                int layers = 5;
                for (int layer = 0; layer < layers; layer++) {
                    for (int i = 0; i < particles; i++) {
                        Location loc = player.getLocation().clone();
                        Vector rotationVec = new Vector(1, 0, 1).normalize();
                        rotationVec.rotateAroundY(i * (360.0D / particles));

                        loc.add(new Vector(distance, 0, distance).multiply(rotationVec));
                        loc.add(0, layer * (player.getHeight() / (layers - 1)), 0);
                        world.spawnParticle(Particle.REDSTONE, loc, 100, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, 148, 0), 0.35F));
                    }
                }
            }

            if (prevPieces != pieces) {
                String color = pieces == 4 ? "\u00a7a" : "\u00a77";

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a77Celestial Pieces: " + color + pieces + "\u00a77/\u00a764"));
            }

            player.setAllowFlight(ToolManager.getCelestialPieces(player) == 4);
        });

    }


    @EventHandler
    public void onCelestial$6(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (!ToolUpgrades.getInstance().celestialDrainXP())
            return;

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())
            return;

        if (!(ToolManager.getCelestialPieces(player) == 4 && player.isFlying() && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR))
            return;

        long currentXP = (long) (player.getExpToLevel() * player.getExp());

        if (currentXP == 0 && player.getLevel() == 0) {
            player.setFlying(false);
            return;
        }

        NamespacedKey key = new NamespacedKey(ToolUpgrades.getInstance(), "celestialMovementDelta");
        double deltaMovement = player.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE) ? player.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE) : 0.0D;
        deltaMovement += from.distance(to);

        while (deltaMovement >= 1.0D / ToolUpgrades.getInstance().celestialXPToDrain()) {
            if (currentXP == 0) {
                player.setLevel(player.getLevel() - 1);
                currentXP = player.getExpToLevel();
            }

            currentXP -= 1;
            player.setExp((float) currentXP / player.getExpToLevel());
            deltaMovement -= 1.0D / ToolUpgrades.getInstance().celestialXPToDrain();
        }

        player.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, deltaMovement);

    }

    @EventHandler
    public void onCelestial$7(PlayerToggleFlightEvent event) {

        Player player = event.getPlayer();

        if (ToolManager.getCelestialPieces(player) != 4)
            return;

        player.getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "celestialFlying"), PersistentDataType.BOOLEAN, event.isFlying());

        if (!ToolUpgrades.getInstance().celestialDrainXP())
            return;


        if (player.getGameMode() == GameMode.CREATIVE || !event.isFlying())
            return;

        long currentXP = (long) (player.getExpToLevel() * player.getExp());
        if (currentXP == 0 && player.getLevel() == 0)
            event.setCancelled(true);

    }

    @EventHandler
    public void onCelestial$8(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        World world = player.getWorld();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (!ToolUpgrades.getInstance().celestialFallDamage())
            return;

        if (player.getFallDistance() == 0.0F || world.getBlockAt(to.getBlockX(), to.getBlockY() - 1, to.getBlockZ()).isPassable())
            return;

        if (ToolManager.getCelestialPieces(player) == 4)
            player.setAllowFlight(false);

        ToolUpgrades.runPostEventTask(() -> {
            if (ToolManager.getCelestialPieces(player) == 4)
                player.setAllowFlight(true);
        });
    }


    @EventHandler
    public void onCelestial$9(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        NamespacedKey key = new NamespacedKey(ToolUpgrades.getInstance(), "celestialFlying");

        if (ToolManager.getCelestialPieces(player) == 4 && player.isFlying())
            player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);


    }

    @EventHandler
    public void onCelestial$10(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        NamespacedKey key = new NamespacedKey(ToolUpgrades.getInstance(), "celestialFlying");
        boolean wasFlying = player.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN) ? player.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN) : false;

        if (ToolManager.getCelestialPieces(player) != 4)
            return;

        player.setAllowFlight(true);
        player.setFlying(wasFlying);
    }


    @EventHandler
    public void onCelestial$11(PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();

        if (ToolManager.getCelestialPieces(player) != 4)
            return;

        NamespacedKey key = new NamespacedKey(ToolUpgrades.getInstance(), "celestialFlying");
        boolean wasFlying = player.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN) ? player.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN) : false;

        if ((player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) && (event.getNewGameMode() == GameMode.SURVIVAL || event.getNewGameMode() == GameMode.ADVENTURE)) {
            ToolUpgrades.runPostEventTask(() -> {
                player.setAllowFlight(true);
                player.setFlying(wasFlying);
            });
        }

        if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR)) {
            player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, player.isFlying());
        }


    }

}
