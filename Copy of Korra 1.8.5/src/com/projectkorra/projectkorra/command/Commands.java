package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.ProjectKorra;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Commands {

	private ProjectKorra plugin;

	public static Set<String> invincible = new HashSet<String>();
	public static boolean debugEnabled = false;
	public static boolean isToggledForAll = false;

	public Commands(ProjectKorra plugin) {
		this.plugin = plugin;
		debugEnabled = ProjectKorra.plugin.getConfig().getBoolean("debug");
		init();
	}

	/*
	 * Element Aliases
	 */
	public static String[] airaliases = { "air", "a", "airbending", "airbender" };
	public static String[] chialiases = { "chi", "c", "chiblocking", "chiblocker" };
	public static String[] earthaliases = { "earth", "e", "earthbending", "earthbender" };
	public static String[] firealiases = { "fire", "f", "firebending", "firebender" };
	public static String[] wateraliases = { "water", "w", "waterbending", "waterbender" };
	public static String[] snowmanaliases = { "snowman", "sn", "snowbending", "snowbender" };
	public static String[] scarecrowaliases = { "scarecrow", "sc", "scarebending", "scarebender" };
	public static String[] elementaliases = { "air", "a", "airbending", "airbender", "chi", "c", "chiblocking", "chiblocker", "earth", "e", "earthbending", "earthbender", "fire", "f", "firebending", "firebender", "water", "w", "waterbending", "waterbender", "scarecrow", "sc", "scarebending", "scarebender", "snowman", "sn", "snowbending", "snowbender" };
	public static String[] avataraliases = { "avatar", "av", "avy", "aang", "korra" };
	
	/*
	 * Combo Aliases
	 */
	public static String[] aircomboaliases = { "aircombo", "ac", "aircombos", "airbendingcombos" };
	public static String[] chicomboaliases = { "chicombo", "cc", "chicombos", "chiblockingcombos", "chiblockercombos" };
	public static String[] earthcomboaliases = { "earthcombo", "ec", "earthcombos", "earthbendingcombos" };
	public static String[] firecomboaliases = { "firecombo", "fc", "firecombos", "firebendingcombos" };
	public static String[] watercomboaliases = { "watercombo", "wc", "watercombos", "waterbendingcombos" };

	public static String[] comboaliases = { "aircombo", "ac", "aircombos", "airbendingcombos", "chicombo", "cc", "chicombos", "chiblockingcombos", "chiblockercombos", "earthcombo", "ec", "earthcombos", "earthbendingcombos", "firecombo", "fc", "firecombos", "firebendingcombos", "watercombo", "wc", "watercombos", "waterbendingcombos" };

	/*
	 * Subelement Aliases
	 */

	public static String[] subelementaliases = { "flight", "fl", "spiritualprojection", "sp", "spiritual", "bloodbending", "bb", "healing", "heal", "icebending", "ice", "ib", "plantbending", "plant", "metalbending", "mb", "metal", "lavabending", "lb", "lava", "sandbending", "sb", "sand", "combustionbending", "combustion", "cb", "lightningbending", "lightning" };
	//Air
	public static String[] flightaliases = { "flight", "fl" };
	public static String[] spiritualprojectionaliases = { "spiritualprojection", "sp", "spiritual" };

	//Water
	public static String[] bloodaliases = { "bloodbending", "bb" };
	public static String[] healingaliases = { "healing", "heal" };
	public static String[] icealiases = { "icebending", "ice", "ib" };
	public static String[] plantaliases = { "plantbending", "plant" };

	//Earth
	public static String[] metalbendingaliases = { "metalbending", "mb", "metal" };
	public static String[] lavabendingaliases = { "lavabending", "lb", "lava" };
	public static String[] sandbendingaliases = { "sandbending", "sb", "sand" };

	//Firebending
	public static String[] combustionaliases = { "combustionbending", "combustion", "cb" };
	public static String[] lightningaliases = { "lightningbending", "lightning" };
	
	//Miscellaneous
	public static String[] commandaliases = { "b", "pk", "bending", "mtla", "tla", "korra", "bend" };

	private void init() {
		PluginCommand projectkorra = plugin.getCommand("projectkorra");
		new AddCommand();
		new BindCommand();
		new CheckCommand();
		new ChooseCommand();
		new ClearCommand();
		new DebugCommand();
		new DisplayCommand();
		new HelpCommand();
		new ImportCommand();
		new InvincibleCommand();
		new PermaremoveCommand();
		new PresetCommand();
		new ReloadCommand();
		new RemoveCommand();
		new ToggleCommand();
		new VersionCommand();
		new WhoCommand();

		/**
		 * Set of all of the Classes which extend Command
		 */

		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				for (int i = 0; i < args.length; i++) {
					args[i] = args[i];
				}

				if (args.length == 0 && Arrays.asList(commandaliases).contains(label.toLowerCase())) {
					s.sendMessage(ChatColor.RED + "/bending help [Ability/Command] " + ChatColor.YELLOW + "Display help.");
					s.sendMessage(ChatColor.RED + "/bending choose [Element] " + ChatColor.YELLOW + "Choose an element.");
					s.sendMessage(ChatColor.RED + "/bending bind [Ability] # " + ChatColor.YELLOW + "Bind an ability.");
					return true;
				}

				List<String> sendingArgs = Arrays.asList(args).subList(1, args.length);
				for (PKCommand command : PKCommand.instances.values()) {
					if (Arrays.asList(command.getAliases()).contains(args[0].toLowerCase())) {
						command.execute(s, sendingArgs);
						return true;
					}
				}
				return true;
			}
		};
		projectkorra.setExecutor(exe);
		projectkorra.setTabCompleter(new BendingTabComplete());
	}
}
