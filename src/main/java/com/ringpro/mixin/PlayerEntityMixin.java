package com.ringpro.mixin;

import com.ringpro.EasyDeathsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    @Shadow @Final private static Logger LOGGER;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyArgs(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V", ordinal = 0)
    )
    private void multiplySpread(Args args){
        double spreadMultiplier = ((ServerWorld)this.getEntityWorld()).getGameRules().get(EasyDeathsMod.DEATH_ITEMS_SPREAD_AMOUNT).get();
        for(int i = 0; i < args.size(); i++) args.set(i, (double)args.get(i)*spreadMultiplier);
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(value = "TAIL", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V"))
    private void injected(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> itemEntity)
    {
        try {
            boolean isInvulnerable = ((ServerWorld)itemEntity.getReturnValue().getEntityWorld()).getGameRules().get(EasyDeathsMod.DEATH_ITEMS_INVULNERABLE).get();
            boolean itemNeverDespawn = ((ServerWorld)itemEntity.getReturnValue().getEntityWorld()).getGameRules().get(EasyDeathsMod.DEATH_ITEMS_NEVER_DESPAWN).get();

            if (itemNeverDespawn)
            {
                itemEntity.getReturnValue().setNeverDespawn();
            }
            if (isInvulnerable) {
                itemEntity.getReturnValue().setInvulnerable(isInvulnerable);
            }
        }
        catch (Exception e) {
            LOGGER.error("FALUIRE! " + e.getLocalizedMessage());
        }
    }
}