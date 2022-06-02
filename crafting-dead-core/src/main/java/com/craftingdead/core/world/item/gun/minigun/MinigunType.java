/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between
 * you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be
 * bound by the terms and conditions of this Agreement as may be revised from time
 * to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download,
 * copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.core.world.item.gun.minigun;

import com.craftingdead.core.world.item.gun.GunType;
import com.craftingdead.core.world.item.gun.TypedGun;
import com.craftingdead.core.world.item.gun.TypedGunClient;
import java.util.function.Function;

public class MinigunType extends GunType {

  protected MinigunType(Builder<?> builder) {
    super(builder);
  }

  @Override
  public <T extends TypedGun<?>> Function<T, TypedGunClient<? super T>> getClientFactory() {
    return MinigunClient::new;
  }

  public static Builder<?> builder() {
    return new Builder<>(MinigunType::new);
  }
}