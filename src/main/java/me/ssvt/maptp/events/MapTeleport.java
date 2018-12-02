package me.ssvt.maptp.events;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Vector;

public class MapTeleport implements Listener {

    @EventHandler
    public void onMapClick(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if(entity.getType() == EntityType.ITEM_FRAME){
            ItemFrame itemFrame = (ItemFrame) entity;
            if(itemFrame.getItem().getType() == Material.FILLED_MAP){
                event.setCancelled(true);
                ItemStack mapItem = itemFrame.getItem();
                MapMeta mapMeta = (MapMeta)mapItem.getItemMeta();
                if(mapMeta.hasMapId()){
                    GriefPrevention plugin;
                    plugin = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
                    DataStore dataS = plugin.dataStore;
                    PlayerData pd = dataS.getPlayerData(player.getUniqueId());
                    Vector<Claim> pClaims = pd.getClaims();
                    player.sendMessage(String.valueOf(pClaims.size()));
                    Location tpSpot = claimCenter(pClaims.get(0), player);
                    @SuppressWarnings("deprecation")
                    MapView mapView = Bukkit.getMap((short)mapMeta.getMapId());
                    Location mapCenter = new Location(mapView.getWorld(), mapView.getCenterX(), 0.0, mapView.getCenterZ());
                    Claim tpClaim = closestClaim(mapCenter, pClaims, player);
                    player.teleport(claimCenter(tpClaim, player));
                } else {
                    player.sendMessage("That's an empty map. Why is it in a frame...");
                }
            }
        }
    }

    private Location claimCenter(Claim claim, Player player){
        //Will return the center surface location of the claim.
        //Should be called right before a TP, and this will return a location including the pitch and yaw of the player.
        Location lesserCorner = claim.getLesserBoundaryCorner();
        Location greaterCorner = claim.getGreaterBoundaryCorner();
        Location center = new Location(lesserCorner.getWorld(), (lesserCorner.getX() + greaterCorner.getX()) / 2,
                                                                (lesserCorner.getY() + greaterCorner.getY()) / 2,
                                                                (lesserCorner.getZ() + greaterCorner.getZ()) / 2);
        Block centerBlock = player.getWorld().getHighestBlockAt(center);
        center = centerBlock.getLocation();
        center.setYaw(player.getLocation().getYaw());
        center.setPitch(player.getLocation().getPitch());

        return center;
    }

    private Claim closestClaim(Location center, Vector<Claim> claims, Player player){
        //Returns the closest claim to the location provided.
        center.setY(0.0);
        Claim nearestClaim = null;
        double closestDistance = -1;
        for (Claim claim : claims){
            Location centerOfClaim = claimCenter(claim, player);
            centerOfClaim.setY(0.0);
            double distance = center.distance(centerOfClaim);
            if (closestDistance == -1 || closestDistance > distance){
                nearestClaim = claim;
                closestDistance = distance;
            }
        }

        return nearestClaim;
    }
}