package me.justacat.luxurycombat;

import com.google.common.cache.Cache;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class Events implements Listener {

    public static Cache<UUID, Long> combat;


    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player attacker && e.getEntity() instanceof Player victim) {

            combat.put(attacker.getUniqueId(), 0L);
            combat.put(victim.getUniqueId(), 0L);


        }


    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        if (isInCombat(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(LuxuryCombat.colorMessage(LuxuryCombat.instance.getConfig().getString("prefix") + LuxuryCombat.instance.getConfig().getString("message")));
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        e.getPlayer().setHealth(0);

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        if (!isInCombat(e.getPlayer())) return;

        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = regionContainer.createQuery();

        ApplicableRegionSet set1 = query.getApplicableRegions(BukkitAdapter.adapt(from));
        ApplicableRegionSet set2 = query.getApplicableRegions(BukkitAdapter.adapt(to));

        if (!set1.getRegions().equals(set2.getRegions())) {

            e.setCancelled(true);

            Location minus = to.clone().add(from.multiply(-1));



            for (int y = -2; y < 3; y++) {

                for (int xz = -3; xz < 4; xz++) {

                    Location location = to.clone();
                    location.setY(to.getY() + y);

                    if (minus.getZ() > minus.getX()) {

                        location.setZ(to.getZ() + xz);

                    } else {

                        location.setX(to.getX() + xz);

                    }

                    e.getPlayer().sendBlockChange(location, Material.RED_STAINED_GLASS.createBlockData());


                }

            }


        }


    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        if (isInCombat(e.getPlayer())) {

            combat.asMap().remove(e.getPlayer().getUniqueId());

        }

    }


    public boolean isInCombat(Player player) {
        return combat.asMap().containsKey(player.getUniqueId());
    }




}
