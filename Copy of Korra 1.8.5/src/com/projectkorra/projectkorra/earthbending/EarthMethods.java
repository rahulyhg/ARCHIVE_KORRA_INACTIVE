package com.projectkorra.projectkorra.earthbending;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.Information;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class EarthMethods {

	static ProjectKorra plugin;
	private static FileConfiguration config = ProjectKorra.plugin.getConfig();

	public static ConcurrentHashMap<Block, Information> movedearth = new ConcurrentHashMap<Block, Information>();
	public static ConcurrentHashMap<Integer, Information> tempair = new ConcurrentHashMap<Integer, Information>();
	public static HashSet<Block> tempNoEarthbending = new HashSet<Block>();
	public static Integer[] transparentToEarthbending = { 0, 6, 8, 9, 10, 11, 30, 31, 32, 37, 38, 39, 40, 50, 51, 59, 78, 83, 106, 175 };
	private static final ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
	public static ArrayList<Block> tempnophysics = new ArrayList<Block>();

	public EarthMethods(ProjectKorra plugin) {
		EarthMethods.plugin = plugin;
	}

	/**
	 * Creates a temporary air block.
	 * 
	 * @param block The block to use as a base
	 */
	public static void addTempAirBlock(Block block) {
		if (movedearth.containsKey(block)) {
			Information info = movedearth.get(block);
			block.setType(Material.AIR);
			info.setTime(System.currentTimeMillis());
			movedearth.remove(block);
			tempair.put(info.getID(), info);
		} else {
			Information info = new Information();
			info.setBlock(block);
			info.setState(block.getState());
			info.setTime(System.currentTimeMillis());
			block.setType(Material.AIR);
			tempair.put(info.getID(), info);
		}

	}

	/**
	 * Checks to see if a player can SandBend.
	 * 
	 * @param player The player to check
	 * @return true If player has permission node "bending.earth.sandbending"
	 */
	public static boolean canSandbend(Player player) {
		if (player.hasPermission("bending.earth.sandbending"))
			return true;
		return false;
	}

	/**
	 * Checks to see if a player can MetalBend.
	 * 
	 * @param player The player to check
	 * @return true If player has permission node "bending.earth.metalbending"
	 */
	public static boolean canMetalbend(Player player) {
		if (player.hasPermission("bending.earth.metalbending"))
			return true;
		return false;
	}

	/**
	 * Checks to see if a player can LavaBend.
	 * 
	 * @param player The player to check
	 * @return true If player has permission node "bending.earth.lavabending"
	 */
	public static boolean canLavabend(Player player) {
		return player.hasPermission("bending.earth.lavabending");
	}

	public static void displaySandParticle(Location loc, float xOffset, float yOffset, float zOffset, float amount, float speed, boolean red) {
		if (amount <= 0)
			return;

		for (int x = 0; x < amount; x++) {
			if (!red) {
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.SAND, (byte) 0), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.SANDSTONE, (byte) 0), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
			} else if (red) {
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.SAND, (byte) 1), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.RED_SANDSTONE, (byte) 0), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
			}

		}
	}

	/**
	 * Gets the EarthColor from the config.
	 * 
	 * @return Config specified ChatColor
	 */
	public static ChatColor getEarthColor() {
		return ChatColor.valueOf(config.getString("Properties.Chat.Colors.Earth"));
	}
	
	/**
	 * Gets the EarthSubColor from the config.
	 * 
	 * @return Config specified ChatColor
	 */
	public static ChatColor getEarthSubColor() {
		return ChatColor.valueOf(config.getString("Properties.Chat.Colors.EarthSub"));
	}

	public static int getEarthbendableBlocksLength(Player player, Block block, Vector direction, int maxlength) {
		Location location = block.getLocation();
		direction = direction.normalize();
		double j;
		for (int i = 0; i <= maxlength; i++) {
			j = (double) i;
			if (!isEarthbendable(player, location.clone().add(direction.clone().multiply(j)).getBlock())) {
				return i;
			}
		}
		return maxlength;
	}

	/**
	 * Finds a valid Earth source for a Player. To use dynamic source selection,
	 * use BlockSource.getEarthSourceBlock() instead of this method. Dynamic
	 * source selection saves the user's previous source for future use.
	 * {@link BlockSource#getEarthSourceBlock(Player, double, com.projectkorra.projectkorra.util.ClickType)}
	 * 
	 * @param player the player that is attempting to Earthbend.
	 * @param range the maximum block selection range.
	 * @return a valid Earth source block, or null if one could not be found.
	 */
	@SuppressWarnings("deprecation")
	public static Block getEarthSourceBlock(Player player, double range) {
		Block testblock = player.getTargetBlock(getTransparentEarthbending(), (int) range);
		if (isEarthbendable(player, testblock))
			return testblock;
		Location location = player.getEyeLocation();
		Vector vector = location.getDirection().clone().normalize();
		for (double i = 0; i <= range; i++) {
			Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
			if (GeneralMethods.isRegionProtectedFromBuild(player, "RaiseEarth", location))
				continue;
			if (isEarthbendable(player, block)) {
				return block;
			}
		}
		return null;
	}

	/**
	 * Attempts to find the closest earth block near a given location.
	 * 
	 * @param loc the initial location to search from.
	 * @param radius the maximum radius to search for the earth block.
	 * @param maxVertical the maximum block height difference between the
	 *            starting location and the earth bendable block.
	 * @return an earth bendable block, or null.
	 */
	public static Block getNearbyEarthBlock(Location loc, double radius, int maxVertical) {
		if (loc == null) {
			return null;
		}
		int rotation = 30;
		for (int i = 0; i < radius; i++) {
			Vector tracer = new Vector(i, 0, 0);
			for (int deg = 0; deg < 360; deg += rotation) {
				Location searchLoc = loc.clone().add(tracer);
				Block block = GeneralMethods.getTopBlock(searchLoc, maxVertical);

				if (block != null && EarthMethods.isEarthbendable(block.getType())) {
					return block;
				}
				tracer = GeneralMethods.rotateXZ(tracer, rotation);
			}
		}
		return null;
	}

	/**
	 * Gets the MetalBendingColor from the config.
	 * 
	 * @return Config specified ChatColor
	 */
	@Deprecated
	public static ChatColor getMetalbendingColor() {
		return ChatColor.valueOf(config.getString("Properties.Chat.Colors.Metalbending"));
	}

	public static HashSet<Byte> getTransparentEarthbending() {
		HashSet<Byte> set = new HashSet<Byte>();
		for (int i : transparentToEarthbending) {
			set.add((byte) i);
		}
		return set;
	}

	/**
	 * Finds a valid Lava source for a Player. To use dynamic source selection,
	 * use BlockSource.getLavaSourceBlock() instead of this method. Dynamic
	 * source selection saves the user's previous source for future use.
	 * {@link BlockSource#getLavaSourceBlock(Player, double, com.projectkorra.projectkorra.util.ClickType)}
	 * 
	 * @param player the player that is attempting to Earthbend.
	 * @param range the maximum block selection range.
	 * @return a valid Lava source block, or null if one could not be found.
	 */
	@SuppressWarnings("deprecation")
	public static Block getLavaSourceBlock(Player player, double range) {
		Location location = player.getEyeLocation();
		Vector vector = location.getDirection().clone().normalize();
		for (double i = 0; i <= range; i++) {
			Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
			if (GeneralMethods.isRegionProtectedFromBuild(player, "LavaSurge", location))
				continue;
			if (isLavabendable(block, player)) {
				if (TempBlock.isTempBlock(block)) {
					TempBlock tb = TempBlock.get(block);
					byte full = 0x0;
					if (tb.getState().getRawData() != full && (tb.getState().getType() != Material.LAVA || tb.getState().getType() != Material.STATIONARY_LAVA)) {
						continue;
					}
				}
				return block;
			}
		}
		return null;
	}

	public static boolean isLavabendingAbility(String ability) {
		return AbilityModuleManager.lavaabilities.contains(ability);
	}

	public static boolean isMetalbendingAbility(String ability) {
		return AbilityModuleManager.metalabilities.contains(ability);
	}

	public static boolean isSandbendingAbility(String ability) {
		return AbilityModuleManager.sandabilities.contains(ability);
	}

	public static boolean isEarthAbility(String ability) {
		return AbilityModuleManager.earthbendingabilities.contains(ability);
	}

	public static boolean isEarthbendable(Player player, Block block) {
		return isEarthbendable(player, "RaiseEarth", block);
	}

	public static boolean isMetal(Block block) {
		Material material = block.getType();
		return config.getStringList("Properties.Earth.MetalBlocks").contains(material.toString());
	}

	public static double getMetalAugment(double value) {
		return value * config.getDouble("Properties.Earth.MetalPowerFactor");
	}

	public static boolean isEarthbendable(Material mat) {
		for (String s : config.getStringList("Properties.Earth.EarthbendableBlocks")) {
			if (mat == Material.getMaterial(s)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEarthbendable(Player player, String ability, Block block) {
		Material material = block.getType();
		boolean valid = false;
		for (String s : config.getStringList("Properties.Earth.EarthbendableBlocks"))
			if (material == Material.getMaterial(s)) {
				valid = true;
				break;
			}
		if (isMetal(block) && canMetalbend(player)) {
			valid = true;
		}

		if (!valid)
			return false;

		if (tempNoEarthbending.contains(block))
			return false;

		if (!GeneralMethods.isRegionProtectedFromBuild(player, ability, block.getLocation()))
			return true;
		return false;
	}

	public static boolean isMetalBlock(Block block) {
		if (block.getType() == Material.GOLD_BLOCK || block.getType() == Material.IRON_BLOCK || block.getType() == Material.IRON_ORE || block.getType() == Material.GOLD_ORE || block.getType() == Material.QUARTZ_BLOCK || block.getType() == Material.QUARTZ_ORE)
			return true;
		return false;
	}

	public static boolean isTransparentToEarthbending(Player player, Block block) {
		return isTransparentToEarthbending(player, "RaiseEarth", block);
	}

	@SuppressWarnings("deprecation")
	public static boolean isTransparentToEarthbending(Player player, String ability, Block block) {
		if (!Arrays.asList(transparentToEarthbending).contains(block.getTypeId()))
			return false;
		if (!GeneralMethods.isRegionProtectedFromBuild(player, ability, block.getLocation()))
			return true;
		return false;
	}

	public static boolean isLava(Block block) {
		if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean isLavabendable(Block block, Player player) {
		byte full = 0x0;
		if (TempBlock.isTempBlock(block)) {
			TempBlock tblock = TempBlock.instances.get(block);
			if (tblock == null || !LavaFlow.TEMP_LAVA_BLOCKS.contains(tblock))
				return false;
		}
		if ((block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) && block.getData() == full)
			return true;
		return false;
	}

	public static void moveEarth(Player player, Block block, Vector direction, int chainlength) {
		moveEarth(player, block, direction, chainlength, true);
	}

	public static boolean moveEarth(Player player, Block block, Vector direction, int chainlength, boolean throwplayer) {
		if (isEarthbendable(player, block) && !GeneralMethods.isRegionProtectedFromBuild(player, "RaiseEarth", block.getLocation())) {

			boolean up = false;
			boolean down = false;
			Vector norm = direction.clone().normalize();
			if (norm.dot(new Vector(0, 1, 0)) == 1) {
				up = true;
			} else if (norm.dot(new Vector(0, -1, 0)) == 1) {
				down = true;
			}
			Vector negnorm = norm.clone().multiply(-1);

			Location location = block.getLocation();

			ArrayList<Block> blocks = new ArrayList<Block>();
			for (double j = -2; j <= chainlength; j++) {
				Block checkblock = location.clone().add(negnorm.clone().multiply(j)).getBlock();
				if (!tempnophysics.contains(checkblock)) {
					blocks.add(checkblock);
					tempnophysics.add(checkblock);
				}
			}

			Block affectedblock = location.clone().add(norm).getBlock();
			if (EarthPassive.isPassiveSand(block)) {
				EarthPassive.revertSand(block);
			}

			if (affectedblock == null)
				return false;
			if (isTransparentToEarthbending(player, affectedblock)) {
				if (throwplayer) {
					for (Entity entity : GeneralMethods.getEntitiesAroundPoint(affectedblock.getLocation(), 1.75)) {
						if (entity instanceof LivingEntity) {
							LivingEntity lentity = (LivingEntity) entity;
							if (lentity.getEyeLocation().getBlockX() == affectedblock.getX() && lentity.getEyeLocation().getBlockZ() == affectedblock.getZ())
								if (!(entity instanceof FallingBlock))
									entity.setVelocity(norm.clone().multiply(.75));
						} else {
							if (entity.getLocation().getBlockX() == affectedblock.getX() && entity.getLocation().getBlockZ() == affectedblock.getZ())
								if (!(entity instanceof FallingBlock))
									entity.setVelocity(norm.clone().multiply(.75));
						}
					}

				}

				if (up) {
					Block topblock = affectedblock.getRelative(BlockFace.UP);
					if (topblock.getType() != Material.AIR) {
						GeneralMethods.breakBlock(affectedblock);
					} else if (!affectedblock.isLiquid() && affectedblock.getType() != Material.AIR) {
						moveEarthBlock(affectedblock, topblock);
					}
				} else {
					GeneralMethods.breakBlock(affectedblock);
				}

				moveEarthBlock(block, affectedblock);
				playEarthbendingSound(block.getLocation());

				for (double i = 1; i < chainlength; i++) {
					affectedblock = location.clone().add(negnorm.getX() * i, negnorm.getY() * i, negnorm.getZ() * i).getBlock();
					if (!isEarthbendable(player, affectedblock)) {
						if (down) {
							if (isTransparentToEarthbending(player, affectedblock) && !affectedblock.isLiquid() && affectedblock.getType() != Material.AIR) {
								moveEarthBlock(affectedblock, block);
							}
						}
						break;
					}
					if (EarthPassive.isPassiveSand(affectedblock)) {
						EarthPassive.revertSand(affectedblock);
					}
					if (block == null) {
						for (Block checkblock : blocks) {
							tempnophysics.remove(checkblock);
						}
						return false;
					}
					moveEarthBlock(affectedblock, block);
					block = affectedblock;
				}

				int i = chainlength;
				affectedblock = location.clone().add(negnorm.getX() * i, negnorm.getY() * i, negnorm.getZ() * i).getBlock();
				if (!isEarthbendable(player, affectedblock)) {
					if (down) {
						if (isTransparentToEarthbending(player, affectedblock) && !affectedblock.isLiquid()) {
							moveEarthBlock(affectedblock, block);
						}
					}
				}

			} else {
				for (Block checkblock : blocks) {
					tempnophysics.remove(checkblock);
				}
				return false;
			}
			for (Block checkblock : blocks) {
				tempnophysics.remove(checkblock);
			}
			return true;
		}
		return false;
	}

	public static void moveEarth(Player player, Location location, Vector direction, int chainlength) {
		moveEarth(player, location, direction, chainlength, true);
	}

	public static void moveEarth(Player player, Location location, Vector direction, int chainlength, boolean throwplayer) {
		Block block = location.getBlock();
		moveEarth(player, block, direction, chainlength, throwplayer);
	}

	@SuppressWarnings("deprecation")
	public static void moveEarthBlock(Block source, Block target) {
		byte full = 0x0;
		Information info;
		if (movedearth.containsKey(source)) {
			info = movedearth.get(source);
			info.setTime(System.currentTimeMillis());
			movedearth.remove(source);
			movedearth.put(target, info);
		} else {
			info = new Information();
			info.setBlock(source);
			info.setTime(System.currentTimeMillis());
			info.setState(source.getState());
			movedearth.put(target, info);
		}

		if (GeneralMethods.isAdjacentToThreeOrMoreSources(source)) {
			source.setType(Material.WATER);
			source.setData(full);
		} else {
			source.setType(Material.AIR);
		}
		if (info.getState().getType() == Material.SAND) {
			if (info.getState().getRawData() == (byte) 0x1) {
				target.setType(Material.RED_SANDSTONE);
			} else {
				target.setType(Material.SANDSTONE);
			}
		} else {
			target.setType(info.getState().getType());
			target.setData(info.getState().getRawData());
		}
	}

	public static void playSandBendingSound(Location loc) {
		if (plugin.getConfig().getBoolean("Properties.Earth.PlaySound")) {
			loc.getWorld().playSound(loc, Sound.BLOCK_SAND_HIT, 1.5f, 5);
		}
	}

	public static void removeAllEarthbendedBlocks() {
		for (Block block : movedearth.keySet()) {
			revertBlock(block);
		}

		for (Integer i : tempair.keySet()) {
			revertAirBlock(i, true);
		}
	}

	public static void removeRevertIndex(Block block) {
		if (movedearth.containsKey(block)) {
			Information info = movedearth.get(block);
			if (block.getType() == Material.SANDSTONE && info.getType() == Material.SAND)
				block.setType(Material.SAND);
			if (EarthColumn.blockInAllAffectedBlocks(block))
				EarthColumn.revertBlock(block);

			movedearth.remove(block);
		}
	}

	public static void revertAirBlock(int i) {
		revertAirBlock(i, false);
	}

	@SuppressWarnings("deprecation")
	public static void revertAirBlock(int i, boolean force) {
		if (!tempair.containsKey(i))
			return;
		Information info = tempair.get(i);
		Block block = info.getState().getBlock();
		if (block.getType() != Material.AIR && !block.isLiquid()) {
			if (force || !movedearth.containsKey(block)) {
				GeneralMethods.dropItems(block, GeneralMethods.getDrops(block, info.getState().getType(), info.getState().getRawData(), pickaxe));
				tempair.remove(i);
			} else {
				info.setTime(info.getTime() + 10000);
			}
			return;
		} else {
			info.getState().update(true);
			tempair.remove(i);
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean revertBlock(Block block) {
		byte full = 0x0;
		if (!ProjectKorra.plugin.getConfig().getBoolean("Properties.Earth.RevertEarthbending")) {
			movedearth.remove(block);
			return false;
		}
		if (movedearth.containsKey(block)) {
			Information info = movedearth.get(block);
			Block sourceblock = info.getState().getBlock();

			if (info.getState().getType() == Material.AIR) {
				movedearth.remove(block);
				return true;
			}

			if (block.equals(sourceblock)) {
				info.getState().update(true);
				if (EarthColumn.blockInAllAffectedBlocks(sourceblock))
					EarthColumn.revertBlock(sourceblock);
				if (EarthColumn.blockInAllAffectedBlocks(block))
					EarthColumn.revertBlock(block);
				movedearth.remove(block);
				return true;
			}

			if (movedearth.containsKey(sourceblock)) {
				addTempAirBlock(block);
				movedearth.remove(block);
				return true;
			}

			if (sourceblock.getType() == Material.AIR || sourceblock.isLiquid()) {
				info.getState().update(true);
			} else {
				GeneralMethods.dropItems(block, GeneralMethods.getDrops(block, info.getState().getType(), info.getState().getRawData(), pickaxe));
			}

			if (GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
				block.setType(Material.WATER);
				block.setData(full);
			} else {
				block.setType(Material.AIR);
			}

			if (EarthColumn.blockInAllAffectedBlocks(sourceblock))
				EarthColumn.revertBlock(sourceblock);
			if (EarthColumn.blockInAllAffectedBlocks(block))
				EarthColumn.revertBlock(block);
			movedearth.remove(block);
		}
		return true;
	}

	public static void playEarthbendingSound(Location loc) {
		if (plugin.getConfig().getBoolean("Properties.Earth.PlaySound")) {
			loc.getWorld().playEffect(loc, Effect.GHAST_SHOOT, 0, 10);
		}
	}

	public static void playMetalbendingSound(Location loc) {
		if (plugin.getConfig().getBoolean("Properties.Earth.PlaySound")) {
			loc.getWorld().playSound(loc, Sound.ENTITY_IRONGOLEM_HURT, 1, 10);
		}
	}

	public static void stopBending() {
		Catapult.removeAll();
		CompactColumn.removeAll();
		EarthBlast.removeAll();
		EarthColumn.removeAll();
		EarthPassive.removeAll();
		EarthArmor.removeAll();
		EarthTunnel.instances.clear();
		Shockwave.removeAll();
		Tremorsense.removeAll();
		LavaFlow.removeAll();
		EarthSmash.removeAll();

		if (ProjectKorra.plugin.getConfig().getBoolean("Properties.Earth.RevertEarthbending")) {
			EarthMethods.removeAllEarthbendedBlocks();
		}

		EarthPassive.removeAll();
	}

}
