package com.github.highright1234.hrspectator;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (HRSpectator.getInstance().getSpectators().contains(sender)) {
                HRSpectator.getInstance().removeSpectator((Player) sender);
            } else {
                HRSpectator.getInstance().addSpectator((Player) sender);
            }
        }
        return true;
    }
}
