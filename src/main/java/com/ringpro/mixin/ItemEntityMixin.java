package com.ringpro.mixin;

import com.ringpro.access.ItemEntityInterface;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemEntityInterface {
    @Unique private boolean isAcutallyInvulnerable;
    @Unique private  boolean shouldGlow;
    @Unique private final ItemEntity itemEntity = (ItemEntity) (Object) this;


    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void inv(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (getAcutallyInvulnerable()) {
            cir.setReturnValue(false);
        }
    }
    @Inject(at=@At("HEAD"), method="tick()V")
    private void tick(CallbackInfo ci) {
        World world = itemEntity.getWorld();
        if (itemEntity.getY() < -64) {
            if (getAcutallyInvulnerable()) {
                if (!world.isClient) {
                    itemEntity.teleport((ServerWorld) itemEntity.getWorld(),itemEntity.getX(), -60, itemEntity.getZ(), PositionFlag.VALUES,0,0);
                    itemEntity.setVelocity(0, 0, 0);
                    ((ServerWorld)world).getChunkManager().sendToNearbyPlayers(itemEntity, new EntityPositionS2CPacket(itemEntity));
                    ((ServerWorld)world).getChunkManager().sendToNearbyPlayers(itemEntity, new EntityVelocityUpdateS2CPacket(itemEntity));
                }
            }
        }
        itemEntity.setGlowing(getShouldGlow());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void read(NbtCompound nbt, CallbackInfo ci) {
        setAcutallyInvulnerable(nbt.getBoolean("easydeaths:isAcutallyInvulnerable"));
        setShouldGlow(nbt.getBoolean("easydeaths:shouldGlow"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void write(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("easydeaths:isAcutallyInvulnerable", getAcutallyInvulnerable());
        nbt.putBoolean("easydeaths:shouldGlow", getShouldGlow());
    }

    @Override
    public void setAcutallyInvulnerable(boolean bool) {
        this.isAcutallyInvulnerable = bool;
    }

    @Override
    public boolean getAcutallyInvulnerable() {
        return this.isAcutallyInvulnerable;
    }

    @Override
    public void setShouldGlow(boolean bool) {
        this.shouldGlow = bool;
    }

    @Override
    public boolean getShouldGlow() {
        return this.shouldGlow;
    }
}
