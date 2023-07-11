package com.ringpro.mixin;

import com.ringpro.EasyDeathsMod;
import com.ringpro.access.ItemEntityInterface;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Final @Shadow private List<DefaultedList<ItemStack>> combinedInventory;

    @Inject(method = "dropAll", at = @At(value = "HEAD"), cancellable = true)
    private void dropAllInject(CallbackInfo ci) {

        for (List<ItemStack> list : combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);
                if (!itemStack.isEmpty()) {
                    ItemEntity itemEntity = ((PlayerInventory) (Object) this).player.dropItem(itemStack, true, false);
                    list.set(i, ItemStack.EMPTY);

                    if (itemEntity != null) {
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
                    }
                }
            }
        }
        ci.cancel();
    }

}
