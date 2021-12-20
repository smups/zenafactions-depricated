package ZenaCraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.War;

public class PlayerJoin implements Listener{
    //This file checks on join if the player has metadata corresponding to membership of a new faction
    //if not, it adds the player to the 'neutral' faction. Also adds metadata tag for faction to new players
    
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Plugin plugin = App.getPlugin(App.class);

        if (!App.factionIOstuff.isKnownPlayer(player)){
            //Add player to default faction with lowest rank
            //and give him some metadata
            Faction defaultFaction = App.factionIOstuff.getFaction(0);

            if (!player.isOp())
                App.factionIOstuff.addPlayerToFaction(defaultFaction, player);
            else
                App.factionIOstuff.addPlayerToFaction(defaultFaction, player, defaultFaction.getRanks().get(0));

            event.getPlayer().sendMessage(App.zenfac + ChatColor.GREEN + "You've been added to the international faction!");

            if(App.EU && App.logging) App.getCommon().EUgdpr(player);
        }
        else{
            //Now for the returning player
            Faction faction = App.factionIOstuff.getPlayerFaction(player);
                    
            event.setJoinMessage(App.zenfac + ChatColor.WHITE + "(" + faction.getPrefix() + ChatColor.WHITE + ") " +
                "Welcome back " + ChatColor.BOLD + player.getDisplayName());

        }

        //notify admins of GDPR
        if(player.isOp())
            if(App.EU && App.logging) App.getCommon().EUgdpr(player);

        //Here comes the Scoreboard stuff
        App.factionIOstuff.reloadScoreBoard(player);

        //here comes the FQC stuff
        if(player.getWorld().getEnvironment().equals(Environment.NORMAL)) App.factionIOstuff.loadFQC(player, null);

        //setAutoclaimingMetadata
        if (!player.hasMetadata("autoClaiming")){
            player.setMetadata("autoClaiming", new FixedMetadataValue(plugin, false));
        }
        if (!player.hasMetadata("autoClaimingRadius")){
            player.setMetadata("autoClaimingRadius", new FixedMetadataValue(plugin, 0));
        }

        //WarStuff
        for (War war : App.warThread.getWars()) war.setPlayerBossBar(player);
    }
}