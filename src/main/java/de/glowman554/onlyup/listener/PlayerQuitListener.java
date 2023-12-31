package de.glowman554.onlyup.listener;

import de.glowman554.onlyup.OnlyUpMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.EventListener;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        OnlyUpMain.getInstance().getTimer().onQuit(event.getPlayer());
    }
}
