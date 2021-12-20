package ZenaCraft.listeners;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
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

        //stop als player niet in overworld is
        if (!oldLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL) && 
            !newLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL)) return;
        //als de player van de overworld naar de nether/end gaat, moeten alle FQC's verwijderd worden
        else if (oldLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL) &&
            !newLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL))
        {
            App.factionIOstuff.unLoadFQC(player, oldLocation);
            return;
        }
        //als de player van de nether/end naar de overworld gaat, moeten er juist FQC's geladen worden!
        else if (newLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL) &&
            !oldLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL))
        {
            App.factionIOstuff.loadFQC(player, newLocation);
            return;
        }

        //Nu de code voor wanneer de speler beweegt in de overworld

        //Check of de player naar een nieuwe chunk is gelopen
        if (oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()){

            String oldFQCName = App.factionIOstuff.calcFQCName(oldChunk.getX(), oldChunk.getZ(), null, null);
            String newFQCName = App.factionIOstuff.calcFQCName(newChunk.getX(), newChunk.getZ(), null, null);

            //Check of de player naar een nieuwe FQC is gelopen
            if(!oldFQCName.equals(newFQCName)){
                if (player.isOp()) player.sendMessage("Moved from: " + oldFQCName + " to: " + newFQCName);
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
            App.factionIOstuff.claimChunks(player, newLocation, null, player.getMetadata("autoClaimingRadius").get(0).asInt(), waitThread);
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

            String oldFQCName = App.factionIOstuff.calcFQCName(oldChunk.getX(), oldChunk.getZ(), null, null);
            String newFQCName = App.factionIOstuff.calcFQCName(newChunk.getX(), newChunk.getZ(), null, null);

            byte oldOwnerID = App.factionIOstuff.getFQC(oldFQCName).getChunkData()[Math.abs(oldChunk.getX()) % 100][Math.abs(oldChunk.getZ()) % 100];
            byte newOwnerID = App.factionIOstuff.getFQC(newFQCName).getChunkData()[Math.abs(newChunk.getX()) % 100][Math.abs(newChunk.getZ()) % 100];

            if (oldOwnerID != newOwnerID){
                if (newOwnerID == -1) player.sendTitle("Entering Wilderness", "Claimable territory", 10, 35, 20);
                else player.sendTitle("Entering: " + App.factionIOstuff.getFaction(newOwnerID).getPrefix(), "", 10, 35, 20);
            }

        }
    }
}