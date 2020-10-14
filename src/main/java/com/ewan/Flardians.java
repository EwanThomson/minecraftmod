package com.ewan;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Random;

@Plugin(id = "flardians", name = "Flardians", version = "0.5", description = "BUY FLARD HERE")
public class Flardians {
    private static final List<ItemType> ITEM_TYPES = ImmutableList.of(ItemTypes.ACACIA_DOOR, ItemTypes.LEAVES2,
            ItemTypes.BOOKSHELF, ItemTypes.COAL, ItemTypes.COBBLESTONE, ItemTypes.ANVIL, ItemTypes.IRON_ORE,
            ItemTypes.APPLE, ItemTypes.WHEAT_SEEDS, ItemTypes.DIRT);

    // This field refers to the display name of the villager that will sell our stuff
    private static final Text FLARDARIAN = Text.of(TextColors.DARK_AQUA, TextStyles.BOLD, TextStyles.ITALIC, "Flardarian");

    // This field refers to the display name of our ItemStack
    private static final List<Text> DISPLAY_NAMES = ImmutableList.of(
            Text.of(TextColors.GREEN, TextStyles.ITALIC, "Book One"),
            Text.of(TextColors.GREEN, TextStyles.ITALIC, "Book Two"));

    // Here we define the Lore we will be using for out items.
    private static final Text LORE_FIRST = Text.of(TextColors.BLUE, TextStyles.ITALIC, "This is indeed a glorious day!");
    private static final Text LORE_SECOND = Text.of(TextColors.BLUE, TextStyles.ITALIC, "Shining sun makes the clouds flee");
    private static final Text LORE_THIRD = Text.of(TextColors.BLUE, TextStyles.ITALIC, "With State of ",
            TextColors.YELLOW, "Sponge", TextColors.BLUE, " again today");
    private static final Text LORE_FOURTH = Text.of(TextColors.BLUE, TextStyles.ITALIC, "Granting delights for you and me");
    private static final Text LORE_FIFTH = Text.of(TextColors.BLUE, TextStyles.ITALIC, "For ",
            TextColors.YELLOW, "Sponge", TextColors.BLUE, " is in a State of play");
    private static final Text LORE_SIXTH = Text.of(TextColors.BLUE, TextStyles.ITALIC, "Today, be happy as can be!");
    private static final ImmutableList<Text> LORE = ImmutableList.of(LORE_FIRST, LORE_SECOND, LORE_THIRD, LORE_FOURTH,
            LORE_FIFTH, LORE_SIXTH);

    private static final Random RANDOM = new Random();

    @Listener
    public void onSpawn(SpawnEntityEvent event) {
        // Here we create the villager that will sell out stuff.
        // Sponge takes inspiration from Entity systems, where any object can have any data.
        // The data we're setting here is then represented as the key.
        // Once we have our data we then offer the data to the entity using the specified key.
        for (Entity entity : event.getEntities()) {
            if (!entity.getType().equals(EntityTypes.VILLAGER)) {
                continue;
            }
            entity.offer(Keys.CAREER, Careers.CLERIC);
            entity.offer(Keys.DISPLAY_NAME, FLARDARIAN);
            entity.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            entity.offer(Keys.INVULNERABILITY_TICKS, 1);
            // Up until now we have offered the entity single pieces of data tied to keys.
            // Here we instead hand it a DataManipulator, which is like
            // a bundle of different data with the keys already associated with different fields.
            entity.offer(generateTradeOffer());
        }
    }

    private TradeOfferData generateTradeOffer() {
        final TradeOfferData tradeOfferData = Sponge.getDataManager().getManipulatorBuilder(TradeOfferData.class).get().create();
        ListValue<TradeOffer> tradeOffers = tradeOfferData.tradeOffers();
        for (int i = 0; i < 2; i++) {
            final DisplayNameData itemName = Sponge.getDataManager().getManipulatorBuilder(DisplayNameData.class).get().create();
            itemName.set(Keys.DISPLAY_NAME, DISPLAY_NAMES.get(i));

            final LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
            final ListValue<Text> lore = loreData.lore();
            lore.add(LORE.get(i));
            loreData.set(lore);

            final ItemStack selling = ItemStack.builder()
                    .itemType(ITEM_TYPES.get(i+1))
                    .itemData(itemName)
                    .itemData(loreData)
                    .quantity(1)
                    .build();

            tradeOffers.add(TradeOffer.builder()
                    .firstBuyingItem(ItemStack.of(ITEM_TYPES.get(i)))
                    .maxUses(10000)
                    .sellingItem(selling)
                    .build());
        }
        tradeOfferData.set(tradeOffers);
        return tradeOfferData;
    }

}