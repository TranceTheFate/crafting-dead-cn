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

package com.craftingdead.survival.world.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

public class TankZombieEntity extends AdvancedZombieEntity {

  public TankZombieEntity(EntityType<? extends TankZombieEntity> type, World world) {
    super(type, world);
  }

  public static AttributeModifierMap.MutableAttribute registerAttributes() {
    return AdvancedZombieEntity.createAttributes()
        .add(Attributes.FOLLOW_RANGE, 20.0D)
        .add(Attributes.MAX_HEALTH, 100.0D)
        .add(Attributes.ATTACK_DAMAGE, 15.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.19D)
        .add(Attributes.ATTACK_SPEED, 2.0D);
  }
}
