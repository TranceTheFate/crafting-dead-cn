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

package com.craftingdead.core.client.renderer.item;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.client.renderer.item.model.ModelScarhIS2;
import com.craftingdead.core.world.gun.attachment.Attachment;
import com.craftingdead.core.world.gun.attachment.Attachments;
import com.craftingdead.core.world.gun.type.GunTypes;
import com.craftingdead.core.world.gun.Gun;
import com.craftingdead.core.world.item.ModItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class TrenchgunRenderer extends GunRenderer {

  private final Model ironSight = new ModelScarhIS2();

  public TrenchgunRenderer() {
    super(ModItems.TRENCH_GUN.getId(), GunTypes.TRENCH_GUN);
  }

  @Override
  protected void applyGenericTransforms(Gun gun, MatrixStack matrixStack) {
    matrixStack.translate(-0.3F, -0.3F, 0);
  }

  @Override
  protected void applyThirdPersonTransforms(Gun gun,
      MatrixStack matrixStack) {

    matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-15.0F));
    matrixStack.mulPose(Vector3f.YP.rotationDegrees(77));

    matrixStack.translate(0.5F, -0.75F, 0.4F);

    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(15));
    matrixStack.translate(-0.2F, 0.55F, 0.0F);

    float scale = 0.7F;
    matrixStack.scale(scale, scale, scale);
  }

  @Override
  protected void applyFirstPersonTransforms(Gun gun,
      MatrixStack matrixStack) {

    this.muzzleFlashX = 0.1F;
    this.muzzleFlashY = 0.1F;
    this.muzzleFlashZ = -1.8F;
    this.muzzleScale = 1.2F;

    matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-40.0F));
    matrixStack.mulPose(Vector3f.YP.rotationDegrees(-3.0F));

    matrixStack.translate(0.7F, -0.2F, 0.15F);

    float scale = 0.5F;
    matrixStack.scale(scale, scale, scale);

    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(3.0F));
  }

  @Override
  protected void applyAimingTransforms(Gun gun,
      MatrixStack matrixStack) {

    matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-24.0F));
    matrixStack.mulPose(Vector3f.YP.rotationDegrees(5.1F));

    matrixStack.translate(0.21F, -0.69F, 0.972F);

    if (!gun.hasIronSight()) {

      matrixStack.translate(0.0F, 0.017F, 0.0F);
    }

    float scale = 0.6F;
    matrixStack.scale(scale, scale, scale);

    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-0.7F));
    matrixStack.translate(0F, 0F, 0.0F);
  }

  @Override
  protected void renderAdditionalParts(Gun gun, float partialTicks,
      MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight,
      int packedOverlay) {
    this.renderIronSight(matrixStack, renderTypeBuffer, packedLight, packedOverlay);
  }

  private void renderIronSight(MatrixStack matrixStack,
      IRenderTypeBuffer renderTypeBuffer, int packedLight, int packedOverlay) {
    matrixStack.pushPose();
    {
      matrixStack.translate(1.6F, -0.11F, 0.0315F);
      float scale = 0.49F;
      matrixStack.scale(scale, scale, scale);

      IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(this.ironSight.renderType(
          new ResourceLocation(CraftingDead.ID, "textures/attachment/scarh_is2.png")));
      this.ironSight.renderToBuffer(matrixStack, vertexBuilder, packedLight, packedOverlay, 1.0F,
          1.0F,
          1.0F, 1.0F);
    }
    matrixStack.popPose();
  }

  @Override
  protected void applyWearingTransforms(Gun gun,
      MatrixStack matrixStack) {

    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
    matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
    matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
    float scale = 0.45F;
    matrixStack.scale(scale, scale, scale);
    matrixStack.translate(-0.7F, 0.18F, 0.4F);
  }

  @Override
  protected void applyMagazineTransforms(ItemStack itemStack,
      MatrixStack matrixStack) {}

  @Override
  protected void applyAttachmentTransforms(Attachment attachment,
      MatrixStack matrixStack) {
    if (attachment == Attachments.TACTICAL_GRIP.get()) {
      matrixStack.translate(7D, 3.3D, 1D);
      float scale = 1F;
      matrixStack.scale(scale, scale, scale);
      return;
    }
  }

  @Override
  protected void applyHandTransforms(Gun gun,
      boolean rightHand, MatrixStack matrixStack) {
    if (rightHand) {
      matrixStack.translate(-0.1F, -0.15F, -0.3F);
    } else {
      matrixStack.translate(0.01F, 0.15F, -0.1F);
    }
  }

  @Override
  protected void applySprintingTransforms(MatrixStack matrixStack, float pct) {
    matrixStack.mulPose(Vector3f.YP.rotationDegrees(pct * -40));
    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(pct * 10));
    matrixStack.translate(pct * 1F, 0.0F, pct * 0.75F);
  }
}
