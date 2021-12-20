package ZenaCraft.commands.faction;

import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class JoinFaction extends TemplateCommand{

    public JoinFaction() {super(1);}

    @Override
    protected boolean run() {
        Plugin plugin = App.getPlugin(App.class);

        if(!player.hasMetadata("joinFaction") || !player.getMetadata("joinFaction").get(0).asBoolean()){
            player.sendMessage(App.zenfac + "are you sure you want to change faction? You'll leave your" + 
                " current faction. Type this command again to confirm.");
            player.setMetadata("joinFaction", new FixedMetadataValue(plugin, true));
            return true;
        }

        player.setMetadata("joinFaction", new FixedMetadataValue(plugin, false));

        //check if they're trying to boop you by joining their own faction
        if (App.factionIOstuff.getPlayerFaction(player).getName().equals(args[0])){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't join a faction you're already a member of!");
            return true;
        }

        for (Faction faction : App.factionIOstuff.getFactionList()){
            if (faction.getName().equals(args[0])){
                App.factionIOstuff.changePlayerFaction(faction, player);
                player.sendMessage(App.zenfac + ChatColor.GREEN + "You've been added to the faction: " + ChatColor.BOLD + faction.getPrefix());
                return true;
            }
        }
        player.sendMessage(App.zenfac + ChatColor.RED + "Faction " + args[0] + " not found!");
        return true;
    }
}