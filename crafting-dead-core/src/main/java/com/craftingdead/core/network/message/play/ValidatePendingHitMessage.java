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

package com.craftingdead.core.network.message.play;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import com.craftingdead.core.capability.Capabilities;
import com.craftingdead.core.world.entity.extension.EntitySnapshot;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.core.world.gun.PendingHit;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ValidatePendingHitMessage {

  private final Map<Integer, Collection<PendingHit>> hits;

  public ValidatePendingHitMessage(Map<Integer, Collection<PendingHit>> hits) {
    this.hits = hits;
  }

  public void encode(PacketBuffer out) {
    out.writeVarInt(this.hits.size());
    for (Map.Entry<Integer, Collection<PendingHit>> hit : this.hits.entrySet()) {
      out.writeVarInt(hit.getKey());
      out.writeVarInt(hit.getValue().size());
      for (PendingHit value : hit.getValue()) {
        out.writeByte(value.getTickOffset());
        value.getPlayerSnapshot().write(out);
        value.getHitSnapshot().write(out);
        out.writeVarLong(value.getRandomSeed());
      }
    }
  }

  public static ValidatePendingHitMessage decode(PacketBuffer in) {
    final int hitsSize = in.readVarInt();
    Map<Integer, Collection<PendingHit>> hits = new Int2ObjectLinkedOpenHashMap<>();
    for (int i = 0; i < hitsSize; i++) {
      int key = in.readVarInt();
      int valueSize = in.readVarInt();
      Collection<PendingHit> value = new ObjectArrayList<PendingHit>();
      for (int j = 0; j < valueSize; j++) {
        value.add(new PendingHit(in.readByte(), EntitySnapshot.read(in), EntitySnapshot.read(in),
            in.readVarLong()));
      }
      hits.put(key, value);
    }
    return new ValidatePendingHitMessage(hits);
  }

  public boolean handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      PlayerExtension<ServerPlayerEntity> extension = PlayerExtension.getOrThrow(player);
      extension.getMainHandGun().ifPresent(gun -> {
        for (Map.Entry<Integer, Collection<PendingHit>> hit : this.hits.entrySet()) {
          Optional.ofNullable(player.level.getEntity(hit.getKey()))
              .flatMap(entity -> entity.getCapability(Capabilities.LIVING_EXTENSION).resolve())
              .ifPresent(hitLiving -> {
                for (PendingHit value : hit.getValue()) {
                  gun.validatePendingHit(extension, hitLiving, value);
                }
              });
        }
      });
    });
    return true;
  }
}
