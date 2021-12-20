package ZenaCraft.commands.faction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class Promote extends TemplateCommand{

    public Promote() {super(1);}

    @Override
    protected boolean run() {
        String promotedName = args[0];
        Player promotedPlayer = Bukkit.getPlayer(promotedName);

        if (promotedPlayer == null) return playerNoExist(player);

        Faction faction = App.factionIOstuff.getPlayerFaction(promotedPlayer);

        if (!faction.getMembers().containsKey(promotedPlayer.getUniqueId())){
            return noMember(player);
        }

        if (faction.getMembers().get(player.getUniqueId()) != 0) return invalidRank(player, 0);

        Integer rank = faction.getMembers().get(promotedPlayer.getUniqueId());

        if (rank == 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't promote someone who is " + faction.getRanks()[0] + " rank!");
            return true;
        }

        faction.getMembers().replace(promotedPlayer.getUniqueId(), rank - 1);
        player.sendMessage(App.zenfac + "Promoted " + ChatColor.WHITE + args[0] + ChatColor.AQUA + " to the rank of " + ChatColor.BOLD + faction.getRanks()[rank - 1]);
        promotedPlayer.sendMessage(App.zenfac + "You were promoted to the rank of: " + ChatColor.BOLD + ChatColor.GREEN + faction.getRanks()[rank - 1]);
        promotedPlayer.playSound(promotedPlayer.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
        return true;
    }
}