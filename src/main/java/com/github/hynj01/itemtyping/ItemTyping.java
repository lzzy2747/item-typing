package com.github.hynj01.itemtyping;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ItemTyping extends JavaPlugin implements Listener {
    private final Random random = new Random();
    private final Map<Player, ItemStack> playerItemMap = new HashMap<>();

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!playerItemMap.containsKey(player)) {
                        ItemStack itemStack = getRandomItem();
                        playerItemMap.put(player, itemStack);
                        sendItemInfoTitle(player, itemStack);
                    }
                }
            }
        }.runTaskTimer(this, 0, 20 * 20);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Component chatMessage = event.message();
        String chatText = PlainTextComponentSerializer.plainText().serialize(chatMessage);
        Player player = event.getPlayer();

        ItemStack playerItem = playerItemMap.get(player);
        if (playerItem != null && chatText.equalsIgnoreCase(playerItem.getType().name())) {
            playerItemMap.remove(player);
            player.getInventory().addItem(playerItem);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.clearTitle();

                onlinePlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofSeconds(10L), Duration.ZERO));
                onlinePlayer.sendTitlePart(TitlePart.TITLE, Component.text(""));
                onlinePlayer.sendTitlePart(TitlePart.SUBTITLE, Component.text(player.getName() + "님이 " + playerItem.getType().name().toUpperCase() + "를 획득했습니다."));
            }
        }
    }

    private void sendItemInfoTitle(Player player, ItemStack itemStack) {
        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ZERO));
        player.sendTitlePart(TitlePart.TITLE, Component.text(itemStack.getType().name().toUpperCase(Locale.ENGLISH)));
        player.sendTitlePart(TitlePart.SUBTITLE, Component.text(itemStack.getAmount()).color(NamedTextColor.YELLOW));
    }

    private ItemStack getRandomItem() {
        Material[] materials = Material.values();
        Material randomMaterial = materials[random.nextInt(materials.length)];

        return new ItemStack(randomMaterial);
    }
}