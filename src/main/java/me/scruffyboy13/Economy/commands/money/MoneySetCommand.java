package me.scruffyboy13.Economy.commands.money;

import java.util.Arrays;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import me.scruffyboy13.Economy.EconomyMain;
import me.scruffyboy13.Economy.commands.CommandExecutor;
import me.scruffyboy13.Economy.data.ConfigHandler;
import me.scruffyboy13.Economy.utils.StringUtils;

public class MoneySetCommand extends CommandExecutor {

	public MoneySetCommand() {
		this.setName("set");
		this.setPermission("economy.command.set");
		this.setUsage(ConfigHandler.getMessage("money.set.usage"));
		this.setBoth(true);
		this.setLengths(Arrays.asList(3));
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		List<OfflinePlayer> others = EconomyMain.getPlayersFromString(sender, args[1]);
		
		if (others.isEmpty() && !args[1].equals("@a")) {
			StringUtils.sendConfigMessage(sender, "messages.money.set.otherDoesntExist", ImmutableMap.of(
					"%player%", args[1]));
			return;
		}
		
		double amount = 0;
		try {
			amount = EconomyMain.getAmountFromString(args[2]);
		}
		catch (NumberFormatException e){
			StringUtils.sendConfigMessage(sender, "messages.money.set.invalidAmount", ImmutableMap.of(
					"%amount%", args[2]));
			return;
		}
		if (amount < 0) {
			StringUtils.sendConfigMessage(sender, "messages.money.set.invalidAmount", ImmutableMap.of(
					"%amount%", args[2]));
			return;
		}
		
		int total = 0;
		boolean failed = false;
		
		for (OfflinePlayer other : others) {
		
			if (!EconomyMain.getEco().hasAccount(other.getUniqueId())) {
				StringUtils.sendConfigMessage(sender, "messages.money.set.otherNoAccount", ImmutableMap.of(
						"%player%", other.getName()));
				failed = true;
				continue;
			}
			
			EconomyMain.getEco().set(other.getUniqueId(), amount);
			
			if (other instanceof Player) {
				if (!(sender instanceof Player && ((Player) sender).equals((Player) other))) {
					StringUtils.sendConfigMessage((Player) other, "messages.money.set.set", ImmutableMap.of(
							"%amount%", EconomyMain.format(amount)));
				}
			}
			
			total += 1;
		
		}
		
		if (others.size() == 1) {
			
			if (!failed) {
		
				OfflinePlayer other = others.get(0);
				StringUtils.sendConfigMessage(sender, "messages.money.set.setter", ImmutableMap.of(
						"%balance%", EconomyMain.format(amount),
						"%player%", other.getName()));
				
			}
		
		}
		
		else {
			
			StringUtils.sendConfigMessage(sender, "messages.money.set.setterMultiple", ImmutableMap.of(
					"%total%", total + "",
					"%balance%", EconomyMain.format(amount)
					));
			
		}
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
