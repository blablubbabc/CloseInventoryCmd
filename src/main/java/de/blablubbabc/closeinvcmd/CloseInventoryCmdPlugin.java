package de.blablubbabc.closeinvcmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class CloseInventoryCmdPlugin extends JavaPlugin {

	public static final String PERMISSION_CLOSEINVENTORY_OWN = "closeinventory.own";
	public static final String PERMISSION_CLOSEINVENTORY_OTHERS = "closeinventory.others";

	// Settings:
	private boolean onlyCloseIfOpen;
	private boolean informSender;
	private boolean informTarget;

	// Messages:
	private String msgTooManyArguments;
	private String msgUnknownPlayer;
	private String msgNoPlayerSpecified;
	private String msgNoPermission;
	private String msgInventoryClosedSender;
	private String msgInventoryClosedTarget;

	@Override
	public void onEnable() {
		// Generate default config if it is missing:
		this.saveDefaultConfig();

		// Load settings:
		Configuration config = this.getConfig();
		onlyCloseIfOpen = config.getBoolean("only-close-if-open", false);
		informSender = config.getBoolean("inform-sender", true);
		informTarget = config.getBoolean("inform-target", false);

		// Load messages:
		msgTooManyArguments = this.loadMessage("msg-too-many-arguments", "&cToo many arguments! Usage: /{label} [player]");
		msgUnknownPlayer = this.loadMessage("msg-unknown-player", "&cUnknown player: &e{player}");
		msgNoPlayerSpecified = this.loadMessage("msg-no-player-specified", "&cNo player specified!");
		msgNoPermission = this.loadMessage("msg-no-permission", "&cYou do not have the permission to do that!");
		msgInventoryClosedSender = this.loadMessage("msg-inventory-closed-sender", "&aClosed inventory of player &e{player}&a!");
		msgInventoryClosedTarget = this.loadMessage("msg-inventory-closed-target", "&7Inventory closed.");
	}

	private String loadMessage(String key, String defaultMessage) {
		return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(key, defaultMessage));
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Check for unexpected arguments:
		if (args.length > 1) {
			sender.sendMessage(msgTooManyArguments.replace("{label}", label));
			return true;
		}

		// Determine target player:
		Player targetPlayer = null;
		if (sender instanceof Player) {
			targetPlayer = (Player) sender;
		}

		if (args.length == 1) {
			String targetPlayerName = args[0];
			targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
			if (targetPlayer == null) {
				sender.sendMessage(msgUnknownPlayer.replace("{player}", targetPlayerName));
				return true;
			}
		}

		if (targetPlayer == null) {
			sender.sendMessage(msgNoPlayerSpecified);
			return true;
		}

		// Check permission:
		if (targetPlayer != sender) {
			if (!sender.hasPermission(PERMISSION_CLOSEINVENTORY_OTHERS)) {
				sender.sendMessage(msgNoPermission);
				return true;
			}
		} else if (!sender.hasPermission(PERMISSION_CLOSEINVENTORY_OWN)) {
			sender.sendMessage(msgNoPermission);
			return true;
		}

		// Close inventory of target player:
		// If onlyCloseIfOpen, we check if the player has a non-default inventory open (not CRAFTING).
		// Note: We cannot check if the player has his own (default) inventory open since that is client side.
		if (!onlyCloseIfOpen || targetPlayer.getOpenInventory().getType() != InventoryType.CRAFTING) {
			targetPlayer.closeInventory();
		}

		// Inform sender:
		if (informSender) {
			sender.sendMessage(msgInventoryClosedSender.replace("{player}", targetPlayer.getName()));
		}
		// Inform target:
		if (informTarget && targetPlayer != sender) {
			targetPlayer.sendMessage(msgInventoryClosedTarget);
		}
		return true;
	}
}
