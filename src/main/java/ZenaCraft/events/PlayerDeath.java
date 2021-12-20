package ZenaCraft.events;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import net.milkbowl.vault.economy.Economy;

public class PlayerDeath implements Listener{

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event){
        Player dead = event.getEntity();
        Economy econ = App.getEconomy();
        Plugin plugin = App.getPlugin(App.class);
        double deathCost = plugin.getConfig().getDouble("deathCost");
        deathCost = deathCost*dead.getLevel();

        if (dead.getKiller() != null){
            Player killer = (Player) dead.getKiller();

            econ.depositPlayer(killer, deathCost);
            killer.sendMessage(App.zenfac + "gained " + ChatColor.GREEN + "Ƒ" + String.valueOf(deathCost) + ChatColor.AQUA + " from killing " + ChatColor.RED + dead.getName());
            killer.playSound(killer.getLocation(), Sound.ENTITY_DOLPHIN_PLAY, 1f, 1f);

            /*
            if (dead.getMetadata("factionID").get(0).asByte() == killer.getMetadata("factionID").get(0).asByte());
            killer.sendMessage(App.zenfac + "still under construction");
            */
        }

        if (econ.has(dead, deathCost)) econ.withdrawPlayer(dead, deathCost);
        else econ.withdrawPlayer(dead, econ.getBalance(dead));
        dead.sendMessage(App.zenfac + "you lost " + ChatColor.RED + "Ƒ" + String.valueOf(deathCost) + ChatColor.AQUA + " from dying!");
        dead.playSound(dead.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1f, 1f);        
    }    
}