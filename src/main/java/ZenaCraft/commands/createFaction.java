package ZenaCraft.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

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

        if (args.length != 1) return App.invalidSyntax(player);

        if(!player.hasMetadata("createFaction") || !player.getMetadata("createFaction").get(0).asBoolean()){
            player.sendMessage(App.zenfac + "are you sure you want to create a faction? You'll leave your current faction. Type this command again to confirm.");
            player.setMetadata("createFaction", new FixedMetadataValue(plugin, true));
            return true;
        }
        player.setMetadata("createFaction", new FixedMetadataValue(plugin, false));


        String name = args[0];

        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction f = (Faction) mEntry.getValue();
            if (f.getName() == name){
                player.sendMessage(App.zenfac + ChatColor.RED + "Faction Already exists!");
                return true;
            }
        }

        if (econ.getBalance(player) < faction_cost){
            sender.sendMessage(App.zenfac + ChatColor.DARK_RED + "You don't have enough money to make a faction!");
            return true;
        }

        String[] defaultRanks = {"Founder", "Bigshot", "Member"};
        String prefix = new String(name);
        int newID = (int) App.factionIOstuff.getFactionList().size();

        while (App.factionIOstuff.getFactionList().containsKey(newID)) newID++;

        Faction newFaction = new Faction(name, defaultRanks, faction_cost, new HashMap<UUID, Integer>(), prefix, newID, 0xFFFFFF);

        App.factionIOstuff.addFaction(newFaction);

        App.factionIOstuff.changePlayerFaction(newFaction, player, 0);

        //scoreboard stuff
        App.factionIOstuff.reloadScoreBoard(null);

        econ.withdrawPlayer(player, faction_cost);

        player.setMetadata("faction", new FixedMetadataValue(plugin, name));
        player.setMetadata("factionID", new FixedMetadataValue(plugin, newID));
        
        player.sendMessage(App.zenfac + ChatColor.DARK_RED + "Created faction: " + name);
        
        return true;
    }
}