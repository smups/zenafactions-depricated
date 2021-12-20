package ZenaCraft.events;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import ZenaCraft.App;

public class PlayerMove implements Listener{

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        
        Location oldLocation = event.getFrom();
        Location newLocation = event.getTo();

        Chunk newChunk = newLocation.getChunk();
        Chunk oldChunk = oldLocation.getChunk();

        //Check of de player naar een nieuwe chunk is gelopen
        if (oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()){
            String oldFQCName = "X" + String.valueOf(oldChunk.getX()/100) + "Z" + String.valueOf(oldChunk.getZ()/100);
            String newFQCName = "X" + String.valueOf(newChunk.getX()/100) + "Z" + String.valueOf(newChunk.getZ()/100);

            //Check of de player naar een nieuwe FQC is gelopen
            if(!oldFQCName.equals(newFQCName)){
                player.sendMessage("Moved from: " + oldFQCName + " to: " + newFQCName);
                //IOstuff + autoclaiming
                MovedFQC movedFQC = new MovedFQC(player, oldLocation, newLocation);
                autoClaimFunc(movedFQC.getThread(), player, newLocation);
            }
            //Niet naar een nieuwe FQC dus
            else{
                byte oldOwnerID = App.factionIOstuff.getFQC(oldFQCName).getChunkData()[Math.abs(oldChunk.getX()) % 100][Math.abs(oldChunk.getZ()) % 100];
                byte newOwnerID = App.factionIOstuff.getFQC(newFQCName).getChunkData()[Math.abs(newChunk.getX()) % 100][Math.abs(newChunk.getZ()) % 100];
                if (oldOwnerID != newOwnerID){
                    if (newOwnerID == -1) player.sendTitle("Entering Wilderness", "Claimable territory", 10, 35, 20);
                    else player.sendTitle("Entering: " + App.factionIOstuff.getFaction(newOwnerID).getPrefix(), "", 10, 35, 20);
                }
                //Code voor autoclaiming
                autoClaimFunc(null, player, newLocation);
            }
        }
    }

    @Nullable
    private void autoClaimFunc(Thread waitThread, Player player, Location newLocation){
        if (player.getMetadata("autoClaiming").get(0).asBoolean()){
            App.factionIOstuff.claimChunks(player, newLocation, player.getMetadata("autoClaimingRadius").get(0).asInt(), waitThread);
        }
    }

    private class MovedFQC implements Runnable{
        private Thread t;
        private Player player;
        private Location oldLocation;
        private Location newLocation;

        public MovedFQC(Player player, Location oldLocation, Location newLocation){
            this.player = player;
            this.oldLocation = oldLocation;
            this.newLocation = newLocation;
            this.start();
        }

        public Thread getThread(){
            return this.t;
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            player.sendMessage(App.zenfac + ChatColor.WHITE + "loading new FQC...");
            try{
                //Wacht totdat de **verwijder** thread klaar is
                App.factionIOstuff.unLoadFQC(player, oldLocation).join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //laad nu de nieuwe FQC's
            try{
                //Wacht totdat ze geladen zijn
                App.factionIOstuff.loadFQC(player, newLocation).join();;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            
            /*
                Okay dit is nog steeds suboptimaal: je zou een rare bug
                kunnen krijgen als een speler te snel van chunk wisselt
                omdat we eerste de oude chunks verwijderen (incl de chunk
                met de player) voordat we ze weer toevoegen, wat zuigt.
                Ook zouden er eigenlijk niet meerdere van deze threads 
                tegelijkertijd moeten runnen -> dit veroorzaakt een memory
                leak!

                Te lui om dit te fixen tho.
            */

            //Deze code doet niks anders dan fucking titels sturen
            Chunk newChunk = newLocation.getChunk();
            Chunk oldChunk = oldLocation.getChunk();

            String oldFQCName = "X" + String.valueOf(oldChunk.getX()/100) + "Z" + String.valueOf(oldChunk.getZ()/100);
            String newFQCName = "X" + String.valueOf(newChunk.getX()/100) + "Z" + String.valueOf(newChunk.getZ()/100);

            byte oldOwnerID = App.factionIOstuff.getFQC(oldFQCName).getChunkData()[Math.abs(oldChunk.getX()) % 100][Math.abs(oldChunk.getZ()) % 100];
            byte newOwnerID = App.factionIOstuff.getFQC(newFQCName).getChunkData()[Math.abs(newChunk.getX()) % 100][Math.abs(newChunk.getZ()) % 100];

            if (oldOwnerID != newOwnerID){
                if (newOwnerID == -1) player.sendTitle("Entering Wilderness", "Claimable territory", 10, 35, 20);
                else player.sendTitle("Entering: " + App.factionIOstuff.getFaction(newOwnerID).getPrefix(), "", 10, 35, 20);
            }

        }
    }
}