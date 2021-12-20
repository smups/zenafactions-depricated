package ZenaCraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.War;
import net.milkbowl.vault.economy.Economy;

public class PlayerDeath implements Listener{

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event){
        Player dead = event.getEntity();
        Economy econ = App.getEconomy();
        Plugin plugin = App.getPlugin(App.class);
        double deathCost = plugin.getConfig().getDouble("deathCost");
        deathCost = deathCost*dead.getLevel();

        //unload de FQC's van de dode boi
        App.factionIOstuff.unLoadFQC(dead, dead.getLocation());

        if (dead.getKiller() != null){
            Player killer = (Player) dead.getKiller();

            Faction killerFaction = App.factionIOstuff.getPlayerFaction(killer);
            Faction deadFaction = App.factionIOstuff.getPlayerFaction(dead);

            if (deadFaction.getID() != killerFaction.getID()){
                deathCost += 10;
                deathCost *= 1 + deadFaction.getMembers().get(dead.getUniqueId()).getLevel()/App.permList.size();
                double warscore = deathCost/((double) 100 * deadFaction.getMembers().size());
                if (warscore > 0.45) warscore = 0.45;

                for (War war : App.warThread.getWars()){
                    if (war.getAttackers().equals(killerFaction) && war.getDefenders().equals(deadFaction)){
                        war.addWarScore(warscore);
                    }
                    else if (war.getAttackers().equals(deadFaction) && war.getDefenders().equals(killerFaction)){
                        war.removeWarScore(warscore);
                    }
                }
            }
            if(!econ.has(dead, deathCost)) deathCost = econ.getBalance(dead);

            econ.depositPlayer(killer, deathCost);
            killer.sendMessage(App.zenfac + "gained " + ChatColor.GREEN + "Ƒ" + String.valueOf(deathCost) + ChatColor.AQUA + " from killing " + ChatColor.RED + dead.getName());
            killer.playSound(killer.getLocation(), Sound.ENTITY_DOLPHIN_PLAY, 1f, 1f);

        }
        econ.withdrawPlayer(dead, deathCost);
        dead.sendMessage(App.zenfac + "you lost " + ChatColor.RED + "Ƒ" + String.valueOf(deathCost) + ChatColor.AQUA + " from dying!");
        dead.playSound(dead.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1f, 1f);
    }    
}