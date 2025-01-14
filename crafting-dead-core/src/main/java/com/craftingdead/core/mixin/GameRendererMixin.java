/*
 * Crafting Dead
 * Copyright (C) 2021  NexusNode LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.craftingdead.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.craftingdead.core.capability.Capabilities;
import com.craftingdead.core.world.inventory.ModEquipmentSlotType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.LivingEntity;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

  /**
   * Prevents night vision flicker due to vanilla basing the scale off of duration.
   */
  @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
  private static void getNightVisionScale(LivingEntity livingEntity, float partialTicks,
      CallbackInfoReturnable<Float> callbackInfo) {
    // It's faster not flat-mapping or filtering (we want to be fast in a render method)
    livingEntity.getCapability(Capabilities.LIVING_EXTENSION)
        .ifPresent(l -> l.getItemHandler().getStackInSlot(ModEquipmentSlotType.HAT.getIndex())
            .getCapability(Capabilities.HAT).ifPresent(hat -> {
              if (hat.hasNightVision()) {
                callbackInfo.setReturnValue(1.0F);
              }
            }));

  }
}
