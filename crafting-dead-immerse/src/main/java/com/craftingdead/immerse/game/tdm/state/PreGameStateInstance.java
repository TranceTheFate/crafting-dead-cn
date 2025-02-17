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

package com.craftingdead.immerse.game.tdm.state;

import com.craftingdead.immerse.game.GameUtil;
import com.craftingdead.immerse.game.tdm.TdmServer;
import com.craftingdead.immerse.game.tdm.TdmTeam;
import com.craftingdead.immerse.sounds.ImmerseSoundEvents;
import com.craftingdead.immerse.util.state.State;
import com.craftingdead.immerse.util.state.TimedStateInstance;

public class PreGameStateInstance extends TimedStateInstance<TdmServer> {

  public PreGameStateInstance(State<?> state, TdmServer context) {
    super(state, context, context.getPreGameDuration());
  }

  @Override
  public boolean tick() {
    boolean redEmpty =
        this.getContext().getTeamModule().getTeamInstance(TdmTeam.RED).getMembers().isEmpty();
    boolean blueEmpty =
        this.getContext().getTeamModule().getTeamInstance(TdmTeam.BLUE).getMembers().isEmpty();
    if (redEmpty || blueEmpty) {
      return false;
    }

    if (super.tick()) {
      this.getContext().setMovementBlocked(false);
      this.getContext().resetBuyTimes();
      this.getContext().resetPlayerData();
      this.getContext().resetTeams();
      this.getContext().getLogicalServer()
          .respawnPlayers(playerEntity -> !playerEntity.isSpectator(), false);
      GameUtil.broadcastSound(ImmerseSoundEvents.START_MUSIC.get(),
          this.getContext().getMinecraftServer());
      return true;
    }

    if (this.getContext().getMinecraftServer().getPlayerCount() > 5
        && this.getTimeRemainingSeconds() > 16) {
      this.setTimeRemainingSeconds(15);
    }

    if (this.getTimeRemainingSeconds() <= 15 && this.hasSecondPast()) {
      GameUtil.broadcastSound(ImmerseSoundEvents.COUNTDOWN.get(),
          this.getContext().getMinecraftServer());
    }

    if (this.getTimeRemainingSeconds() <= 5) {
      this.getContext().setMovementBlocked(true);
    }

    return false;
  }
}
