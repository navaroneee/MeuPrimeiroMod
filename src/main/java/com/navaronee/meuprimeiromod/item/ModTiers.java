package com.navaronee.meuprimeiromod.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Tiers customizados pras armas/ferramentas do mod.
 *
 * KNIGHT — mais forte que netherite:
 *  - Damage bonus 5 (vs 4 do netherite)
 *  - Speed 10 (vs 9)
 *  - Durability 2500 (vs 2031)
 *  - Enchantability 18 (vs 15)
 *  - Level 4 (mesmo do netherite — mina os mesmos blocos)
 *  - Repair: refined_cesium (item end-game)
 */
public class ModTiers {

    public static final Tier KNIGHT = new Tier() {
        @Override public int getUses() { return 2500; }
        @Override public float getSpeed() { return 10.0F; }
        @Override public float getAttackDamageBonus() { return 5.0F; }
        @Override public int getLevel() { return 4; }
        @Override public int getEnchantmentValue() { return 18; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.REFINED_CESIUM.get());
        }
    };
}
