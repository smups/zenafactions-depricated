package ZenaCraft.commands.faction;

import java.util.Map;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Colour;
import ZenaCraft.objects.Faction;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class CreateFaction extends TemplateCommand{
    Plugin plugin = App.getPlugin(App.class);
    Economy econ = App.getEconomy();

    double faction_cost = plugin.getConfig().getDouble("faction creation cost");
    double player_influence = plugin.getConfig().getDouble("influence per player");

    public CreateFaction() {super(1);}

    @Override
    protected boolean run() {

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

        if (econ.getBalance(player) < faction_cost) return insufficientFunds(player);

        Colour c = new Colour(0xFFFFFF, org.bukkit.ChatColor.WHITE);

        int newID = 0;
        while (newID < 127){
            if (!App.factionIOstuff.getFactionList().containsKey(newID)) break;
            newID++;
        }

        Faction newFaction = new Faction(name, faction_cost, player, newID, c);

        App.factionIOstuff.addFaction(newFaction);

        App.factionIOstuff.changePlayerFaction(newFaction, player);

        //scoreboard stuff
        App.factionIOstuff.reloadScoreBoard(null);

        econ.withdrawPlayer(player, faction_cost);

        player.setMetadata("faction", new FixedMetadataValue(plugin, name));
        player.setMetadata("factionID", new FixedMetadataValue(plugin, newID));
        
        player.sendMessage(App.zenfac + ChatColor.DARK_RED + "Created faction: " + name);
        
        return true;
    }
}