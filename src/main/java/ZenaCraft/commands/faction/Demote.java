package ZenaCraft.commands.faction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class Demote extends TemplateCommand{

    public Demote() {super(1);}

    @Override
    protected boolean run() {
        String demotedName = args[0];
        Player demotedPlayer = Bukkit.getPlayer(demotedName);

        if (demotedPlayer == null) return playerNoExist(player);

        Faction faction = App.factionIOstuff.getPlayerFaction(demotedPlayer);

        if (faction.getMembers().get(player.getUniqueId()) != 0) return invalidRank(player, 0);

        if (!faction.getMembers().containsKey(demotedPlayer.getUniqueId())){
            player.sendMessage(App.zenfac + ChatColor.RED + "that player isn't in your faction!");
            return true;
        }

        Integer rank = faction.getMembers().get(demotedPlayer.getUniqueId());

        if (rank == 2){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't demote someone who is " + faction.getRanks()[2] + " rank!");
            return true;
        }

        faction.getMembers().replace(demotedPlayer.getUniqueId(), rank + 1);
        player.sendMessage(App.zenfac + "Demoted " + ChatColor.WHITE + args[0] + ChatColor.AQUA + " to the rank of " + ChatColor.BOLD + faction.getRanks()[rank + 1]);
        demotedPlayer.sendMessage(App.zenfac + "You were demoted to the rank of: " + ChatColor.BOLD + ChatColor.RED + faction.getRanks()[rank + 1]);
        demotedPlayer.playSound(demotedPlayer.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1f, 1f);
        return true;
    }
}