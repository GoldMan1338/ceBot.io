package io.github.ceBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import io.github.ceBot.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfoCommand extends Command {
    @Override
    public CommandInfo getInfo() {
        return new CommandInfo("%cmdname%",
                "Display information about the bot including uptime, author, and how the bot was made.");
    }

    @Override
    public Set<String> getNames() {
        return Stream.of("info").collect(Collectors.toSet());
    }

    @Override
    protected Mono<Message> run(MessageCreateEvent event, String[] args) {
        Consumer<EmbedCreateSpec> spec = embed -> {
            embed.setAuthor("Information", null, null);
            embed.setDescription("");
            embed.addField("Author", "", true);
            embed.addField("Discord4J Version", "3.0.0", true);
            //currently broken with latest v3 commit
            //embed.addField("Discord4J Version", VersionUtil.getProperties().getProperty(VersionUtil.APPLICATION_VERSION), true);
            embed.addField("Prefix", Main.getPrefix(event.getClient(), event.getGuildId().get()), false);
            embed.addField("Uptime", getUptime(), false);
        };
        return event.getMessage().getChannel()
                .flatMap(c -> c.createMessage(m -> m
                        .setContent("\u200BTo find out my commands, use `" + Main.getPrefix(event.getClient(), event.getGuildId().get()) + "help`")
                        .setEmbed(spec)));
    }

    private static String getUptime() {
        Duration duration = Duration.ofMillis(System.currentTimeMillis() - Main.getFirstOnline());
        List<String> list = new ArrayList<>();

        if (duration.toDays() > 0)
            list.add(duration.toDays() == 1 ? duration.toDays() + " day" : duration.toDays() + " days");
        duration = duration.minusDays(duration.toDays());

        if (duration.toHours() > 0)
            list.add(duration.toHours() == 1 ? duration.toHours() + " hour" : duration.toHours() + " hours");
        duration = duration.minusHours(duration.toHours());

        if (duration.toMinutes() > 0)
            list.add(duration.toMinutes() == 1 ? duration.toMinutes() + " minute" : duration.toMinutes() + " minutes");
        duration = duration.minusMinutes(duration.toMinutes());

        if (duration.getSeconds() > 0)
            list.add(duration.getSeconds() == 1 ? duration.getSeconds() + " second" : duration.getSeconds() + " seconds");

        if (list.isEmpty()) return "Less than a second";
        return String.join(", ", list);
    }
}