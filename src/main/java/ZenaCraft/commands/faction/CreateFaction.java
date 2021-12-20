package ZenaCraft.commands.faction;

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


        String name = this.args[0];

        for (Faction f : App.factionIOstuff.getFactionList()){
            if (f.getName() == name){
                player.sendMessage(App.zenfac + ChatColor.RED + "Faction Already exists!");
                return true;
            }
        }

        if (econ.getBalance(player) < faction_cost) return insufficientFunds(player);

        Colour c = new Colour(0xFFFFFF, org.bukkit.ChatColor.WHITE);
        Faction newFaction = new Faction(name, faction_cost, player, c);

        App.factionIOstuff.addFaction(newFaction);

        //scoreboard stuff
        App.factionIOstuff.reloadScoreBoard(null);

        econ.withdrawPlayer(player, faction_cost);
        
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Created faction: " + name);
        
        return true;
    }
}