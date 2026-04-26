package com.navaronee.meuprimeiromod.item;

import com.navaronee.meuprimeiromod.entity.SlimeShotEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Slime Gun — bazooka. Comportamento estilo MrCrayfish Gun Mod:
 *  - Click direito = dispara imediato (sem charge/hold)
 *  - Consome 1 ammo_slime do inventário
 *  - Cooldown de 4s atua como "reload" — overlay escuro do vanilla mostra o tempo
 *  - Recoil forte empurra o player pra trás
 *  - Tiro explode no impacto SEM quebrar bloco
 */
public class SlimeGunItem extends Item {

    public static final int COOLDOWN_TICKS = 80;          // 4s reload
    public static final float SHOT_VELOCITY = 1.8F;
    public static final float RECOIL_HORIZONTAL = 0.85F;  // recuo bazooka
    public static final float RECOIL_VERTICAL = 0.22F;

    public SlimeGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!player.getAbilities().instabuild && !consumeAmmo(player)) {
            level.playSound(null, player.blockPosition(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.7F, 1.2F);
            return InteractionResultHolder.fail(stack);
        }

        // Sons de disparo (bazooka feel)
        level.playSound(null, player.blockPosition(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.7F, 1.6F);
        level.playSound(null, player.blockPosition(),
                SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 1.4F, 0.5F);

        // Spawna projétil server-side
        if (!level.isClientSide()) {
            Vec3 look = player.getLookAngle();
            SlimeShotEntity shot = new SlimeShotEntity(
                    level, player, look.x, look.y, look.z);
            shot.setPos(player.getX() + look.x * 0.7,
                        player.getEyeY() - 0.15,
                        player.getZ() + look.z * 0.7);
            shot.setDeltaMovement(look.scale(SHOT_VELOCITY));
            level.addFreshEntity(shot);
        }

        // Recoil — empurra o player pra trás
        Vec3 look = player.getLookAngle();
        player.push(-look.x * RECOIL_HORIZONTAL,
                    RECOIL_VERTICAL,
                    -look.z * RECOIL_HORIZONTAL);
        player.hurtMarked = true;

        // Cooldown = "reload" da arma. Overlay escuro do vanilla na hotbar.
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // Durabilidade
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

        return InteractionResultHolder.success(stack);
    }

    private static boolean consumeAmmo(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.AMMO_SLIME.get())) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }
}
