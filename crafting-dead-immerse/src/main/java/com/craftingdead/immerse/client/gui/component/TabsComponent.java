/*
 * Crafting Dead
 * Copyright (C)  2021  Nexus Node
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

package com.craftingdead.immerse.client.gui.component;

import com.craftingdead.core.util.Text;
import com.craftingdead.immerse.client.gui.component.event.TabChangeEvent;
import com.craftingdead.immerse.client.gui.component.type.FlexDirection;
import com.craftingdead.immerse.client.gui.component.type.FlexWrap;
import com.craftingdead.immerse.client.util.RenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TabsComponent extends ParentComponent<TabsComponent> {

  private List<Tab> tabList = new ArrayList<>();
  private Tab selectedTab = null;
  private float tabWidth = 60f;
  private float tabHeight = 20f;
  private boolean init = false;

  public TabsComponent() {
    this.setFlexDirection(FlexDirection.ROW);
    this.setFlexWrap(FlexWrap.WRAP);
  }

  /**
   * If called after adding this component as a child,
   */
  public TabsComponent addTab(Tab tab) {
    tabList.add(tab);
    return this;
  }

  @Override
  protected void layout() {
    init();
    super.layout();
  }

  public void init() {
    if (init) {
      return;
    }
    init = true;
    Tab newSelectedTab = null;
    for (Tab tab : tabList) {
      tab.setWidth(tabWidth);
      float y = (tabHeight - this.minecraft.fontRenderer.FONT_HEIGHT) / 2F;
      tab.setTopPadding(y);
      tab.setBottomPadding(y);
      this.addChild(tab);
      if (tab.isSelected() && selectedTab == null) {
        newSelectedTab = tab;
      } else if (tab.isSelected()) {
        tab.setSelected(false);
      }
      tab.layout();
      tab.addActionListener(tab1 -> changeTab((Tab) tab1));
    }

    if (newSelectedTab != null) {
      changeTab(newSelectedTab);
    } else if (tabList.size() > 0){
      changeTab(tabList.get(0));
    }
  }

  private void changeTab(Tab newTab) {
    Tab previousTab = selectedTab;
    selectedTab = newTab;
    if (previousTab != newTab) {
      if (previousTab != null) {
        previousTab.setSelected(false);
      }
      newTab.setSelected(true);
      this.post(new TabChangeEvent(newTab));
    }
  }

  public void addTabChangeListener(Consumer<Tab> listener) {
    this.addListener(TabChangeEvent.class, (tabsComponent, tabChangeEvent) -> {
      listener.accept(tabChangeEvent.getTab());
    });
  }

  public static class Tab extends TextBlockComponent {
    public static final Colour DEFAULT_UNDERSCORE_COLOR = Colour.WHITE;
    public static final double DEFAULT_UNDERSCORE_HEIGHT = 2.5D;
    public static final int DEFAULT_UNDERSCORE_OFFSET = 1;
    public static final boolean DEFAULT_DISABLED = false;
    public static final boolean DEFAULT_SHADOW = false;
    public static final boolean DEFAULT_CENTERED = true;

    private boolean selected = false;
    private boolean hovered = false;

    private Colour underscoreColor;
    private double underscoreHeight;
    private boolean disabled;
    private float underscoreYOffset;

    public Tab(ITextComponent text) {
      super(text);
      this.underscoreColor = DEFAULT_UNDERSCORE_COLOR;
      this.underscoreHeight = DEFAULT_UNDERSCORE_HEIGHT;
      this.underscoreYOffset = DEFAULT_UNDERSCORE_OFFSET;
      this.disabled = DEFAULT_DISABLED;
      this.setShadow(DEFAULT_SHADOW);
      this.setCentered(DEFAULT_CENTERED);
    }

    public Tab(String text) {
      this(Text.of(text));
    }

    @Override
    protected void mouseEntered(double mouseX, double mouseY) {
      super.mouseEntered(mouseX, mouseY);
      this.hovered = true;
    }

    @Override
    protected void mouseLeft(double mouseX, double mouseY) {
      super.mouseLeft(mouseX, mouseY);
      this.hovered = false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);

      if (this.selected) {
        RenderUtil.fill(this.getScaledX(), this.getScaledY() + this.getScaledHeight() - underscoreHeight + underscoreYOffset,
            this.getScaledX() + this.getScaledWidth(),
            this.getScaledY() + this.getScaledHeight() + underscoreYOffset, underscoreColor.getHexColour());
      } else if (this.hovered) {
        RenderUtil.fill(this.getScaledX(), this.getScaledY() + this.getScaledHeight() - underscoreHeight / 1.5D + underscoreYOffset,
            this.getScaledX() + this.getScaledWidth(),
            this.getScaledY() + this.getScaledHeight() + underscoreYOffset, underscoreColor.getHexColour());
      }
    }

    public boolean isSelected() {
      return selected;
    }

    public Tab setSelected(boolean selected) {
      this.selected = selected;
      return this;
    }

    public Colour getUnderscoreColor() {
      return underscoreColor;
    }

    public Tab setUnderscoreColor(Colour underscoreColor) {
      this.underscoreColor = underscoreColor;
      return this;
    }

    public double getUnderscoreHeight() {
      return underscoreHeight;
    }

    public Tab setUnderscoreHeight(double underscoreHeight) {
      this.underscoreHeight = underscoreHeight;
      return this;
    }

    public boolean isDisabled() {
      return disabled;
    }

    public Tab setDisabled(boolean disabled) {
      this.disabled = disabled;
      return this;
    }

    public float getUnderscoreYOffset() {
      return underscoreYOffset;
    }

    public Tab setUnderscoreYOffset(float underscoreYOffset) {
      this.underscoreYOffset = underscoreYOffset;
      return this;
    }

  }
}