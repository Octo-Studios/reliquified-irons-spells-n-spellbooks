package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

public class HyperfocusGloveItem extends SpellbooksRelic {
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("absorption")
                                .stat(StatData.builder("charges")
                                        .initialValue(2D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.22D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("improvement")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.067)
                                        .formatValue(value -> (int) MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff107087)
                                .borderBottom(0xff673824)
                                .textured(true)
                                .build())
                        .beams(BeamsData.builder()
                                .startColor(0xFFf2ee10)
                                .endColor(0x00083a64)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("absorption")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry()
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        if (!isAbilityUnlocked(stack, "absorption"))
            return super.getRelicAttributeModifiers(stack);

        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(AttributeRegistry.MAX_MANA, (int) getStatValue(stack, "empower", "capacity"), AttributeModifier.Operation.ADD_VALUE))
                .build();
    }

    public void addCharges(ItemStack stack, int count) {
        setCharges(stack, (int) Math.min(getCharges(stack) + count, MathUtils.round(getStatValue(stack, "absorption", "charges"), 0)));
    }

    public int getCharges(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.CHARGE, 0);
    }

    public void setCharges(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.CHARGE, Math.max(val, 0));
    }

    public String getTarget(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TARGET, "empty");
    }

    public void setTarget(ItemStack stack, String val) {
        stack.set(DataComponentRegistry.TARGET, val);
    }

    @EventBusSubscriber
    public static class HyperfocusGloveEvent {
        @SubscribeEvent
        public static void onCast(SpellOnCastEvent event) {
//            var player = event.getEntity();
//
//            var stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HYPERFOCUS_GLOVE.value());
//
//            if (!(stack.getItem() instanceof HyperfocusGloveItem relic) || !relic.isAbilityUnlocked(stack, "absorption"))
//                return;
//
//            relic.addCharges(stack, 1);
        }
    }
}
