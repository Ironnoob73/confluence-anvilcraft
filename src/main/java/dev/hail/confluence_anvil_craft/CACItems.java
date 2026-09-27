package dev.hail.confluence_anvil_craft;

import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.dubhe.anvilcraft.item.AnvilHammerItem;
import dev.hail.confluence_anvil_craft.item.HardmodeAnvilHammerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRATE;
import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;

@SuppressWarnings("unused")
public class CACItems {
    public static final ItemEntry<AnvilHammerItem> LEAD_ANVIL_HAMMER = REGISTRATE.item("lead_anvil_hammer", AnvilHammerItem::new)
            .properties(properties -> properties.durability(43))
            .register();
    public static final ItemEntry<HardmodeAnvilHammerItem> MYTHRIL_ANVIL_HAMMER = REGISTRATE.item("mythril_anvil_hammer",
                    properties -> new HardmodeAnvilHammerItem(properties.durability(10000)
                            .component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)
                            .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.RED)
                            .attributes(ItemAttributeModifiers.builder()
                                    .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                                            BASE_ATTACK_DAMAGE_ID,
                                            5 + ModTiers.MYTHRIL.getAttackDamageBonus(),
                                            AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.MAINHAND)
                                    .build()),
                            ModTiers.MYTHRIL))
            .register();
    public static final ItemEntry<HardmodeAnvilHammerItem> ORICHALCUM_ANVIL_HAMMER = REGISTRATE.item("orichalcum_anvil_hammer",
                    properties -> new HardmodeAnvilHammerItem(properties.durability(10000)
                            .component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)
                            .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.RED)
                            .attributes(ItemAttributeModifiers.builder()
                                    .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                                                    BASE_ATTACK_DAMAGE_ID,
                                                    5 + ModTiers.ORICHALCUM.getAttackDamageBonus(),
                                                    AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.MAINHAND)
                                    .build()),
                            ModTiers.ORICHALCUM))
            .register();

    public static void register() {
    }
}
