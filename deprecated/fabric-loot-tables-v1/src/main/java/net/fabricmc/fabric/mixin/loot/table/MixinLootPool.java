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

package net.fabricmc.fabric.mixin.loot.table;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;

import net.fabricmc.fabric.api.loot.v1.FabricLootPool;

@Mixin(LootPool.class)
public abstract class MixinLootPool implements FabricLootPool {
	@Shadow
	@Final
	LootPoolEntry[] entries;

	@Shadow
	@Final
	LootCondition[] conditions;

	@Shadow
	@Final
	LootFunction[] functions;

	@Shadow
	@Final
	LootNumberProvider rolls;

	@Override
	public List<LootPoolEntry> getEntries() {
		return ImmutableList.copyOf(entries);
	}

	@Override
	public List<LootCondition> getConditions() {
		return ImmutableList.copyOf(conditions);
	}

	@Override
	public List<LootFunction> getFunctions() {
		return ImmutableList.copyOf(functions);
	}

	@Override
	public LootNumberProvider getRolls() {
		return rolls;
	}
}
