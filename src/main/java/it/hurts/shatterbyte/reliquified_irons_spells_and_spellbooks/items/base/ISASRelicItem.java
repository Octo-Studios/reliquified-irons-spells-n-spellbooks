package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

public abstract class ISASRelicItem extends RelicItem {
    public ISASRelicItem(Item.Properties properties) {
        super(properties);
    }

    public ISASRelicItem() {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .stacksTo(1));
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedIronsSpellsAndSpellbooks.MODID;
    }

    @Override
    public @Nullable String getURI(LivingEntity entity, ItemStack stack) {
        return "https://shatterbyte.com/docs/mods/reliquified-irons-spells-n-spellbooks/relics/" + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + "/";
    }
}
