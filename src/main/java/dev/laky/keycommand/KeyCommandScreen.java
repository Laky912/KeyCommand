package dev.laky.keycommand;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeyCommandScreen extends Screen {
    private TextFieldWidget commandField;
    private final List<ButtonWidget> rows = new ArrayList<>();
    private int captureIndex = -1;
    private int scroll;
    public KeyCommandScreen() { super(Text.literal("KeyCommand · Comandos y teclas")); }
    @Override protected void init() {
        int left = width / 2 - 150;
        commandField = new TextFieldWidget(textRenderer, left, 44, 205, 20, Text.literal("Comando"));
        commandField.setPlaceholder(Text.literal("/warp end")); addDrawableChild(commandField);
        addDrawableChild(ButtonWidget.builder(Text.literal("Añadir"), b -> { String cmd = commandField.getText().trim(); if (!cmd.isEmpty()) { KeyCommandClient.ENTRIES.add(new KeyCommandClient.Entry(0, cmd)); commandField.setText(""); rebuildRows(); KeyCommandClient.save(); } }).dimensions(left + 212, 44, 88, 20).build());
        rebuildRows();
        addDrawableChild(ButtonWidget.builder(Text.literal("Cerrar"), b -> close()).dimensions(width / 2 - 45, height - 30, 90, 20).build());
    }
    private void rebuildRows() {
        for (ButtonWidget row : rows) remove(row); rows.clear();
        int left = width / 2 - 150;
        for (int i = 0; i < KeyCommandClient.ENTRIES.size(); i++) {
            final int index = i; KeyCommandClient.Entry entry = KeyCommandClient.ENTRIES.get(i); int y = 76 + i * 27 - scroll;
            if (y < 65 || y > height - 40) continue;
            ButtonWidget key = ButtonWidget.builder(Text.literal(captureIndex == i ? "Pulsa tecla…" : entry.keyCode() == 0 ? "Sin tecla" : KeyCommandClient.keyName(entry.keyCode())), b -> captureIndex = index).dimensions(left, y, 92, 20).build();
            ButtonWidget cmd = ButtonWidget.builder(Text.literal(entry.command()), b -> { }).dimensions(left + 98, y, 155, 20).build();
            ButtonWidget del = ButtonWidget.builder(Text.literal("X"), b -> { KeyCommandClient.ENTRIES.remove(index); captureIndex = -1; rebuildRows(); KeyCommandClient.save(); }).dimensions(left + 258, y, 42, 20).build();
            rows.add(key); rows.add(cmd); rows.add(del); addDrawableChild(key); addDrawableChild(cmd); addDrawableChild(del);
        }
    }
    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (captureIndex >= 0) { KeyCommandClient.Entry old = KeyCommandClient.ENTRIES.get(captureIndex); KeyCommandClient.ENTRIES.set(captureIndex, new KeyCommandClient.Entry(keyCode == GLFW.GLFW_KEY_ESCAPE ? 0 : keyCode, old.command())); captureIndex = -1; KeyCommandClient.save(); rebuildRows(); return true; }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) { scroll = Math.max(0, Math.min(Math.max(0, KeyCommandClient.ENTRIES.size() * 27 - height + 120), scroll - (int)(verticalAmount * 27))); rebuildRows(); return true; }
    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) { renderBackground(context, mouseX, mouseY, delta); context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFF); context.drawTextWithShadow(textRenderer, Text.literal("Pulsa una tecla para asignarla. Ñ abre este menú."), width / 2 - 150, 30, 0xAAAAAA); super.render(context, mouseX, mouseY, delta); }
    @Override public void close() { assert client != null; client.setScreen(null); }
}
