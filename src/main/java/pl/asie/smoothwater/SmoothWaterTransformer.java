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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SmoothWaterTransformer implements IClassTransformer {
	public static class MV extends MethodVisitor {
		public MV(int api, MethodVisitor parent) {
			super(api, parent);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
		                            String desc, boolean itf) {
			if ("<init>".equals(name) && "net/minecraft/block/BlockLiquid".equals(owner)) {
				super.visitMethodInsn(opcode, "pl/asie/smoothwater/BlockLiquidForged", name, desc, itf);
			} else {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}

	public static class CV extends ClassVisitor {
		public CV(int api, ClassVisitor cv) {
			super(api, cv);
		}

		@Override
		public void visit(int version, int access, String name, String signature,
		                  String superName, String[] interfaces) {
			super.visit(version, access, name, signature, "pl/asie/smoothwater/BlockLiquidForged", interfaces);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
		                                 String signature, String[] exceptions) {
			MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
			return "<init>".equals(name) ? new MV(Opcodes.ASM5, parent) : parent;
		}
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;

		ClassReader reader = new ClassReader(basicClass);
		if ("net/minecraft/block/BlockLiquid".equals(reader.getSuperName())) {
			System.out.println("[SmoothWaterCore] Patched " + transformedName + "!");

			ClassWriter writer = new ClassWriter(0);
			ClassVisitor visitor = new CV(Opcodes.ASM5, writer);

			reader.accept(visitor, 0);
			return writer.toByteArray();
		} else {
			return basicClass;
		}
	}
}
