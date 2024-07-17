package com.aquobus.antagoncore.kingdoms.ultimaaddon.handlers;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.abstraction.data.KingdomItemBuilder;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.constants.land.location.SimpleLocation;
import org.kingdoms.constants.land.structures.Structure;
import org.kingdoms.constants.land.structures.StructureRegistry;
import org.kingdoms.constants.land.structures.StructureStyle;
import org.kingdoms.constants.land.structures.StructureType;
import org.kingdoms.constants.metadata.KingdomMetadata;
import org.kingdoms.constants.metadata.StandardKingdomMetadata;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.constants.player.StandardKingdomPermission;
import org.kingdoms.events.items.KingdomItemBreakEvent;
import org.kingdoms.events.lands.ClaimLandEvent;
import org.kingdoms.events.lands.NexusMoveEvent;
import org.kingdoms.events.lands.UnclaimLandEvent;
import org.kingdoms.main.Kingdoms;
import org.kingdoms.managers.land.indicator.LandVisualizer;
import org.kingdoms.services.managers.ServiceHandler;
import org.kingdoms.utils.nbt.ItemNBT;
import org.kingdoms.utils.nbt.NBTType;
import org.kingdoms.utils.nbt.NBTWrappers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OutpostListener implements Listener {
    private static final Set<Structure> justRemoved = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOutpostBreak(KingdomItemBreakEvent<Structure> e) {
        if (!(e.getKingdomItem() instanceof Structure)) {
            return;
        }

        Structure structure = e.getKingdomItem();
        if (!structure.getNameOrDefault().equals("Outpost")) {
            return;
        }

        // Stop recursion
        if (justRemoved.contains(structure)) {
            justRemoved.remove(structure);
            return;
        }

        // Allow if structure was removed by Kingdoms or a player w/o kingdom
        KingdomPlayer kp = e.getPlayer();
        if (kp == null || !kp.hasKingdom()) {
            return;
        }

        // Allow if structure has no land (if kingdom did unclaimall or disbanded)
        if (!structure.getLand().isClaimed()) {
            return;
        }

        e.setCancelled(true);

        // Must not be in war
        Player p = kp.getPlayer();
        if (Utils.hasChallenged(kp.getKingdom())) {
            Utils.msg(p, "&cВы не можете этого сделать, поскольку вам бросили вызов или вы в состоянии войны.");
            return;
        }

        if (!kp.hasPermission(StandardKingdomPermission.UNCLAIM)) {
            Utils.msg(p, "&cУ вашего ранга в королевстве должно быть разрешение на удаление аванпостов без запроса!");
            return;
        }

        // For some reason without the 1 tick delay it skips the confirmation screen
        p.closeInventory();
        Utils.schedule(1, () -> {
            justRemoved.add(structure);
            structure.remove();
            int amt = Utils.unclaimOutpost(kp, kp.getKingdom(), structure);
            Utils.msg(p, "&2Ты потерял &6" + amt + " &2чанк(ов).");
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOutpostPlace(PlayerInteractEvent e) {
        // Check if item is kingdom item
        Block b = e.getClickedBlock();
        if (b == null) {
            return;
        }

        // Must be a right click (place) action 
        if (!e.getAction().toString().contains("RIGHT")) {
            return;
        }

        // Must be able to place
        Block pb = b.getRelative(e.getBlockFace());
        if (pb.getType() != Material.AIR) {
            return;
        }

        ItemStack item = e.getItem();
        NBTWrappers.NBTTagCompound nbt = ItemNBT.getTag(item);
        nbt = nbt.getCompound(Kingdoms.NBT);
        if (nbt == null) {
            return;
        }

        // Check if its a structure
        String tag = nbt.get(StructureType.METADATA, NBTType.STRING);
        if (tag == null || !tag.equals("outpost")) {
            return;
        }

        // Hack to not let the method pass to Kingdoms by setting it to air
        // This works probably because the event stores a copy of the item
        // so when you set the type it doesn't affect the player(?)
        e.setCancelled(true);
        Player p = e.getPlayer();
        Material type = item.getType();
        item.setType(Material.AIR);

        // Only allow outpost to be placed on unclaimed land not in the end
        SimpleChunkLocation scl = SimpleChunkLocation.of(pb);
        if (p.getWorld().getName().equals("world_the_end") || ServiceHandler.isInRegion(scl)) {
            Utils.msg(p, "&cВы не можете создать здесь аванпост!");
            return;
        }

        // Check if land is claimed.
        Land land = Land.getLand(scl);
        if (land == null) {
            land = new Land(scl);
        }

        if (land.isClaimed()) {
            Utils.msg(e.getPlayer().getPlayer(), "&cВы можете размещать аванпосты только на невостребованных землях!");
            return;
        }

        // Must have kingdom
        KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(p);
        if (!kp.hasKingdom()) {
            Utils.msg(p, "&cВы должны быть в королевстве чтобы использовать это!");
            return;
        }

        // Must have appropriate perms
        if (!kp.hasPermission(StandardKingdomPermission.CLAIM) ||
                !kp.hasPermission(StandardKingdomPermission.STRUCTURES)) {
            Utils.msg(p, "&cЧтобы создать аванпост, у вашего ранга в королевстве должны быть разрешения как на ПРЕТЕНЗИИ, так и на СТРУКТУРЫ!");
            return;
        }

        // Must have a nexus
        Kingdom k = kp.getKingdom();
        if (k.getNexus() == null) {
            Utils.msg(p, "&cВы должны поставить Нексус через &a/k nexus &cперед приватом новых территорий!");
            return;
        }

        // Must have less than 3 placed outposts
        if (k.getAllStructures().stream().filter(s -> s.getNameOrDefault().equals("Outpost")).count() >=
                StructureRegistry.getStyle("outpost").getOption("limits", "total").getInt()) {
            Utils.msg(p, "&cВаше королевство достигло лимита в количестве аванпостов!");
            return;
        }

        // Must be less than max lands
        if (k.getLandLocations().size() >= k.getMaxClaims()) {
            Utils.msg(p, "&cВаше королевство уже достигло лимита приватов!");
            return;
        }

        // Must not be in war
        if (Utils.hasChallenged(k)) {
            Utils.msg(p, "&cВы не можете использовать это, поскольку вам бросили вызов или вы бросили вызов другим королевством.");
            return;
        }

        pb.setType(type);

        // Kingdoms spawn structure
        SimpleLocation sl = SimpleLocation.of(pb);
        k.claim(scl, kp, ClaimLandEvent.Reason.ADMIN);
        StructureStyle outpostStyle = StructureRegistry.getStyle("outpost");
        Structure outpost = outpostStyle.getType().build(
                new KingdomItemBuilder<Structure, StructureStyle, StructureType>(outpostStyle, SimpleLocation.of(pb), kp));
        land.getStructures().put(sl, outpost);
        outpost.spawnHolograms(k);
        outpost.playSound("place");
        outpost.displayParticle("place");
        Utils.msg(p, "&2Захвачена территория аванпоста на координатах &6" + scl.getX() + "&7, &6" + scl.getZ());

        // Add metadata
        // ID is simply cur time, no way 2 people put an outpost at the same milisecond...
        StandardKingdomMetadata skm = new StandardKingdomMetadata(System.currentTimeMillis());
        land.getMetadata().put(Utils.outpost_id, skm);
        outpost.getMetadata().put(Utils.outpost_id, skm);

        // Remove item amount
        ItemStack hand = p.getInventory().getItem(e.getHand());
        hand.setAmount(hand.getAmount() - 1);

        // Visualize lands
        new LandVisualizer().forPlayer(p, kp).forLand(land, scl.toChunk()).displayIndicators();
    }

    // Only allow claiming lands if nexus was placed/add outpost IDs
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLandClaim(ClaimLandEvent e) {
        // Allow claiming if currently kingdom has 0 lands
        Kingdom k = e.getKingdom();
        if (k.getLandLocations().size() == 0) {
            return;
        }

        // Also returns if this land was claimed through means of an outpost
        if (e.getReason() == ClaimLandEvent.Reason.ADMIN) {
            return;
        }

        // Must have a nexus
        Player p = e.getPlayer().getPlayer();
        if (k.getNexus() == null) {
            e.setCancelled(true);
            Utils.msg(p, "&cВы должны поставить Нексус с помощью &a/k nexus &c, прежде чем захватывать больше земель!");
            return;
        }

        // Disable claiming if there are no other claims in the same world
        Set<SimpleChunkLocation> chunks = e.getLandLocations();
        if (!k.getLandLocations().stream().anyMatch(scl -> scl.getWorld().equals(p.getWorld().getName()))) {
            e.setCancelled(true);
            Utils.msg(p, "&cВаша земля должна быть присоединена к остальным землям королевства.");
            return;
        }

        // Find outpost metadata and add it if available
        // Assume getLandLocations only returns successful claims
        Bukkit.getScheduler().runTaskLaterAsynchronously(AntagonCore.getPlugin(), () -> {
            UUID kid = k.getId();
            Set<SimpleChunkLocation> checked = new HashSet<>();
            long outpost_id = 0;
            for (SimpleChunkLocation scl : chunks) {
                for (SimpleChunkLocation sclnear : scl.getChunksAround(1)) {
                    // Continue if this was one of the recently claimed lands
                    if (chunks.contains(sclnear)) {
                        continue;
                    }

                    // Continue if we already checked this land before
                    if (checked.contains(sclnear)) {
                        continue;
                    }

                    // Continue if the land is unclaimed, or not claimed by same kingdom
                    Land scll = sclnear.getLand();
                    if (scll == null) {
                        checked.add(sclnear);
                        continue;
                    }

                    UUID nearclaim = scll.getKingdomId();
                    if (nearclaim == null || !nearclaim.equals(kid)) {
                        checked.add(sclnear);
                        continue;
                    }

                    // If any surrounding land doesn't have metadata, it means its a nexus land
                    KingdomMetadata data = scll.getMetadata().get(Utils.outpost_id);
                    if (data == null) {
                        return;
                    }

                    // Otherwise log an outpost id
                    outpost_id = ((StandardKingdomMetadata) data).getLong();
                }

                // This checks if this is the first chunk that a kingdom claimed during an invasion.
                // We return after because invasions can only have 1 land claim at a time.
                // Assign a negative outpost ID so that this land still works with custom disconnectLands
                // function, and also to allow nexus to be moved to an invasion spot.
                if (e.getReason() == ClaimLandEvent.Reason.INVASION && checked.size() == 8) {
                    long ctime = System.currentTimeMillis();
                    scl.getLand().getMetadata().put(Utils.outpost_id, new StandardKingdomMetadata(-1 * ctime));
                    return;
                }
            }

            if (outpost_id == 0) {
                return;
            }

            // If we got to this point, an outpost land must've been found. Then add the metadata to all claimed chunks
            long finalid = outpost_id;
            chunks.forEach(c -> c.getLand().getMetadata().put(Utils.outpost_id, new StandardKingdomMetadata(finalid)));
        }, 1);
    }

    // Stop unclaiming of outpost chunk
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnclaim(UnclaimLandEvent e) {
        // This method checks if this event would unclaim an outpost
        if (cancelUnclaim(e)) {
            return;
        }

        // Remove metadata
        e.getLandLocations().forEach(scl -> {
            scl.getLand().getMetadata().remove(Utils.outpost_id);
        });
    }

    // Checks if a land can be unclaimed
    private boolean cancelUnclaim(UnclaimLandEvent e) {
        // Don't check if unclaimall was done
        if (e.getLandLocations().size() > 1) {
            return false;
        }

        // Don't check if cause was invasion - OnInvadeSuccess checks for that instead
        if (e.getReason() == UnclaimLandEvent.Reason.INVASION || e.getReason() == UnclaimLandEvent.Reason.ADMIN) {
            return false;
        }

        for (SimpleChunkLocation scl : e.getLandLocations()) {
            if (scl.getLand().getStructures().values().stream().anyMatch(s -> s.getNameOrDefault().equals("Outpost"))) {
                e.setCancelled(true);
                Utils.msg(e.getPlayer().getPlayer(), "&cЧтобы расприватить территорию, сломайте аванпост.");
                return true;
            }
        }

        return false;
    }

    // Disallow nexus to be moved to an outpost chunk
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNexusMove(NexusMoveEvent e) {
        Land l = e.getTo().toSimpleChunkLocation().getLand();
        KingdomMetadata meta = l.getMetadata().get(Utils.outpost_id);
        if (meta == null) {
            return;
        }

        // Only check if meta is positive, meaning time > 0
        if (((StandardKingdomMetadata) meta).getLong() > 0) {
            e.setCancelled(true);
            Utils.msg(e.getPlayer().getPlayer(), "&cВы не можете поставить Нексус на территории аванпоста.");
        }
    }
}