package ZenaCraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ZenaCraft.App;


public class PlayerChat implements Listener{

    @EventHandler
    void onPlayerJoin(AsyncPlayerChatEvent event){
        String prefix = App.factionIOstuff.getPlayerPrefix(event.getPlayer());
        String rank = App.factionIOstuff.getPlayerRank(event.getPlayer());
        event.setFormat(ChatColor.WHITE + "[" + prefix + " | " + rank + ChatColor.WHITE + "] " + "<" + event.getPlayer().getName() + "> " + event.getMessage());
    }
}
