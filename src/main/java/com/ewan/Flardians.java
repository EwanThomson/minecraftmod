package com.ewan;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.lang.reflect.Field;
import java.util.HashMap;

@Plugin(id = "flardians", name = "Flardians", version = "0.1", description = "BUY FLARD HERE")
public class Flardians {
    private static final Text FLARDARIAN = Text.of(TextColors.DARK_AQUA, TextStyles.BOLD, TextStyles.ITALIC, "Flardarian");

    @Inject
    private PluginContainer container;

    TradeItems tradeItems;

    @Listener
    public void onStarting(GameStartingServerEvent event) {
        tradeItems = new TradeItems();

        CommandSpec merchantCommand = CommandSpec.builder()
                .description(Text.of("program the next villager that spawns"))
                .arguments(
                        GenericArguments.string(Text.of("buy")),
                        GenericArguments.string(Text.of("sell"))
                )
                .executor(new MerchantCommand(tradeItems))
                .build();
        Sponge.getCommandManager().register(container, merchantCommand, "merchant");
    }

    @Listener
    public void onSpawn(SpawnEntityEvent event) {
        if (this.tradeItems == null || this.tradeItems.buy == null) {
            return;
        }
        for (Entity entity : event.getEntities()) {
            if (!entity.getType().equals(EntityTypes.VILLAGER)) {
                continue;
            }
            entity.offer(Keys.CAREER, Careers.CLERIC);
            entity.offer(Keys.DISPLAY_NAME, FLARDARIAN);
            entity.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            entity.offer(Keys.INVULNERABILITY_TICKS, 1);
            entity.offer(generateTradeOffer());
        }
    }

    private TradeOfferData generateTradeOffer() {
        final TradeOfferData tradeOfferData = Sponge.getDataManager().getManipulatorBuilder(TradeOfferData.class).get().create();
        ListValue<TradeOffer> tradeOffers = tradeOfferData.tradeOffers();

        tradeOffers.add(TradeOffer.builder()
                .firstBuyingItem(ItemStack.of(this.tradeItems.buy))
                .maxUses(10000)
                .sellingItem(ItemStack.of(this.tradeItems.sell))
                .build());

        tradeOfferData.set(tradeOffers);
        return tradeOfferData;
    }

}

class MerchantCommand implements CommandExecutor {
    TradeItems tradeItems;
    HashMap<String, ItemType> itemNameMap;

    MerchantCommand(TradeItems tradeItems) {
        this.tradeItems = tradeItems;

        this.itemNameMap = new HashMap<>();
        for (Field field : ItemTypes.class.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == ItemType.class) {
                ItemType itemType;
                try {
                    itemType = (ItemType) field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                if (itemType.getName().startsWith("minecraft:")) {
                    this.itemNameMap.put(itemType.getName().substring(10), itemType);
                } else {
                    System.out.println("ignoring " + itemType.getName());
                }
            }
        }
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String buy = args.<String>getOne("buy").get();
        String sell = args.<String>getOne("sell").get();
        ItemType buyType = this.itemNameMap.get(buy);
        ItemType sellType = this.itemNameMap.get(sell);
        if (buyType == null || sellType == null) {
            if (buyType == null) {
                src.sendMessage(Text.of("couldn't find ", TextColors.RED, buy));
            }
            if (sellType == null) {
                src.sendMessage(Text.of("couldn't find ", TextColors.RED, sell));
            }
            return CommandResult.empty();
        }
        this.tradeItems.buy = buyType;
        this.tradeItems.sell = sellType;
        src.sendMessage(Text.of("buying ", TextColors.LIGHT_PURPLE, this.tradeItems.buy, TextColors.RESET,
                ", selling ", TextColors.GREEN, this.tradeItems.sell));
        return CommandResult.success();
    }
}

class TradeItems {
    public ItemType buy;
    public ItemType sell;
}