package ZenaCraft.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class createFaction implements CommandExecutor{
    Plugin plugin = App.getPlugin(App.class);
    Economy econ = App.getEconomy();

    double faction_cost = plugin.getConfig().getDouble("faction creation cost");
    double player_influence = plugin.getConfig().getDouble("influence per player");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String name = args[0];

        if (App.factionHashMap.containsKey(name)){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "Faction Already exists!");
            return true;
        }
        if (econ.getBalance(player) < faction_cost){
            sender.sendMessage(App.zenfac + ChatColor.DARK_RED + "You don't have enough money to make a faction!");
            return true;
        }

        HashMap<UUID, Integer> founder = new HashMap<UUID, Integer>();
        founder.put(player.getUniqueId(), 0);
        String[] defaultRanks = {"Founder", "Bigshot", "Member"};
        String prefix = new String(name);
        int newID = (int) App.factionHashMap.size();
        Faction newFaction = new Faction(name, defaultRanks, faction_cost, founder, prefix, newID);

        String oldFactionString = player.getMetadata("faction").get(0).asString();
        Faction oldFaction = (Faction) App.factionHashMap.get(oldFactionString);

        newFaction.setInfluence(player_influence);
        oldFaction.setInfluence(oldFaction.getInfluence() - player_influence);

        oldFaction.removeMember(player.getUniqueId());

        App.factionHashMap.put(name, newFaction);
        App.playerHashMap.replace(player.getUniqueId(), name);

        //scoreboard stuff
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Objective objective = board.getObjective(DisplaySlot.SIDEBAR);

        Score score = objective.getScore(newFaction.getPrefix());
        score.setScore( (int) newFaction.getInfluence());
        score = objective.getScore(oldFaction.getPrefix());
        score.setScore( (int) oldFaction.getInfluence());

        econ.withdrawPlayer(player, faction_cost);
        player.setMetadata("faction", new FixedMetadataValue(plugin, name));
        player.setMetadata("factionID", new FixedMetadataValue(plugin, newID));
        player.sendMessage(App.zenfac + ChatColor.DARK_RED + "Created faction: " + name);
        
        return true;
    }
}