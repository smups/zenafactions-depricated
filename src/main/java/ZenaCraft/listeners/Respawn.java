package ZenaCraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import ZenaCraft.App;

public class Respawn implements Listener{

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        App.factionIOstuff.loadFQC(e.getPlayer(), e.getRespawnLocation());
    }
    
}