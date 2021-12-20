package ZenaCraft.events;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

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
                player.setMetadata("factionID", new FixedMetadataValue(plugin, faction.getID())); 
                event.getPlayer().sendMessage(App.zenfac + ChatColor.DARK_RED + "You've been added to the international faction!");
            }
            else{
                String faction = App.playerHashMap.get(player.getUniqueId());
                int factionID = App.factionHashMap.get(faction).getID();
                player.setMetadata("faction", new FixedMetadataValue(plugin, faction));
                player.setMetadata("factionID", new FixedMetadataValue(plugin, factionID));
                event.setJoinMessage(App.zenfac + "(" + faction + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
            }
        }
        else{
            String faction = player.getMetadata("faction").get(0).asString();
            event.setJoinMessage(App.zenfac + "(" + faction + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
        }

        //Here comes the Scoreboard stuff
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Objective objective;

        if (board.getObjective(DisplaySlot.SIDEBAR) == null){
            objective = board.registerNewObjective("test", "dummy", ChatColor.BOLD + "Faction Influence");
            Score score = objective.getScore(ChatColor.RED + "test");
            score.setScore(1200);
        }
        else{
            objective = board.getObjective(DisplaySlot.SIDEBAR);        
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Map.Entry mapElement : App.factionHashMap.entrySet()){
            Faction value = (Faction) mapElement.getValue();
            Score score = objective.getScore(value.getPrefix());
            score.setScore( (int) value.getInfluence());
        }

        player.setScoreboard(board);
    }
}