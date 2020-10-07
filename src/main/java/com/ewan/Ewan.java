package com.ewan;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import com.google.inject.Inject;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Plugin(id = "ewan", name = "Ewan's mod", version = "1.0", description = "blah blah")
public class Ewan {
    @Inject
    private PluginContainer container;

    CommandSpec helloCommand = CommandSpec.builder()
            .description(Text.of("Hello World Command"))
            .arguments(
                    GenericArguments.remainingJoinedStrings(Text.of("message")))
            .executor(new HelloWorldCommand())
            .build();
    CommandSpec addCommand = CommandSpec.builder()
            .description(Text.of("adds 2 numbers"))
            .arguments(
                    GenericArguments.integer(Text.of("num1")),
                    GenericArguments.integer(Text.of("num2"))
            )
            .executor(new AddCommand())
            .build();

    @Listener
    public void onStarting(GameStartingServerEvent event) {
        Sponge.getCommandManager().register(container, helloCommand, "helloworld", "hello", "test");
        Sponge.getCommandManager().register(container, addCommand, "add");
    }
}

class HelloWorldCommand implements CommandExecutor {
    ArrayList<String> list = new ArrayList<>();
    Random rand = new Random();
    static TextColor[] colors = new TextColor[]{
            TextColors.AQUA, TextColors.GOLD,
            TextColors.BLUE, TextColors.DARK_GREEN,
            TextColors.DARK_RED, TextColors.LIGHT_PURPLE
    };

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of("Hello Master eksthomson."));
        String message = args.<String>getOne("message").get();
        if (message.length() > 0) {
            list.add(message);
            for (String t : list) {
                TextColor color = colors[rand.nextInt(colors.length)];
                src.sendMessage(Text.of(color,
                        "\n oooooooooooo \n",
                        "0000000000(O)0\n",
                        "`````000000000\n",
                        "00000000000000\n",
                        "    0000000000\n",
                        "    0000000000"));
            }
        }
        return CommandResult.success();
    }
}

class AddCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int num1 = args.<Integer>getOne("num1").get();
        int num2 = args.<Integer>getOne("num2").get();
        src.sendMessage(Text.of(TextColors.GREEN, num1, " + ", num2, " = ", TextColors.YELLOW, num1 + num2));
        return CommandResult.success();
    }
}