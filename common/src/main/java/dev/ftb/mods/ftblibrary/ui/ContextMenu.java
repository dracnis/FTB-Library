package dev.ftb.mods.ftblibrary.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ContextMenu extends Panel {
	public static class CButton extends Button {
		public final ContextMenu contextMenu;
		public final ContextMenuItem item;

		public CButton(ContextMenu panel, ContextMenuItem i) {
			super(panel, i.title, i.icon);
			contextMenu = panel;
			item = i;
			setSize(panel.getGui().getTheme().getStringWidth(item.title) + (contextMenu.hasIcons ? 14 : 4), 12);
		}

		@Override
		public void addMouseOverText(TooltipList list) {
			item.addMouseOverText(list);
		}

		@Override
		public WidgetType getWidgetType() {
			return item.enabled.getAsBoolean() ? super.getWidgetType() : WidgetType.DISABLED;
		}

		@Override
		public void drawIcon(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
			item.drawIcon(matrixStack, theme, x, y, w, h);
		}

		@Override
		public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
			GuiHelper.setupDrawing();

			if (contextMenu.hasIcons) {
				drawIcon(matrixStack, theme, x + 1, y + 2, 8, 8);
				theme.drawString(matrixStack, getTitle(), x + 11, y + 2, theme.getContentColor(getWidgetType()), Theme.SHADOW);
			} else {
				theme.drawString(matrixStack, getTitle(), x + 2, y + 2, theme.getContentColor(getWidgetType()), Theme.SHADOW);
			}
		}

		@Override
		public void onClicked(MouseButton button) {
			playClickSound();

			if (item.yesNoText.getString().isEmpty()) {
				item.onClicked(contextMenu, button);
			} else {
				getGui().openYesNo(item.yesNoText, new TextComponent(""), () -> item.onClicked(contextMenu, button));
			}
		}
	}

	public static class CSeperator extends Button {
		public CSeperator(Panel panel) {
			super(panel);
			setHeight(5);
		}

		@Override
		public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
			Color4I.WHITE.withAlpha(130).draw(matrixStack, x + 2, y + 2, parent.width - 10, 1);
		}

		@Override
		public void onClicked(MouseButton button) {
		}
	}

	public final List<ContextMenuItem> items;
	public boolean hasIcons;

	public ContextMenu(Panel panel, List<ContextMenuItem> i) {
		super(panel);
		items = i;
		hasIcons = false;

		for (var item : items) {
			if (!item.icon.isEmpty()) {
				hasIcons = true;
				break;
			}
		}
	}

	@Override
	public void addWidgets() {
		for (var item : items) {
			add(item.createWidget(this));
		}
	}

	@Override
	public boolean mousePressed(MouseButton button) {
		var b = super.mousePressed(button);

		if (!b && !isMouseOver()) {
			closeContextMenu();
			return true;
		}

		return b;
	}

	@Override
	public void alignWidgets() {
		setWidth(0);

		for (var widget : widgets) {
			setWidth(Math.max(width, widget.width));
		}

		for (var widget : widgets) {
			widget.setX(3);
			widget.setWidth(width);
		}

		setWidth(width + 6);

		setHeight(align(new WidgetLayout.Vertical(3, 1, 3)));
	}

	@Override
	public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		theme.drawContextMenuBackground(matrixStack, x, y, w, h);
	}

	@Override
	public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		GuiHelper.setupDrawing();
		matrixStack.pushPose();
		matrixStack.translate(0, 0, 900);
		super.draw(matrixStack, theme, x, y, w, h);
		matrixStack.popPose();
	}
}