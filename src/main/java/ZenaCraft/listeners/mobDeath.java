package ZenaCraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
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
        Player player;
        
        if (dead.getLastDamageCause() instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent ede = (EntityDamageByEntityEvent) dead.getLastDamageCause();

            if (ede.getDamager() instanceof Player)
            {
                player = (Player) ede.getDamager();
            }
            else if (ede.getDamager() instanceof Arrow){
                Arrow arrow = (Arrow) ede.getDamager();
                if (!(arrow.getShooter() instanceof Player)) return;
                player = (Player) arrow.getShooter();
            }
            else return;
            
            if (App.getPlugin(App.class).getConfig().getBoolean("Modify mob xp drops")){
                //Blaze, guardian, evoker en elder guardian
                if (event.getDroppedExp() == 10) event.setDroppedExp(30);
                if (dead instanceof Evoker) event.setDroppedExp(50);
                if (dead instanceof ElderGuardian) event.setDroppedExp(100);
                //Piglin brute en 
                if (event.getDroppedExp() == 20) event.setDroppedExp(40);
                if (dead instanceof Hoglin) event.setDroppedExp(20);
                if (dead instanceof Wither) event.setDroppedExp(1000);
                if (dead instanceof WitherSkeleton) event.setDroppedExp(30);
            }

            Economy econ = App.getEconomy();
            double earnings = event.getDroppedExp()/10.0;
            econ.depositPlayer(player, earnings);
            player.sendMessage(App.zenfac + "you earned " + ChatColor.GREEN + "Ƒ" + String.valueOf(earnings));
            player.playSound(player.getLocation(), Sound.ENTITY_DOLPHIN_PLAY, 1f, 1f);
        }
    }
    
}
