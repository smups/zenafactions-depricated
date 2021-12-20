package ZenaCraft.events;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import ZenaCraft.App;
import net.milkbowl.vault.economy.Economy;

public class mobDeath implements Listener{

    @EventHandler
    void onMobDeath(EntityDeathEvent event){
        Entity dead = event.getEntity();

        if (dead.getLastDamageCause() instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent ede = (EntityDamageByEntityEvent) dead.getLastDamageCause();

            if (!(ede.getDamager() instanceof Player)) return;
            Player player = (Player) ede.getDamager();

            Economy econ = App.getEconomy();
            double earnings = event.getDroppedExp()/10.0;
            econ.depositPlayer(player, earnings);
            player.sendMessage(App.zenfac + "you earned " + ChatColor.GREEN + "Ƒ" + String.valueOf(earnings));
            player.playSound(player.getLocation(), Sound.ENTITY_DOLPHIN_PLAY, 1f, 1f);
        }
    }
    
}