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

package com.craftingdead.immerse.game.module.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import com.craftingdead.core.world.entity.extension.PlayerExtension;
import com.craftingdead.immerse.Permissions;
import com.craftingdead.core.world.item.combatslot.CombatSlot;
import com.craftingdead.immerse.game.module.Module;
import com.craftingdead.immerse.game.module.ServerModule;
import com.craftingdead.immerse.game.module.shop.message.BuyItemMessage;
import com.craftingdead.immerse.game.module.shop.message.SyncUserMessage;
import com.craftingdead.immerse.game.network.GameNetworkChannel;
import com.craftingdead.immerse.game.network.MessageHandlerRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.server.permission.PermissionAPI;

public class ServerShopModule extends ShopModule implements ServerModule, Module.Tickable {

  public static final BiConsumer<PlayerExtension<?>, ItemStack> COMBAT_PURCHASE_HANDLER =
      (player, item) -> CombatSlot.getSlotType(item)
          .orElseThrow(() -> new IllegalStateException("Invalid item"))
          .addToInventory(item, player.getEntity().inventory, true);

  private static final MessageHandlerRegistry<ServerShopModule> messageHandlers =
      new MessageHandlerRegistry<>();

  static {
    messageHandlers.register(BuyItemMessage.class, ServerShopModule::handleBuyItem);
  }

  private final Map<UUID, ShopUser> users = new HashMap<>();

  private final BiConsumer<PlayerExtension<?>, ItemStack> purchaseHandler;

  private final int defaultBuyTimeSeconds;

  private int secondTimer;

  public ServerShopModule(BiConsumer<PlayerExtension<?>, ItemStack> purchaseHandler,
      int defaultBuyTimeSeconds) {
    this.purchaseHandler = purchaseHandler;
    this.defaultBuyTimeSeconds = defaultBuyTimeSeconds;
  }

  public void buyItem(PlayerExtension<ServerPlayerEntity> player, UUID itemId) {
    ShopItem item = this.items.get(itemId);
    if (item == null) {
      throw new IllegalArgumentException("Unknown item ID: " + itemId.toString());
    }

    ShopUser user = this.users.get(player.getEntity().getUUID());
    if (user.money >= item.getPrice() && user.buyTimeSeconds != 0) {
      user.money -= item.getPrice();
      user.sync();
      this.purchaseHandler.accept(player, item.getItemStack());
    }
  }

  public void resetBuyTime(UUID playerId) {
    ShopUser user = this.users.get(playerId);
    if (user != null) {
      user.buyTimeSeconds = this.defaultBuyTimeSeconds;
      user.sync();
    }
  }

  private void handleBuyItem(BuyItemMessage message, NetworkEvent.Context context) {
    this.buyItem(PlayerExtension.getOrThrow(context.getSender()), message.getItemId());
  }

  @Override
  public <MSG> void handleMessage(MSG message, NetworkEvent.Context context) {
    messageHandlers.handle(this, message, context);
  }

  @Override
  public void tick() {
    if (this.secondTimer++ >= 20) {
      this.secondTimer = 0;
      for (ShopUser user : this.users.values()) {
        if (user.buyTimeSeconds > 0
            && PermissionAPI.hasPermission(user.gameProfile, Permissions.GAME_OP, null)) {
          user.buyTimeSeconds--;
          user.sync();
        }
      }
    }
  }

  @Override
  public void addPlayer(PlayerExtension<ServerPlayerEntity> player) {
    ShopUser user =
        new ShopUser(player.getEntity().getGameProfile(), player.getEntity().connection.connection);
    this.users.put(player.getEntity().getUUID(), user);
    user.sync();
  }

  @Override
  public void removePlayer(PlayerExtension<ServerPlayerEntity> player) {
    this.users.remove(player.getEntity().getUUID());
  }

  private class ShopUser {

    private final GameProfile gameProfile;
    private final NetworkManager connection;
    private int buyTimeSeconds = ServerShopModule.this.defaultBuyTimeSeconds;
    private int money;

    private ShopUser(GameProfile gameProfile, NetworkManager connection) {
      this.gameProfile = gameProfile;
      this.connection = connection;
    }

    private void sync() {
      this.connection.send(GameNetworkChannel.toVanillaPacket(ServerShopModule.this.getType(),
          new SyncUserMessage(this.buyTimeSeconds, this.money), NetworkDirection.PLAY_TO_CLIENT));
    }
  }
}
