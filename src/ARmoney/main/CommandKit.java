package ARmoney.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public abstract class CommandKit implements CommandExecutor, TabCompleter {
	public CommandKit (String command) {
		PluginCommand pluginCommand = ARmain.getInstance().getCommand(command);
		if (pluginCommand != null) {
			pluginCommand.setExecutor(this);
		}

	}
	public abstract void execute (CommandSender sender, String label, String[] args);
	public List<String> complete(CommandSender sender, String[] args) {
		return null;
	}
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	execute(sender, label, args);
        return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    	return filter(complete(sender, args), args);
    }
    private List<String> filter(List<String> list, String[] args) {
    	if (list == null) return null;
    	String last = args[args.length - 1];
    	List<String> result = new ArrayList<>();
    	for (String arg : list) {
    		if (arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
    	}
    	return result;
    }
}
