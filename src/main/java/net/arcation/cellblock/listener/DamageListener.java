package net.arcation.cellblock.listener;

import net.arcation.cellblock.api.CellBlockManager;
import net.arcation.cellblock.api.DamageManager;
import net.arcation.cellblock.api.TagManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DamageListener implements Listener {

    private static double potionDamage = 6.0;
    private static final short POTION_UPGRADE_MASK = 1 << 5;
    private static final short POTION_EXTENDED_MASK = POTION_UPGRADE_MASK << 1;
    private static final short POTION_MULTIPLIER_MASK = POTION_EXTENDED_MASK | POTION_UPGRADE_MASK;
    private List<PotionEffectType> damagePotions = Arrays.asList(PotionEffectType.HARM,
            PotionEffectType.POISON, PotionEffectType.WEAKNESS);

    @Inject
    private DamageManager damageManager;

    @Inject
    private TagManager tagManager;


    /**
     * Remove tracking for players who die
     *
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();

        damageManager.clearDamage(p.getUniqueId());
    }


    /**
     * Remove tracking for players who quit
     *
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        // Don't stop tracking tagged players
        if (!tagManager.isPlayerTagged(e.getPlayer().getUniqueId())) {
            damageManager.clearDamage(e.getPlayer().getUniqueId());
        }
    }


    /**
     * Record damage dealt to players
     *
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) e.getEntity();

        final UUID playerId = e.getEntity().getUniqueId();

        Player damager = getPlayerDamager(e);
        if (damager == null || damager == player) {
            return;
        }

        damageManager.addDamage(playerId, damager.getUniqueId(), e.getDamage());
    }

    /**
     * Tracks damage from potions
     *
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent e) {
        ProjectileSource ps = e.getPotion().getShooter();
        LivingEntity shooter;
        if (!(ps instanceof LivingEntity)) {
            return;
        }
        shooter = (LivingEntity) ps;
        if (!(shooter instanceof Player)) {
            return;
        }


        Player damager = (Player) shooter;

        boolean isDamagePotion = false;
        for (PotionEffect effect : e.getPotion().getEffects()) {
            if (damagePotions.contains(effect.getType())) {
                isDamagePotion = true;
                break;
            }
        }

        // No valid effect found to log
        if (!isDamagePotion) {
            return;
        }

        // If a potion is upgraded or extended it will deal twice the base damage
        double damage = potionDamage;
        if ((e.getEntity().getItem().getDurability() & POTION_MULTIPLIER_MASK) > 0) {
            damage *= 2;
        }

        // Deal damage to all affected players
        for (LivingEntity entity : e.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            damageManager.addDamage(entity.getUniqueId(), damager.getUniqueId(),
                    damage * e.getIntensity(entity));
        }
    }

    /**
     * Gets the damager or indirect damager from any projectile.
     *
     * @return The damager player
     */
    public Player getPlayerDamager(final EntityDamageByEntityEvent event) {
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getDamager();
            if (wolf.getOwner() instanceof Player) {
                damager = (Player) wolf.getOwner();
            }
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }
        return damager;
    }
}
