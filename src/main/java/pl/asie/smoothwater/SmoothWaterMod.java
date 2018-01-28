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

import akka.util.Reflect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

@Mod(
		modid = "smoothwater",
		name = "SmoothWater",
		clientSideOnly = true,
		acceptableRemoteVersions = "*",
		canBeDeactivated = true,
		version = "@VERSION@"
)
public class SmoothWaterMod {
	public static boolean isActive = true;
	@SidedProxy(clientSide = "pl.asie.smoothwater.ProxyClient", serverSide = "pl.asie.smoothwater.ProxyCommon")
	public static ProxyCommon proxy;

	@Mod.EventHandler
	public void onDisabled(FMLModDisabledEvent event) {
		isActive = false;
	}

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		proxy.preInit();
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		Field f = ReflectionHelper.findField(Block.class, "blockState", "field_176227_L");
		Field f2 = ReflectionHelper.findField(Block.class, "defaultBlockState", "field_176228_M");

		for (Block b : Block.REGISTRY) {
			if (b instanceof BlockLiquidForged) {
				try {
					f.set(b, new ExtendedBlockState(b, b.getBlockState().getProperties().toArray(new IProperty[0]), BlockFluidBase.FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0])));
					f2.set(b, b.getBlockState().getBaseState().withProperty(BlockLiquid.LEVEL, b.getDefaultState().getValue(BlockLiquid.LEVEL)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
