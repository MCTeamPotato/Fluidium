package me.kall.fluidium.common.mixin.tracker;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "setPosRaw", at = @At("HEAD")) protected void beforePosChange(CallbackInfo ci) {}
    @Inject(method = "setPosRaw", at = @At("TAIL")) protected void afterPosChange(CallbackInfo ci) {}
}
