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

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;

public class ProxyClient extends ProxyCommon {
	private Fluid getFluid(Block b) {
		Block lookupBlock = b;
		if (lookupBlock instanceof BlockDynamicLiquid) {
			lookupBlock = BlockDynamicLiquid.getStaticBlock(lookupBlock.getDefaultState().getMaterial());
		}
		return FluidRegistry.lookupFluidForBlock(lookupBlock);
	}

	@Override
	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		try {
			Field f1 = ReflectionHelper.findField(Minecraft.class, "modelManager", "field_175617_aL");
			Object one = f1.get(Minecraft.getMinecraft());

			Field f = ReflectionHelper.findField(BlockStateMapper.class, "setBuiltInBlocks", "field_178449_b");
			Collection c = (Collection) f.get(((ModelManager) one).getBlockModelShapes().getBlockStateMapper());
			c.removeIf(o -> o instanceof BlockLiquidForged && getFluid((Block) o) != null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		for (Block b : ForgeRegistries.BLOCKS) {
			if (b instanceof BlockLiquid) {
				Fluid f = getFluid(b);
				if (f != null) {
					ModelFluid fluid = new ModelFluid(f);
					IBakedModel baked = fluid.bake(
							TRSRTransformation.identity(),
							DefaultVertexFormats.ITEM,
							ModelLoader.defaultTextureGetter()
					);
					for (int i = 0; i < 16; i++) {
						event.getModelRegistry().putObject(new ModelResourceLocation(b.getRegistryName(), "level=" + i), baked);
					}
				}
			}
		}
	}
}
