package dev.laky.keycommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class KeyCommandClient implements ClientModInitializer {
    public static final List<Entry> ENTRIES = new ArrayList<>();
    private static Path configPath;
    private static KeyBinding openMenu;
    private static final java.util.Set<Integer> downLastTick = new java.util.HashSet<>();

    @Override public void onInitializeClient() {
        configPath = MinecraftClient.getInstance().runDirectory.toPath().resolve("config/keycommand.txt");
        load();
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.keycommand.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, "category.keycommand"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) client.setScreen(new KeyCommandScreen());
            if (client.player == null || client.getWindow() == null || client.currentScreen != null) { downLastTick.clear(); return; }
            long window = client.getWindow().getHandle();
            for (Entry entry : List.copyOf(ENTRIES)) {
                if (entry.keyCode <= 0) continue;
                boolean down = GLFW.glfwGetKey(window, entry.keyCode) == GLFW.GLFW_PRESS;
                if (down && !downLastTick.contains(entry.keyCode)) {
                    String command = entry.command.trim();
                    if (!command.isEmpty()) client.player.networkHandler.sendChatCommand(command.startsWith("/") ? command.substring(1) : command);
                }
                if (down) downLastTick.add(entry.keyCode); else downLastTick.remove(entry.keyCode);
            }
        });
    }
    public static void save() {
        try { Files.createDirectories(configPath.getParent()); StringBuilder out = new StringBuilder(); for (Entry e : ENTRIES) out.append(e.keyCode).append('\t').append(e.command.replace("\\", "\\\\").replace("\t", " ").replace("\n", " ")).append('\n'); Files.writeString(configPath, out.toString()); }
        catch (IOException ex) { ex.printStackTrace(); }
    }
    private static void load() {
        try { if (!Files.exists(configPath)) { ENTRIES.add(new Entry(GLFW.GLFW_KEY_G, "/warp end")); ENTRIES.add(new Entry(GLFW.GLFW_KEY_H, "/warp nether")); save(); return; }
            for (String line : Files.readAllLines(configPath)) { String[] p = line.split("\\t", 2); if (p.length == 2) try { ENTRIES.add(new Entry(Integer.parseInt(p[0]), p[1])); } catch (NumberFormatException ignored) {} }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
    public static String keyName(int code) { String name = GLFW.glfwGetKeyName(code, 0); return name == null ? "Key " + code : name.toUpperCase(); }
    public record Entry(int keyCode, String command) { }
}
