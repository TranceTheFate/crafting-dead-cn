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

package com.craftingdead.immerse.client.gui.view;

import com.craftingdead.immerse.CraftingDeadImmerse;
import com.craftingdead.immerse.client.gui.view.layout.Layout;
import com.craftingdead.immerse.client.util.FitType;
import com.craftingdead.immerse.client.util.RenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

public class FogView<L extends Layout> extends View<FogView<L>, L> {

  private static final ResourceLocation SMOKE_TEXTURE =
      new ResourceLocation(CraftingDeadImmerse.ID, "textures/gui/smoke.png");

  private static long fogStartTime = Util.getMillis();

  public FogView(L layout) {
    super(layout);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float paritalTicks) {
    Vector2f fogSize =
        FitType.COVER.getSize(1920, 1080, this.getScaledContentWidth(),
            this.getScaledContentHeight());
    float fogWidth = fogSize.x;
    float fogHeight = fogSize.y;

    final float pct =
        MathHelper.clamp((Util.getMillis() - fogStartTime) / (1000.0F * 100.0F * 2.0F), 0.0F, 1.0F);
    if (pct == 1.0F) {
      fogStartTime = Util.getMillis();
    }
    

    matrixStack.pushPose();
    {
      matrixStack.scale(4F, 4F, 4F);
      RenderSystem.enableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.3F * this.getAlpha());

      RenderUtil.bind(SMOKE_TEXTURE);

      final float smokeX = this.getScaledX() + pct * this.getContentWidth();

      RenderUtil.blit(matrixStack, smokeX, this.getScaledContentY(), fogWidth, fogHeight);
      RenderUtil.blit(matrixStack, smokeX - fogWidth, this.getScaledContentY(), fogWidth,
          fogHeight);

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      RenderSystem.disableBlend();
    }
    matrixStack.popPose();
  }
}
