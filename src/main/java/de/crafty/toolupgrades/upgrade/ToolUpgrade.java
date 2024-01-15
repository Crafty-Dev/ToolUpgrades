package de.crafty.toolupgrades.upgrade;

public enum ToolUpgrade {


    MAGNETISM(Type.TOOL_AND_WEAPON, "\u00a79Magnetism"),
    AUTO_SMELTING(Type.TOOL, "\u00a74Auto Smelting"),
    MULTI_MINER(Type.TOOL, "\u00a78Multi Miner"),
    TELEPORTATION(Type.WEAPON, "\u00a73Teleportation"),
    FADELESS(Type.ALL_GEAR, "\u00a76Fadeless"),
    SOFT_FALL(Type.BOOTS, "\u00a7FSoft Fall"),
    ENDER_MASK(Type.HELMET, "\u00a71Ender Mask"),
    MOB_CAPTURE(Type.TOOL_AND_WEAPON, "\u00a75Mob Capture"),
    SILKY(Type.TOOL, "\u00a7FSilky"),
    LIFE_BONUS(Type.ARMOR, "\u00a7cLife Bonus"),
    ENCHANTMENT_RELOCATION(Type.BOOK, "\u00a79\u00a7oEnchantment Relocation"),
    CELESTIAL(Type.ARMOR, "\u00a76\u00a7oCelestial")
    ;


    final Type type;
    final String displayName;
    ToolUpgrade(Type type, String displayName){
        this.type = type;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {

        ALL_GEAR ("All Gear"),
        HELMET ("Helmet"),
        CHESTPLATE ("Chestplate"),
        LEGGINGS ("Leggings"),
        BOOTS ("Boots"),
        TOOL ("Tools"),
        WEAPON ("Weapons"),
        ARMOR ("Armor"),
        TOOL_AND_WEAPON ("Tools & Weapons"),
        BOOK ("Books");


        final String displayName;

        Type(String displayName){
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

}
