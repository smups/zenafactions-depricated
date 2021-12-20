package ZenaCraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.FactionQChunk;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;

public class ModifyClaim implements Listener{

    @EventHandler
    void onCreateClaim(ClaimModifiedEvent event){
        Player player = (Player) event.getModifier();
        Claim claim = event.getTo();

        int condition = 0;

        for (Chunk chunk : claim.getChunks()){
            int chunkX = (int) chunk.getX();
            int chunkZ = (int) chunk.getZ();

            FactionQChunk fqc = App.factionIOstuff.getFQC(
                App.factionIOstuff.calcFQCName(chunkX, chunkZ, null, null)
            );

            Faction owner = fqc.getOwner(new Location(chunk.getWorld(), chunkX*16, 0, chunkZ*16));

            if (owner == null) continue; //anyone can claim in neutral territory

            Faction playerFaction = App.factionIOstuff.getPlayerFaction(player);
            if (!playerFaction.equals(owner)) condition += 1;
        }

        if(claim.isAdminClaim()) condition = 0;

        if (condition != 0){
            event.setCancelled(true);
            player.sendMessage(App.zenfac + ChatColor.RED + "Modified claim not within faction borders!");
        }
    }
    
}