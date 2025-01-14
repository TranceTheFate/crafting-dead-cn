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

package com.craftingdead.core.world.item.combatslot;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.craftingdead.core.capability.Capabilities;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum CombatSlot implements CombatSlotProvider, IStringSerializable {

  PRIMARY("primary", true),
  SECONDARY("secondary", true),
  MELEE("melee", false),
  GRENADE("grenade", false) {
    @Override
    protected int getAvailableSlot(PlayerInventory playerInventory, boolean ignoreEmpty) {
      int index = super.getAvailableSlot(playerInventory, false);
      return index == -1 ? 3 : index;
    }
  },
  EXTRA("extra", false);

  public static final Codec<CombatSlot> CODEC =
      IStringSerializable.fromEnum(CombatSlot::values, CombatSlot::byName);
  private static final Map<String, CombatSlot> BY_NAME = Arrays.stream(values())
      .collect(Collectors.toMap(CombatSlot::getSerializedName, Function.identity()));

  private final String name;
  private final boolean dropExistingItems;

  private CombatSlot(String name, boolean dropExistingItems) {
    this.name = name;
    this.dropExistingItems = dropExistingItems;
  }

  @Override
  public String getSerializedName() {
    return this.name;
  }

  @Override
  public CombatSlot getCombatSlot() {
    return this;
  }

  protected int getAvailableSlot(PlayerInventory playerInventory, boolean ignoreEmpty) {
    for (int i = 0; i < 6; i++) {
      if ((ignoreEmpty || playerInventory.getItem(i).isEmpty()) && getSlotType(i) == this) {
        return i;
      }
    }
    return -1;
  }

  public boolean addToInventory(ItemStack itemStack, PlayerInventory playerInventory,
      boolean ignoreEmpty) {
    int index = this.getAvailableSlot(playerInventory, ignoreEmpty);
    if (index == -1) {
      return false;
    }
    if (this.dropExistingItems && !playerInventory.getItem(index).isEmpty()) {
      ItemStack oldStack = playerInventory.removeItemNoUpdate(index);
      playerInventory.player.drop(oldStack, true, true);
    }
    playerInventory.setItem(index, itemStack);
    return true;
  }

  public static Optional<CombatSlot> getSlotType(ItemStack itemStack) {
    return itemStack.getCapability(Capabilities.COMBAT_SLOT_PROVIDER)
        .map(CombatSlotProvider::getCombatSlot);
  }

  public boolean isItemValid(ItemStack itemStack) {
    return itemStack.isEmpty() || getSlotType(itemStack)
        .map(this::equals)
        .orElse(false);
  }

  public static boolean isInventoryValid(PlayerInventory inventory) {
    for (int i = 0; i < 7; i++) {
      if (!CombatSlot
          .isItemValidForSlot(inventory.getItem(i), i)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isItemValidForSlot(ItemStack itemStack, int slot) {
    return getSlotType(slot).isItemValid(itemStack);
  }

  public static CombatSlot getSlotType(int slot) {
    switch (slot) {
      case 0:
        return PRIMARY;
      case 1:
        return SECONDARY;
      case 2:
        return MELEE;
      case 3:
      case 4:
      case 5:
        return GRENADE;
      case 6:
        return EXTRA;
      default:
        throw new IllegalArgumentException("Invalid slot");
    }
  }

  public static CombatSlot byName(String name) {
    return BY_NAME.get(name);
  }
}
