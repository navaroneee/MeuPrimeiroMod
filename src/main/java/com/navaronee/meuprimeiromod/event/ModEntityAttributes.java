package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.ModEntities;
import com.navaronee.meuprimeiromod.entity.MutantEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Registro dos attributes (HP, speed, damage, etc) das entities custom.
 * Evento é disparado no MOD bus, durante a inicialização.
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        // RadioactiveBee herda os attributes da Bee vanilla
        event.put(ModEntities.RADIOACTIVE_BEE.get(), Bee.createAttributes().build());
        // RadioactiveSlime usa os attributes padrão de Monster (Slime vanilla não tem helper próprio)
        event.put(ModEntities.RADIOACTIVE_SLIME.get(), Monster.createMonsterAttributes().build());
        // Mutant — boss radioativo
        event.put(ModEntities.MUTANT.get(), MutantEntity.createAttributes().build());
    }
}
