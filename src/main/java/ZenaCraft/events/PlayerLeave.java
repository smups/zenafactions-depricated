package ZenaCraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ZenaCraft.App;

public class PlayerLeave implements Listener{
    
    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        App.factionIOstuff.unLoadFQC(player, null);
    }
}