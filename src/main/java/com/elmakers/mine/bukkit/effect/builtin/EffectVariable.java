package com.elmakers.mine.bukkit.effect.builtin;

import com.elmakers.mine.bukkit.effect.EffectPlayer;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class EffectVariable extends EffectPlayer {

    private Map<Double, Collection<EffectPlayer>> brightnessMap = new TreeMap<Double, Collection<EffectPlayer>>();
    private Collection<EffectPlayer> playing = new ArrayList<EffectPlayer>();

    public EffectVariable() {

    }

    @Override
    public void cancel() {
        super.cancel();
        for (EffectPlayer player : playing) {
            player.cancel();
        }
    }

    @Override
    public void load(Plugin plugin, ConfigurationSection configuration) {
        super.load(plugin, configuration);

        // ... This seems kinda broken. I get a Map here if it's embedded in another section?
        Object testObject = configuration.get("brightness");
        if (testObject != null) {
            if (testObject instanceof Map) {
                Map<String, List<ConfigurationSection>> dataMap = (Map<String, List<ConfigurationSection>>)testObject;
                for (Map.Entry<String, List<ConfigurationSection>> entry : dataMap.entrySet()) {
                    try {
                        String key = entry.getKey();
                        double level = Double.parseDouble(key);
                        MemoryConfiguration brightness = new MemoryConfiguration();
                        brightness.set(key, entry.getValue());
                        brightnessMap.put(level, EffectPlayer.loadEffects(plugin, brightness, key));
                    } catch (Exception ex) {
                    }
                }
            } else {
                ConfigurationSection brightness = configuration.getConfigurationSection("brightness");
                Collection<String> keys = brightness.getKeys(false);
                for (String key : keys) {
                    try {
                        double level = Double.parseDouble(key);
                        brightnessMap.put(level, EffectPlayer.loadEffects(plugin, brightness, key));
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

    public void play() {
        playing.clear();
        if (brightnessMap.size() > 0) {
            double brightness = 0;
            Color color = getColor1();
            if (color != null) {
                brightness = (double) color.getRed() / 255.0 + (double) color.getBlue() / 255.0 + (double) color.getGreen() / 255.0;
            }
            for (Map.Entry<Double, Collection<EffectPlayer>> entry : brightnessMap.entrySet()) {
                if (brightness < entry.getKey()) {
                    for (EffectPlayer player : entry.getValue()) {
                        // Set scale
                        player.setScale(scale);

                        // Set material and color
                        player.setMaterial(getWorkingMaterial());
                        player.setColor(getColor1());
                        player.setParticleOverride(particleOverride == null ? null : particleOverride.name());
                        player.start(origin, getOriginEntity(), target, getTargetEntity());

                        playing.add(player);
                    }
                    break;
                }
            }
        }
    }
}
