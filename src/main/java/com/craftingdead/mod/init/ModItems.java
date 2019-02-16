package com.craftingdead.mod.init;

import com.craftingdead.mod.CraftingDead;
import com.craftingdead.mod.client.animation.GunAnimation;
import com.craftingdead.mod.client.animation.fire.PistolShootAnimation;
import com.craftingdead.mod.client.animation.fire.RifleShootAnimation;
import com.craftingdead.mod.item.FireMode;
import com.craftingdead.mod.item.ItemGun;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(CraftingDead.MOD_ID)
@Mod.EventBusSubscriber
public class ModItems {

	public static final Item ACR = null;
	public static final Item AK47 = null;
	public static final Item DESERT_EAGLE = null;
	public static final Item M4A1 = null;
	public static final Item M9 = null;
	public static final Item TASER = null;

	public static final Item RESIDENTIAL_LOOT = null;

	public static final Item CLIP = null;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(appendRegistryName("desert_eagle",
				new ItemGun(0, 0, 8, 2.5F, ImmutableList.of(FireMode.Modes.SEMI), () -> ModSoundEvents.DESERT_EAGLE_SHOOT,
						ImmutableMap.of(GunAnimation.Type.SHOOT, PistolShootAnimation::new))),
				appendRegistryName("acr",
						new ItemGun(80, 0, 7, 2F, ImmutableList.of(FireMode.Modes.AUTO, FireMode.Modes.SEMI),
								() -> ModSoundEvents.ACR_SHOOT,
								ImmutableMap.of(GunAnimation.Type.SHOOT, RifleShootAnimation::new))),
				appendRegistryName("ak47",
						new ItemGun(80, 0, 7, 2F, ImmutableList.of(FireMode.Modes.AUTO, FireMode.Modes.SEMI),
								() -> ModSoundEvents.AK47_SHOOT,
								ImmutableMap.of(GunAnimation.Type.SHOOT, RifleShootAnimation::new))),
				appendRegistryName("m4a1",
						new ItemGun(80, 0, 7, 1.5F, ImmutableList.of(FireMode.Modes.AUTO, FireMode.Modes.SEMI),
								() -> ModSoundEvents.M4A1_SHOOT,
								ImmutableMap.of(GunAnimation.Type.SHOOT, RifleShootAnimation::new))),
				appendRegistryName("m9",
						new ItemGun(80, 0, 7, 1F, ImmutableList.of(FireMode.Modes.SEMI), () -> ModSoundEvents.M9_SHOOT,
								ImmutableMap.of(GunAnimation.Type.SHOOT, PistolShootAnimation::new))),
				appendRegistryName("taser",
						new ItemGun(2000, 0, 7, 1F, ImmutableList.of(FireMode.Modes.SEMI),
								() -> ModSoundEvents.TASER_SHOOT,
								ImmutableMap.of(GunAnimation.Type.SHOOT, PistolShootAnimation::new))),
				appendRegistryName("residential_loot", new ItemBlock(ModBlocks.RESIDENTIAL_LOOT)),
				appendRegistryName("clip", new Item()));
	}

	private static Item appendRegistryName(String registryName, Item item) {
		return item.setRegistryName(new ResourceLocation(CraftingDead.MOD_ID, registryName))
				.setTranslationKey(String.format("%s%s%s", CraftingDead.MOD_ID, ".", registryName));
	}

}
