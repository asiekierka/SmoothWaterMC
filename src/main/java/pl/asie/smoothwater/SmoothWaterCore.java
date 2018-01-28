/*
 * Copyright (C) 2018 Adrian Siekierka
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package pl.asie.smoothwater;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"pl.asie.smoothwater"})
public class SmoothWaterCore implements IFMLLoadingPlugin {
	public static Configuration config;
	public static boolean patchModdedFluids;

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"pl.asie.smoothwater.SmoothWaterTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		config = new Configuration(new File(new File("config"), "smoothwater.cfg"));

		patchModdedFluids = config.getBoolean("patchModdedFluidAO", "general", true, "Patches default ambient occlusion handling in modded fluids.");

		if (config.hasChanged()) {
			config.save();
		}

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
