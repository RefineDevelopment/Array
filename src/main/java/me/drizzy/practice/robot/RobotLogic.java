package me.drizzy.practice.robot;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.enums.DifficultyType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.other.PlayerUtil;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Drizzy
 * Created at 5/3/2021
 */

@Getter
@Setter
public class RobotLogic extends BukkitRunnable {

    private final Random random = new Random();

    private final Robot robot;
    private final List<UUID> players;
    private final DifficultyType difficulty;
    
    private boolean kit;
    private boolean navigation;
    private boolean selfHealing;
    private double attackRange;
    private double swingRangeModifier;

    private Player target;

    public RobotLogic(Robot robot, List<UUID> players, DifficultyType difficulty) {
        this.robot = robot;
        this.target = null;
        this.players = players;
        this.difficulty = difficulty;
        
        this.initiateLogic();
    }
    
    public void initiateLogic() {
        int delay = 2;
        this.attackRange = 3.2;
        
        switch (difficulty) {
            case EASY:
                this.attackRange *= 0.5;
                this.swingRangeModifier = -0.8;
                break;
            case MEDIUM:
                this.attackRange *= 0.8;
                this.swingRangeModifier = -0.5;
                break;
            case HARD:
                this.attackRange *= 2.0;
                this.swingRangeModifier = 2.0;
                break;
            case EXPERT:
                this.attackRange *= 2.6;
                this.swingRangeModifier = 3.0;
                delay = 1;
                break;
        }
        this.runTaskTimerAsynchronously(Array.getInstance(), 60L, delay);
    }

    /**
     * Apply a certain kit to the robot
     * along with its knockback profile
     *
     * @param kit The kit being applied
     */
    private void giveKit(Kit kit) {
        kit.applyToRobot(robot);
        this.kit = true;
    }

    /**
     * Attempt to self regenerate the robot's health
     * by eating a golden apple if it is present in the hotbar
     */
    private void heal() {
        if (this.selfHealing) {
            return;
        }

        final Damageable damageable = robot.getPlayer();

        if (damageable.getHealth() <= 13.0 && this.random.nextBoolean() && !this.splashPotion() && !this.useSoupRefill()) {
            this.useGoldenApple();
        }
    }

    /**
     * Attempt to eat a golden apple from the robot's
     * hotbar and apply the potion effects
     */
    private void useGoldenApple() {
        ItemStack gapple = null;
        
        for ( final ItemStack items : robot.getPlayer().getInventory().getContents() ) {
            if (items != null && items.getType() == Material.GOLDEN_APPLE) {
                gapple = items.clone();
            }
        }
        
        if (gapple != null) {

            this.selfHealing = true;
            ItemStack finalGapple = gapple;
            ItemStack hand = null;

            for ( int i = 0; i < 9; ++i ) {
                if (robot.getPlayer().getInventory().getItem(i) != null && robot.getPlayer().getInventory().getItem(i).equals(gapple)) {
                    hand=robot.getPlayer().getInventory().getItem(i);
                    robot.getPlayer().getInventory().setHeldItemSlot(i);
                    break;
                }
            }

            if (hand == null) {
                robot.getPlayer().getInventory().setHeldItemSlot(1);
                PlayerUtil.removeItems(robot.getPlayer().getInventory(), finalGapple, 1);
                for ( int i = 9; i < 36; ++i ) {
                    if (robot.getPlayer().getInventory().getItem(i) == null || robot.getPlayer().getInventory().getItem(i).getType() == Material.AIR) {
                        robot.getPlayer().getInventory().setItem(i, robot.getPlayer().getItemInHand());
                        break;
                    }
                }
                robot.getPlayer().setItemInHand(gapple);
            }

            new BukkitRunnable() {
                public void run() {
                    robot.getPlayer().setItemInHand(finalGapple);
                    try {
                        final Class<?> clazz = PlayerAnimation.class;

                        clazz.getField("START_USE_MAINHAND_ITEM");

                        try {
                            PlayerAnimation.START_USE_MAINHAND_ITEM.play(robot.getPlayer());
                        } catch (NoSuchFieldError noSuchFieldError) {
                            Array.logger("&cError! START_USE_MAINHAND_ITEM does not exist!");
                        }

                        clazz.getField("EAT_FOOD");

                        try {
                            PlayerAnimation.EAT_FOOD.play(robot.getPlayer());
                        } catch (NoSuchFieldError noSuchFieldError2) {
                            Array.logger("&cError! EAT_FOOD does not exist!");
                        }

                    } catch (Exception ex) {
                        Array.logger("&cError! PlayerAnimation.class does not exist!");
                    }

                    new BukkitRunnable() {
                        public void run() {
                            if (robot.getPlayer() != null) {
                                robot.getNpc().getNavigator().setPaused(true);
                                robot.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                                finalGapple.setAmount(1);
                                if (finalGapple.getDurability() == 0) {
                                    robot.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                                    robot.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1));
                                } else {
                                    robot.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 4));
                                    robot.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1));
                                    robot.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
                                    robot.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
                                }
                                PlayerUtil.removeItems(robot.getPlayer().getInventory(), finalGapple, finalGapple.getAmount());

                                new BukkitRunnable() {
                                    public void run() {
                                        if (robot.getNpc() != null && robot.isSpawned() && robot.getNpc().getNavigator() != null) {
                                            robot.getNpc().getNavigator().setPaused(false);
                                            robot.getPlayer().getInventory().setHeldItemSlot(0);
                                            selfHealing=false;
                                        }
                                    }
                                }.runTaskLater(Array.getInstance(), (random.nextInt(4) + 2));

                            }
                        }

                    }.runTaskLater(Array.getInstance(), 35L);

                }

            }.runTaskLater(Array.getInstance(), (this.random.nextInt(2) + 1));
        }
    }

    /**
     * Attempt to use soup and refill it
     * after consuming it from the robot's hotbar
     *
     * @return {@link Boolean}
     */
    private boolean useSoupRefill() {
        ItemStack soup = null;
        for (final ItemStack is : robot.getPlayer().getInventory().getContents()) {
            if (is != null && is.getType() == Material.MUSHROOM_SOUP) {
                soup = is.clone();
            }
        }
        if (soup != null) {
            final ItemStack finalSoup = soup;
            ItemStack hand = null;

            robot.getNpc().getNavigator().setPaused(true);
            selfHealing = true;

            for (int i = 0; i < 9; ++i) {
                if (robot.getPlayer().getInventory().getItem(i) != null && robot.getPlayer().getInventory().getItem(i).equals(soup)) {
                    hand = robot.getPlayer().getInventory().getItem(i);
                    robot.getPlayer().getInventory().setHeldItemSlot(i);
                    break;
                }
            }

            if (hand == null) {
                robot.getPlayer().getInventory().setHeldItemSlot(1);
                PlayerUtil.removeItems(robot.getPlayer().getInventory(), finalSoup, 1);

                for (int i = 9; i < 36; ++i) {
                    if (robot.getPlayer().getInventory().getItem(i) == null || robot.getPlayer().getInventory().getItem(i).getType() == Material.AIR) {
                        robot.getPlayer().getInventory().setItem(i, robot.getPlayer().getItemInHand());
                        break;
                    }
                }
                robot.getPlayer().setItemInHand(soup);
            }

            robot.getPlayer().setItemInHand(finalSoup);

            new BukkitRunnable() {
                public void run() {
                    if (robot.getPlayer() == null || robot.getPlayer().isDead()) {
                        return;
                    }

                    PlayerUtil.removeItems(robot.getPlayer().getInventory(), finalSoup, finalSoup.getAmount());

                    if (!robot.getPlayer().isDead()) {
                        Damageable damage = robot.getPlayer();
                        damage.setHealth((damage.getHealth() < 13.0) ? (damage.getHealth() + 7.0) : 20.0);
                        
                        Class<?> clazz = PlayerAnimation.class;
                        try {
                            clazz.getField("START_USE_MAINHAND_ITEM");
                            try {
                                PlayerAnimation.START_USE_MAINHAND_ITEM.play(robot.getPlayer());
                            } catch (NoSuchFieldError e) {
                              Array.logger("&cError! START_USE_MAINHAND_ITEM does not exist!");  
                            }
                        } catch (Exception ex) {
                            Array.logger("&cError! START_USE_MAINHAND_ITEM does not exist!");
                        }

                        robot.getPlayer().setItemInHand(new ItemStack(Material.BOWL));

                        new BukkitRunnable() {
                            public void run() {
                                if (robot.getNpc() != null && robot.isSpawned() && robot.getNpc().getNavigator() != null) {
                                    robot.getNpc().getNavigator().setPaused(false);
                                    final ItemStack is = robot.getPlayer().getItemInHand();
                                    if (is != null) {
                                        robot.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                                        robot.getPlayer().getInventory().setHeldItemSlot(0);
                                        selfHealing = false;
                                    }
                                }
                            }
                        }.runTaskLater(Array.getInstance(), (random.nextInt(1) + 1));

                    }
                }
            }.runTaskLater(Array.getInstance(), (this.random.nextInt(1) + 1));
            return true;
        }
        return false;
    }

    /**
     * Select and splash a potion for the robot
     * if it is present in robot's hotbar
     *
     * @return {@link Boolean}
     */
    private boolean splashPotion() {
        if (!robot.getPlayer().isOnGround() && robot.getPlayer().getLocation().getY() - robot.getPlayer().getLocation().getBlockY() > 0.35 && this.random.nextInt(3) == 0) {
            return false;
        }

        ItemStack pot = null;

        for (final ItemStack item : robot.getPlayer().getInventory().getContents()) {
            if (item != null && ((item.getType() == Material.POTION && (item.getDurability() == 16421 || item.getDurability() == 16453)) || item.getDurability() == 438)) {
                pot = item.clone();
                break;
            }
        }

        if (pot != null) {
            final ItemStack finalPot = pot;

            this.selfHealing = true;
            ItemStack hand = null;

            for (int i = 0; i < 9; ++i) {
                if (robot.getPlayer().getInventory().getItem(i) != null && robot.getPlayer().getInventory().getItem(i).equals(pot)) {
                    hand = robot.getPlayer().getInventory().getItem(i);
                    robot.getPlayer().getInventory().setHeldItemSlot(i);
                    break;
                }
            }

            if (hand == null) {
                robot.getPlayer().getInventory().setHeldItemSlot(1);
                PlayerUtil.removeItems(robot.getPlayer().getInventory(), finalPot, 1);
                for (int i = 9; i < 36; ++i) {
                    if (robot.getPlayer().getInventory().getItem(i) == null || robot.getPlayer().getInventory().getItem(i).getType() == Material.AIR) {
                        robot.getPlayer().getInventory().setItem(i, robot.getPlayer().getItemInHand());
                        robot.getPlayer().getInventory().setHeldItemSlot(1);
                        break;
                    }
                }
                robot.getPlayer().setItemInHand(pot);
            }

            robot.getPlayer().getInventory().setHeldItemSlot(this.random.nextInt(8) + 1);
            robot.getPlayer().setItemInHand(finalPot);

            Location behind = robot.getPlayer().getLocation().add(robot.getPlayer().getLocation().getDirection().normalize().multiply(-5)).subtract(0.0, 10.0, 0.0);
            robot.getNpc().getNavigator().setTarget(behind);

            new BukkitRunnable() {
                final int targetCounter = random.nextInt(5) + 5;
                int counter = this.targetCounter;

                public void run() {
                    if (robot.getNpc() != null && robot.isSpawned() && robot.getNpc().getNavigator() != null) {
                        --this.counter;
                        if (this.counter == 0 || Math.abs(robot.getPlayer().getLocation().getPitch() - 90.0f) < 50.0f) {
                            this.cancel();
                            robot.swing();
                            final ThrownPotion thrownPotion = getThrownPotion(finalPot);
                            new BukkitRunnable() {
                                public void run() {
                                    if (selfHealing && robot.getNpc() != null && robot.getNpc().isSpawned() && !robot.getPlayer().isDead() && !thrownPotion.isDead()) {
                                        robot.getNpc().getNavigator().setTarget(thrownPotion.getLocation());
                                    }
                                    else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Array.getInstance(), 1L, 1L);

                            robot.getPlayer().setItemInHand(new ItemStack(Material.AIR));

                            PlayerUtil.removeItems(robot.getPlayer().getInventory(), finalPot, 1);
                            Damageable damageable = robot.getPlayer();

                            if (damageable.getHealth() < 12.0) {
                                ItemStack pot = null;
                                for (final ItemStack item : robot.getPlayer().getInventory().getContents()) {
                                    if (item != null && item.getType() == Material.POTION && (item.getDurability() == 16421 || item.getDurability() == 16453)) {
                                        pot = item.clone();
                                        break;
                                    }
                                }
                                if (pot != null) {
                                    robot.swing();
                                    getThrownPotion(pot);
                                    PlayerUtil.removeItems(robot.getPlayer().getInventory(), pot, 1);
                                }
                            }

                            new BukkitRunnable() {
                                public void run() {
                                    if (robot.getNpc() != null && robot.isSpawned() && robot.getNpc().getNavigator() != null) {
                                        robot.getPlayer().getInventory().setHeldItemSlot(0);
                                        selfHealing = false;
                                    }
                                }
                            }.runTaskLater(Array.getInstance(), (random.nextInt(12) + 8));
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Array.getInstance(), 1L, 1L);
            return true;
        }
        return robot.isDestroyed();
    }

    /**
     * Returns a thrown potion from its ItemStack
     *
     * @param potion The Itemstack of the Potion
     * @return The potion being thrown
     */
    private ThrownPotion getThrownPotion(ItemStack potion) {
        final ThrownPotion thrownPotion = (ThrownPotion) robot.getPlayer().getWorld().spawnEntity(robot.getPlayer().getLocation(), EntityType.SPLASH_POTION);

        thrownPotion.getEffects().addAll(Potion.fromItemStack(potion).getEffects());
        thrownPotion.setShooter(robot.getPlayer());
        thrownPotion.setItem(potion);

        final Vector vec = robot.getPlayer().getLocation().getDirection();

        if (vec.getY() == 0.0) {
            vec.setY(-this.random.nextInt(2) + 1 + this.random.nextDouble() / 10.0);
        }
        thrownPotion.setVelocity(vec);

        return thrownPotion;
    }
    
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (this.players == null || this.players.isEmpty() || robot.isDestroyed()) {
            this.cancel();
            return;
        }

        if (robot.isSpawned() && robot.getPlayer() != null) {
            if (!this.kit) {
                robot.getNpc().setProtected(false);
                this.giveKit(robot.getKit());
            }

            if (!this.navigation) {
                this.navigation = true;
                if (this.difficulty == DifficultyType.HARD) {
                    robot.getNpc().getNavigator().getLocalParameters().speedModifier(1.33f);
                } else if (this.difficulty == DifficultyType.EXPERT) {
                    robot.getNpc().getNavigator().getLocalParameters().speedModifier(1.66f);
                }
                robot.getNpc().getNavigator().getLocalParameters().attackRange(this.attackRange);
                robot.getNpc().getNavigator().getLocalParameters().stuckAction((npc, navigationgator) -> false);
            }

            if (!robot.getPlayer().isDead() && robot.getPlayer().getLocation().getBlockY() < 0) {
                robot.getPlayer().setHealth(0.0);
                return;
            }

            if (robot.getPlayer().getVelocity().getY() < 0.1 && robot.getPlayer().getVelocity().getY() > -0.0784) {
                final Vector v = robot.getNpc().getEntity().getVelocity();
                robot.getNpc().getEntity().setVelocity(v.setY(-0.0784));
            }
            double distance = (this.target != null && this.target.getWorld().getName().equals(robot.getPlayer().getWorld().getName())) ? this.target.getLocation().distanceSquared(robot.getPlayer().getLocation()) : 22500.0;

            if (robot.getNpc().getNavigator().getTargetAsLocation() == null || this.random.nextInt(10) == 0) {
                for (final UUID uuid : this.players) {
                    final Player pl = Bukkit.getPlayer(uuid);
                    if (pl != null && pl.getWorld().getName().equals(robot.getPlayer().getWorld().getName())) {
                        final double dis = robot.getPlayer().getLocation().distanceSquared(pl.getLocation());
                        if (dis >= distance) {
                            continue;
                        }
                        this.target = pl;
                        distance = dis;
                    }
                }
            }

            if (this.target != null && !this.selfHealing) {
                if (distance <= this.attackRange * this.attackRange * 1.5 && this.random.nextDouble() > 0.2) {
                    robot.getNpc().getNavigator().setTarget(this.target, true);
                } else {
                    robot.getNpc().getNavigator().setTarget(this.target.getLocation());
                }
                robot.getNpc().getNavigator().setPaused(false);
            }

            if (robot.getNpc().getNavigator().getTargetAsLocation() != null) {
                robot.getPlayer().setSprinting(true);
            }

            final double x = this.attackRange + this.swingRangeModifier + this.random.nextDouble() * 3.0;
            if (distance < x * x && !robot.getNpc().getNavigator().isPaused() && !this.selfHealing) {
                robot.swing();
            }
            if (!robot.getPlayer().isDead()) {
                this.heal();
            }
        }
    }
}
