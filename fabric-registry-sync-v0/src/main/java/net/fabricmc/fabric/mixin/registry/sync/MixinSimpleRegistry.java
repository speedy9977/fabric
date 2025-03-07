/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.registry.sync;

import java.util.Set;

import com.mojang.serialization.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;

@Mixin(SimpleRegistry.class)
public abstract class MixinSimpleRegistry<T> extends Registry<T> {
	@Unique
	private static final Logger FARBIC_LOGGER = LoggerFactory.getLogger("FabricRegistrySync");

	// Namespaces used by the vanilla game. "brigadier" is used by command argument type registry.
	// While Realms use "realms" namespace, it is irrelevant for Registry Sync.
	@Unique
	private static final Set<String> VANILLA_NAMESPACES = Set.of("minecraft", "brigadier");

	protected MixinSimpleRegistry(RegistryKey<Registry<T>> arg, Lifecycle lifecycle) {
		super(arg, lifecycle);
	}

	@Inject(method = "add", at = @At("RETURN"))
	private <V extends T> void add(RegistryKey<Registry<T>> registryKey, V entry, Lifecycle lifecycle, CallbackInfoReturnable<V> info) {
		onChange(registryKey);
	}

	@Inject(method = "set", at = @At("RETURN"))
	private <V extends T> void set(int rawId, RegistryKey<Registry<T>> registryKey, V entry, Lifecycle lifecycle, CallbackInfoReturnable<V> info) {
		onChange(registryKey);
	}

	@Unique
	private void onChange(RegistryKey<Registry<T>> registryKey) {
		if (RegistrySyncManager.postBootstrap || !VANILLA_NAMESPACES.contains(registryKey.getValue().getNamespace())) {
			RegistryAttributeHolder holder = RegistryAttributeHolder.get(this);

			if (!holder.hasAttribute(RegistryAttribute.MODDED)) {
				Identifier id = getKey().getValue();
				FARBIC_LOGGER.debug("Registry {} has been marked as modded, registry entry {} was changed", id, registryKey.getValue());
				RegistryAttributeHolder.get(this).addAttribute(RegistryAttribute.MODDED);
			}
		}
	}
}
