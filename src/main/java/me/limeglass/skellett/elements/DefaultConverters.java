package me.limeglass.skellett.elements;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import ch.njol.skript.Skript;
import me.limeglass.skellett.objects.BlockSave;
import org.skriptlang.skript.lang.converter.Converters;

public class DefaultConverters {

	static {
		if (Skript.classExists("org.bukkit.block.data.BlockData")) {
			Converters.registerConverter(BlockSave.class, BlockData.class, BlockSave::getBlockData);
		}
		Converters.registerConverter(BlockSave.class, Location.class, BlockSave::getLocation);
	}

}
