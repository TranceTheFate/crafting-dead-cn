/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be bound by the terms and conditions of this Agreement as may be revised from time to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download, copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.core.event;

import java.lang.reflect.Type;
import com.craftingdead.core.world.action.Action;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IGenericEvent;

public abstract class LivingExtensionEvent extends Event {

  private final LivingExtension<?, ?> living;

  public LivingExtensionEvent(LivingExtension<?, ?> living) {
    this.living = living;
  }

  public LivingExtension<?, ?> getLiving() {
    return this.living;
  }

  public static class Load extends LivingExtensionEvent {

    public Load(LivingExtension<?, ?> living) {
      super(living);
    }
  }

  @Cancelable
  public static class PerformAction<T extends Action> extends LivingExtensionEvent
      implements IGenericEvent<T> {

    private final T action;

    public PerformAction(LivingExtension<?, ?> living, T action) {
      super(living);
      this.action = action;
    }

    public T getAction() {
      return this.action;
    }

    @Override
    public Type getGenericType() {
      return this.action.getClass();
    }
  }
}
