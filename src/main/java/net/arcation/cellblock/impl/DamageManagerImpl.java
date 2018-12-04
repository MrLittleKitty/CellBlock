package net.arcation.cellblock.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.arcation.cellblock.api.Clock;
import net.arcation.cellblock.api.DamageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class DamageManagerImpl implements DamageManager {

    private double maxDamage = 30.0;

    @Inject
    private Clock clock;
    private final Map<UUID, DamageLog> damageLogs = new HashMap<UUID, DamageLog>();


    @Override
    public void addDamage(UUID player, UUID damager, double amount) {
        DamageLog rec = damageLogs.get(player);
        if (rec == null) {
            rec = new DamageLog(player);
            damageLogs.put(player, rec);
        }

        rec.recordDamage(damager, amount, maxDamage);
    }

    @Override
    public List<Player> getOrderedDamagers(UUID player) {
        final List<Player> players = new ArrayList<Player>();
        final DamageLog log = damageLogs.get(player);

        if (log == null) {
            return players;
        }

        // Algorithm 0 sorts by most recent
        // Algorithm 1 sorts by greatest damage
        Collection<DamageRecord> recs = log.getDamageSortedDamagers();
//        if (algorithm == 0) {
//            recs = log.getTimeSortedDamagers();
//        } else {
//            recs = log.getDamageSortedDamagers();
//        }

        for (DamageRecord rec : recs) {
            Player p = Bukkit.getPlayer(rec.getDamager());
            if (p != null && p.isOnline()) {
                players.add(p);
            }
        }

        return players;
    }

    @Override
    public void clearDamage(UUID player) {
        damageLogs.remove(player);
    }


    /**
     * Logs damage dealt to a player by other players.
     *
     * @author Gordon
     */
    @EqualsAndHashCode
    @ToString
    class DamageLog {
        @Getter
        private final UUID playerId;
        private final Map<UUID, DamageRecord> damagers = new HashMap<UUID, DamageRecord>();

        /**
         * Creates a new DamageLog instance
         *
         * @param playerId The player Id
         */
        public DamageLog(UUID playerId) {
            this.playerId = playerId;
        }


        /**
         * Records damage for the player
         *
         * @param damager   The damager
         * @param amount    The damage amount
         * @param maxAmount The max damage amount that should be tracked
         */
        public void recordDamage(UUID damager, double amount, double maxAmount) {
            DamageRecord rec = damagers.get(damager);
            if (rec == null) {
                rec = new DamageRecord(damager);
                damagers.put(damager, rec);
            }

            rec.recordDamage(amount, maxAmount);
        }

        /**
         * Decays all the damage records by a given amount.
         * <p>
         * Once the damage amount for a particular player reaches zero,
         * it is removed from tracking.
         * <p>
         * When there are no longer any players being tracked, then
         * this method returns false to indicate that it can be removed.
         *
         * @param decayAmount The damage amount to decay
         * @return true if there are still damagers being tracked
         */
        public boolean decayDamage(double decayAmount) {
            Iterator<DamageRecord> it = damagers.values().iterator();
            while (it.hasNext()) {
                final DamageRecord rec = it.next();
                if (!rec.decayDamage(decayAmount)) {
                    it.remove();
                }
            }
            return damagers.size() > 0;
        }

        /**
         * Gets the time-sorted damagers
         * The first object in the list will be the most recent damager and
         * the last object will be the least recent damager.
         *
         * @return The time-sorted damager players
         */
        public List<DamageRecord> getTimeSortedDamagers() {
            List<DamageRecord> recs = new LinkedList<DamageRecord>(damagers.values());

            Collections.sort(recs, new Comparator<DamageRecord>() {
                public int compare(DamageRecord o1, DamageRecord o2) {
                    if (o1.getTime() == o2.getTime()) {
                        return 0;
                    }
                    return o1.getTime() > o2.getTime() ? -1 : 1;
                }
            });

            return recs;
        }

        /**
         * Gets the damage-sorted damagers
         * The first object in the list will be the player who damaged the most and
         * the last object will be the player who damaged the least.
         *
         * @return The damage-sorted damager players
         */
        public List<DamageRecord> getDamageSortedDamagers() {
            List<DamageRecord> recs = new LinkedList<DamageRecord>(damagers.values());

            Collections.sort(recs, new Comparator<DamageRecord>() {
                public int compare(DamageRecord o1, DamageRecord o2) {
                    if (o1.getAmount() == o2.getAmount()) {
                        return 0;
                    }
                    return o1.getAmount() > o2.getAmount() ? -1 : 1;
                }
            });

            return recs;
        }
    }

    /**
     * Tracks damage dealt from a player.
     *
     * @author Gordon
     */
    @EqualsAndHashCode
    @ToString
    private final class DamageRecord {

        @Getter
        private final UUID damager;

        @Getter
        @Setter
        private double amount;

        @Getter
        @Setter
        private long time;

        /**
         * Creates a new DamageRecord instance
         *
         * @param damager The damager Id
         */
        public DamageRecord(final UUID damager) {
            this.damager = damager;
            this.amount = 0;
            this.time = clock.getCurrentTime();
        }

        /**
         * Adds damage to the record and marks the current time
         *
         * @param amount    The damage amount
         * @param maxAmount The max amount that should be tracked
         */
        public void recordDamage(double amount, double maxAmount) {
            this.amount = Math.min(maxAmount, this.amount + amount);
            this.time = clock.getCurrentTime();
        }

        /**
         * Decays the damage by an amount
         *
         * @param decayAmount The damage amount to decay
         * @return true if the record is still valid
         */
        public boolean decayDamage(double decayAmount) {
            amount = Math.max(0, amount - decayAmount);
            return amount > 0;
        }
    }
}
