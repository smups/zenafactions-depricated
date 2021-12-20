package ZenaCraft.commands.wars;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class DeclareWar extends TemplateCommand{

    public DeclareWar() {super(1);}

    @Override
    protected boolean run() {
        Faction attackers = App.factionIOstuff.getPlayerFaction(player);
        Plugin plugin = App.getPlugin(App.class);

        double warcost = plugin.getConfig().getDouble("warCost");

        if(!player.hasMetadata("declarewar") || !player.getMetadata("declarewar").get(0).asBoolean()){
            String influenceString = ChatColor.BOLD + String.valueOf(warcost) + ChatColor.RESET + "" + ChatColor.RED + " influence! Type this command again to confirm.";
            player.sendMessage(App.zenfac + "are you sure you want to declare war? This costs " + influenceString);
            player.setMetadata("declarewar", new FixedMetadataValue(plugin, true));
            return true;
        }
        player.setMetadata("declarewar", new FixedMetadataValue(plugin, false));

        if (attackers == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "you're not a member of a faction, so you can't declare war!");
            return true;
        }

        if (attackers.getMembers().get(player.getUniqueId()) != 0) return invalidRank(player, 0);

        if (App.warThread.getWarFromFaction(attackers) != null){
            player.sendMessage(App.zenfac + ChatColor.RED + "you are already at war with someone!");
            return true;
        }

        //get defending faction
        Faction defenders = null;;
        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction mf = (Faction) mEntry.getValue();
            if (mf.getName().equals(args[0])) defenders = mf;
        }

        if (defenders == null) return factionNoExist(player);

        if (App.warThread.getWarFromFaction(defenders) != null){
            player.sendMessage(App.zenfac + ChatColor.RED + "sorry, this faction is already at war with someone else!");
            return true;
        }

        //check if they're trying to boop you by warring their own faction
        if (attackers.equals(defenders)){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't declare war on yourself!");
            return true;
        }

        App.warThread.startWar(defenders, attackers, player.getChunk());
        attackers.setInfluence(attackers.getInfluence() - warcost);
        App.factionIOstuff.reloadScoreBoard(null);

        return true;
    }  
}