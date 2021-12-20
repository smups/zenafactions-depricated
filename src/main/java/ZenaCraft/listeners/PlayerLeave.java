package ZenaCraft.listeners;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import net.md_5.bungee.api.ChatColor;

public class PlayerLeave implements Listener{
    
    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if (player.getWorld().getEnvironment().equals(Environment.NORMAL)) App.factionIOstuff.unLoadFQC(player, null);

        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        event.setQuitMessage(App.zenfac + ChatColor.WHITE + "(" + faction.getPrefix() + ChatColor.WHITE + ") " +
        "Goodbye " + ChatColor.BOLD + player.getDisplayName() + ChatColor.RESET + " !");
    }
}