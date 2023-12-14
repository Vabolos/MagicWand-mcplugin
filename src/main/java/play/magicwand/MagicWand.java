package play.magicwand;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Objects;

public class MagicWand extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("MagicWand has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("MagicWand has been disabled!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Check if it's a left-click with a stick
        if (item != null && item.getType() == Material.STICK && event.getAction() == Action.LEFT_CLICK_AIR) {
            createFireworkTrail(player);
        }
    }

    private void createFireworkTrail(Player player) {
        Location playerLoc = player.getEyeLocation();
        Vector direction = playerLoc.getDirection();
        final int trailLength = 40; // Length of the firework trail
        final double spaceBetweenFireworks = 1.5; // Space between each firework

        for (int i = 0; i < trailLength; i++) {
            Location spawnLoc = playerLoc.clone().add(direction.clone().multiply(i * spaceBetweenFireworks));
            spawnFirework(spawnLoc);
        }

        // Create the final explosion at the end of the trail
        spawnFinalExplosion(playerLoc.clone().add(direction.clone().multiply(trailLength * spaceBetweenFireworks)));
    }

    private void spawnFirework(Location location) {
        Firework firework = Objects.requireNonNull(location.getWorld()).spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(1); // Set the power of the fireworks

        // Customize the fireworks with a smaller particle effect (BALL) for the trail
        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.WHITE)
                .withColor(Color.AQUA)
                .flicker(true)
                .trail(true)
                .build());

        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    private void spawnFinalExplosion(Location location) {
        Firework firework = Objects.requireNonNull(location.getWorld()).spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(1); // Set the power of the fireworks

        // Customize the fireworks for the final explosion (LARGE_BALL and BURST)
        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.WHITE)
                .withColor(Color.AQUA)
                .trail(true)
                .build());

        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(Color.WHITE)
                .withColor(Color.AQUA)
                .trail(true)
                .build());

        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    @EventHandler
    public void onFireworkExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Firework) {
            Location loc = event.getLocation();

            // Create an explosion with fire at the impacted block
            Objects.requireNonNull(loc.getWorld()).createExplosion(loc, 2.0F, true, true);
        }
    }
}
