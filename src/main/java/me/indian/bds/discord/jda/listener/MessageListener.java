package me.indian.bds.discord.jda.listener;

import java.util.concurrent.TimeUnit;
import me.indian.bds.BDSAutoEnable;
import me.indian.bds.config.Config;
import me.indian.bds.discord.jda.DiscordJda;
import me.indian.bds.logger.Logger;
import me.indian.bds.server.ServerProcess;
import me.indian.bds.util.MinecraftUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    private final DiscordJda discordJda;
    private final Logger logger;
    private final Config config;
    private TextChannel textChannel;
    private TextChannel consoleChannel;
    private ServerProcess serverProcess;

    public MessageListener(final DiscordJda discordJda, final BDSAutoEnable bdsAutoEnable) {
        this.discordJda = discordJda;
        this.logger = bdsAutoEnable.getLogger();
        this.config = bdsAutoEnable.getConfig();
    }

    public void init() {
        this.textChannel = this.discordJda.getTextChannel();
        this.consoleChannel = this.discordJda.getConsoleChannel();
    }

    public void initServerProcess(final ServerProcess serverProcess) {
        this.serverProcess = serverProcess;
    }

    @Override
    public void onMessageUpdate(final MessageUpdateEvent event) {
        if (event.getAuthor().isBot()) return;
        final User author = event.getAuthor();
        final Message message = event.getMessage();
        final String rawMessage = message.getContentRaw();

        if (event.getChannel().asTextChannel() == this.textChannel) {
            final Role role = this.discordJda.getHighestRole(author.getIdLong());
            if (this.checkLength(message)) return;

            final String msg = this.config.getMessages().getDiscordToMinecraftMessage()
                    .replaceAll("<name>", author.getName())
                    .replaceAll("<msg>", rawMessage)
                    .replaceAll("<reply>", this.generatorReply(message.getReferencedMessage()))
                    .replaceAll("<role>", role == null ? "" : role.getName()) + this.config.getMessages().getEdited();

            this.serverProcess.sendToConsole(MinecraftUtil.tellrawToAllMessage(msg));
            this.logger.info(msg);
        }
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;

        final Member member = event.getMember();
        final User author = event.getAuthor();
        final Message message = event.getMessage();
        final String rawMessage = message.getContentRaw();


        if (event.getChannel().asTextChannel() == this.consoleChannel) {
            if (member == null) return;
            if (member.hasPermission(Permission.ADMINISTRATOR)) {
                event.getChannel().sendMessage(this.serverProcess.commandAndResponse(rawMessage)).queue();
            } else {
                event.getChannel().sendMessage("Nie masz uprawnień administratora aby wysłać tu wiadomość").queue(msg -> {
                    msg.delete().queueAfter(5, TimeUnit.SECONDS);
                    message.delete().queueAfter(4, TimeUnit.SECONDS);
                });
            }
            return;
        }
        if (event.getChannel().asTextChannel() == this.textChannel) {
            final Role role = this.discordJda.getHighestRole(author.getIdLong());
            if (this.checkLength(message)) return;

            final String msg = this.config.getMessages().getDiscordToMinecraftMessage()
                    .replaceAll("<name>", author.getName())
                    .replaceAll("<msg>", rawMessage)
                    .replaceAll("<reply>", this.generatorReply(message.getReferencedMessage()))
                    .replaceAll("<role>", role == null ? "" : role.getName());

            this.serverProcess.sendToConsole(MinecraftUtil.tellrawToAllMessage(msg));
            this.logger.info(msg);
        }
    }


    private boolean checkLength(final Message message) {
        if (message.getContentRaw().length() >= this.config.getMessages().getAllowedLength()) {
            this.sendPrivateMessage(message.getAuthor(), this.config.getMessages().getReachedMessage());
            if (this.config.getMessages().isDeleteOnReachLimit()) {
                message.delete().queue();
                this.sendPrivateMessage(message.getAuthor(), "`" + message.getContentRaw() + "`");
            }
            return true;
        }
        return false;
    }


    private void sendPrivateMessage(final User user, final String message) {
        user.openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(message)
                        .queue());
    }

    private String generatorReply(final Message messageReference) {
        return messageReference == null ? "" : this.config.getMessages()
                .getReplyStatement().replaceAll("<msg>", messageReference.getContentRaw())
                .replaceAll("<author>", messageReference.getAuthor().getName());
    }
}
