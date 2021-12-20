package ZenaCraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport implements Listener{

    @EventHandler
    void onPlayerTeleport(PlayerTeleportEvent e){
        PlayerMoveEvent pme = new PlayerMoveEvent(e.getPlayer(), e.getFrom(), e.getTo());
        pme.callEvent();
    }
    
}
