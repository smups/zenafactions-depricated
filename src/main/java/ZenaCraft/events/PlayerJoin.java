package ZenaCraft.events;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import ZenaCraft.objects.FactionQChunk;

public class PlayerJoin implements Listener{
    //This file checks on join if the player has metadata corresponding to membership of a new faction
    //if not, it adds the player to the 'neutral' faction. Also adds metadata tag for faction to new players
    
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Plugin plugin = App.getPlugin(App.class);
        
        //Check of player faction metadata heeft
        if (!player.hasMetadata("factionID")){

            //Check if player has _ever_ played on the server
            //If not, add them to the default faction
            if (!App.factionIOstuff.isKnownPlayer(player)){

                //Add player to default faction with lowest rank
                //and give him some metadata
                Faction defaultFaction = App.factionIOstuff.getFaction(0);
                int rank = 3;
                if (player.isOp()) rank = 0;
                App.factionIOstuff.addPlayerToFaction(defaultFaction, player, rank);
                player.setMetadata("faction", new FixedMetadataValue(plugin, defaultFaction.getName()));
                player.setMetadata("factionID", new FixedMetadataValue(plugin, defaultFaction.getID()));   
                
                event.getPlayer().sendMessage(App.zenfac + ChatColor.GREEN + "You've been added to the international faction!");
            }
            else{
                //Now for the returning player
                Faction faction = App.factionIOstuff.getPlayerFaction(player);
            
                player.setMetadata("faction", new FixedMetadataValue(plugin, faction.getName()));
                player.setMetadata("factionID", new FixedMetadataValue(plugin, faction.getID()));                
                event.setJoinMessage(App.zenfac + "(" + faction.getPrefix() + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
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
        }
        else{
            objective = board.getObjective(DisplaySlot.SIDEBAR);        
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Map.Entry mapElement : App.factionIOstuff.getFactionList().entrySet()){
            Faction value = (Faction) mapElement.getValue();
            Score score = objective.getScore(value.getPrefix());
            score.setScore( (int) value.getInfluence());
        }

        player.setScoreboard(board);

        //here comes the chunk stuff
        App.factionIOstuff.loadFQC(player, null);

        //setAutoclaimingMetadata
        if (!player.hasMetadata("autoClaiming")){
            player.setMetadata("autoClaiming", new FixedMetadataValue(plugin, false));
        }
    }
}