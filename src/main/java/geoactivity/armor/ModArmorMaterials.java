package geoactivity.armor;

import geoactivity.GeoActivity;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * 1.10.2 armor materials. Reinforced: durability factor 30, {boots 2, legs 5, chest 6, helmet 2} like vanilla iron
 * (1.10.2 read its array in the wrong order and gave legs 6 / chest 5; fixed in the 3.0.0 balance pass), enchantability 18. Advanced: factor 35, enchantability 8, toughness 2; its armor points are
 * the 1.10.2 armor-bar values 3/8/6/3 (helmet/chest/legs/boots) — its extra absorption comes from perks.
 */
public final class ModArmorMaterials {
    public static final ResourceKey<EquipmentAsset> REINFORCED_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, GeoActivity.id("reinforced"));
    public static final ResourceKey<EquipmentAsset> ADVANCED_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, GeoActivity.id("advanced"));
    /** Nothing repairs them in an anvil (1.10.2 materials had no repair item). */
    public static final TagKey<Item> NO_REPAIR = TagKey.create(Registries.ITEM, GeoActivity.id("reinforced_tool_materials"));

    public static final ArmorMaterial REINFORCED = new ArmorMaterial(30,
            Map.of(ArmorType.BOOTS, 2, ArmorType.LEGGINGS, 5, ArmorType.CHESTPLATE, 6, ArmorType.HELMET, 2, ArmorType.BODY, 5),
            18, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, NO_REPAIR, REINFORCED_ASSET);

    public static final ArmorMaterial ADVANCED = new ArmorMaterial(35,
            Map.of(ArmorType.BOOTS, 3, ArmorType.LEGGINGS, 6, ArmorType.CHESTPLATE, 8, ArmorType.HELMET, 3, ArmorType.BODY, 11),
            8, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, NO_REPAIR, ADVANCED_ASSET);

    private ModArmorMaterials() {}
}
