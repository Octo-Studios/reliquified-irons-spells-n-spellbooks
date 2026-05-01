package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.WearableRelicItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ISASWearableRelicItem extends WearableRelicItem {
    @Override
    public String getConfigRoute() {
        return ReliquifiedIronsSpellsAndSpellbooks.MODID;
    }

    @Override
    public @Nullable String getURI(LivingEntity entity, ItemStack stack) {
        return "https://shatterbyte.com/docs/mods/reliquified-irons-spells-n-spellbooks/relics/" + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + "/";
    }
}