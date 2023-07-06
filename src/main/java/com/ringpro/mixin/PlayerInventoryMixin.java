package com.ringpro.mixin;

import com.ringpro.EasyDeathsMod;
import com.ringpro.access.ItemEntityInterface;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Redirect(method = "dropAll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
    private ItemEntity droppedItem(PlayerEntity player, ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        ItemEntity itemEntity = player.dropItem(stack, throwRandomly, retainOwnership);

        if (itemEntity == null)
            return null;

        boolean isInvulnerable = itemEntity.getEntityWorld().getGameRules().get(EasyDeathsMod.DEATH_ITEMS_INVULNERABLE).get();
        boolean itemNeverDespawn = itemEntity.getEntityWorld().getGameRules().get(EasyDeathsMod.DEATH_ITEMS_NEVER_DESPAWN).get();
        boolean itemShouldGlow = itemEntity.getEntityWorld().getGameRules().get(EasyDeathsMod.DEATH_ITEMS_GLOW).get();

        if (itemNeverDespawn) {
            itemEntity.setNeverDespawn();
        }
        if (isInvulnerable) {
            ((ItemEntityInterface) itemEntity).setAcutallyInvulnerable(true);
        }
        if (itemShouldGlow) {
            ((ItemEntityInterface) itemEntity).setShouldGlow(true);
        }
        return itemEntity;
    }
}
