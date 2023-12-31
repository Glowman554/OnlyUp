package de.glowman554.onlyup.tasks;

import de.glowman554.onlyup.OnlyUpMain;
import de.glowman554.onlyup.db.Database;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;

public class TimerTask extends BukkitRunnable {
    public final String world;
    public final int startY;
    public final int goalY;

    private final Hologram hologram;

    private final HashMap<Player, Integer> timer = new HashMap<>();

    public TimerTask(String world, int startY, int goalY) {
        this.world = world;
        this.startY = startY;
        this.goalY = goalY;

        Location holoLocation = OnlyUpMain.getInstance().getHologram().toLocation(Bukkit.getWorld(OnlyUpMain.getInstance().getWorld()));
        hologram = HolographicDisplaysAPI.get(OnlyUpMain.getInstance()).createHologram(holoLocation);

        updateHologram();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers().stream().filter(v -> v.getLocation().getWorld().getName().equals(world)).toList()) {
            Location location = player.getLocation();

            int y = location.getBlockY();

            if (y < startY && timer.containsKey(player)) {
                timer.remove(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
                updateHologram();
            } else if (y >= startY) {
                if (y >= goalY) {
                    if (timer.containsKey(player)) {
                        Database.Time time = new Database.Time(player.getName(), timer.get(player));
                        timer.remove(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§e§lɢʟüᴄᴋᴡᴜɴsᴄʜ ᴅᴜ ʜᴀsᴛ ᴇs ɢᴇsᴄʜᴀғғᴛ."));

                        try {
                            OnlyUpMain.getInstance().getDatabase().saveTime(time);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    int ptimer = 1;
                    if (timer.containsKey(player)) {
                        ptimer = timer.get(player) + 1;
                    } else if (y > startY + 5) {
                        return;
                    }
                    timer.put(player, ptimer);

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§e§lʜöʜᴇ: %d §7§lᴛɪᴍᴇʀ: §a§l %.2fs", y, (double) ptimer / 20)));
                }
            }
        }
    }

    private void updateHologram() {
        hologram.getLines().clear();

        try {
            int p = 0;
            String[] colors = new String[] {"e", "c", "b", "a", "2"};
            for (Database.Time time : OnlyUpMain.getInstance().getDatabase().loadBestTime(5)) {
                hologram.getLines().appendText(String.format("§%s§lᴘʟᴀᴛᴢ %d / §7§l%s %.2fs", colors[p], p + 1, time.player(), (double) time.timeTick() / 20));
                p++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onQuit(Player player) {
        timer.remove(player);
    }
}
