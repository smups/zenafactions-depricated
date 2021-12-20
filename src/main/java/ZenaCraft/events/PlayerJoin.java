package ZenaCraft.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class PlayerJoin implements Listener{
    //This file checks on join if the player has metadata corresponding to membership of a new faction
    //if not, it adds the player to the 'neutral' faction. Also adds metadata tag for faction to new players
    
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Plugin plugin = App.getPlugin(App.class);
        Double player_influence = plugin.getConfig().getDouble("influence per player");

        if (!player.hasMetadata("faction")){
            //Check of player faction metadata heeft
            if (!App.playerHashMap.containsKey(player.getUniqueId())){
                //Check of player in de database staat, -> voeg toe
                App.playerHashMap.put(player.getUniqueId(), "default");
                player.setMetadata("faction", new FixedMetadataValue(plugin, "default"));     
                
                Faction faction = (Faction) App.factionHashMap.get("default");
                faction.addMember(player.getUniqueId(), 3);
                faction.setInfluence(faction.getInfluence() + player_influence);
                event.getPlayer().sendMessage(App.zenfac + ChatColor.DARK_RED + "You've been added to the international faction!");
            }
            else{
                String faction = App.playerHashMap.get(player.getUniqueId());
                player.setMetadata("faction", new FixedMetadataValue(plugin, faction));
                event.setJoinMessage(App.zenfac + "(" + faction + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
            }
        }
        else{
            String faction = player.getMetadata("faction").get(0).asString();
            event.setJoinMessage(App.zenfac + "(" + faction + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
        }
    }
}