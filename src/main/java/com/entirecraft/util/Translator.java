package com.entirecraft.util;

import net.minecraft.util.StatCollector;

public class Translator
{
	private Translator() {

	}

	public static String translateToLocal(String key) {
		if (StatCollector.canTranslate(key)) {
			return StatCollector.translateToLocal(key);
		} else {
			return StatCollector.translateToFallback(key);
		}
	}
}
