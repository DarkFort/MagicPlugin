package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.api.action.GeneralAction;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.spell.BaseSpellAction;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class WeatherAction extends BaseSpellAction implements GeneralAction
{
	private boolean makeStorm = true;

	@Override
	public SpellResult perform(ConfigurationSection parameters) {
		World world = getWorld();
		if (world == null) {
			return SpellResult.WORLD_REQUIRED;
		}
		String weatherString = parameters.getString("weather", "");
		boolean hasStorm = world.hasStorm();
		makeStorm = weatherString.equals("storm");
		if (weatherString.equals("cycle")) {
			makeStorm = !hasStorm;
		}

		if (makeStorm) {
			world.setStorm(true);
			world.setThundering(true);
		} else {
			world.setStorm(false);
			world.setThundering(false);
		}
		return makeStorm ? SpellResult.CAST : SpellResult.ALTERNATE;
	}
}
