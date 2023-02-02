package uk.jamieisgeek;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class PrivateVCs implements EventListener {
    private YamlFile config;
    private Map<Member, VoiceChannel> personalChannels;

    private void makeConfigFile() throws IOException {
        File folder = new File("plugins/PrivateVCs");

        if (!folder.exists()) folder.mkdir();

        File file = new File(folder.getAbsolutePath() + "/config.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = new YamlFile();
        config.load(file);

        if(config.get("host-vc") == null){
            config.set("host-vc", "123");
            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() throws IOException {
        System.out.println("Loading PrivateVSs...");
        this.makeConfigFile();
        this.personalChannels = new HashMap<>();
        System.out.println("PrivateVSs loaded!");
    }

    public void unload() {
        System.out.println("PrivateVSs unloaded!");
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof GuildVoiceJoinEvent vcJoinEvent) {
            Member member = vcJoinEvent.getMember();
            VoiceChannel channel = vcJoinEvent.getChannelJoined();

            if(!channel.getId().equals(config.getString("host-vc"))) return;

            VoiceChannel personalChannel = channel.getGuild().createVoiceChannel(member.getEffectiveName() + "'s VC")
                            .setParent(channel.getParent())
                            .addMemberPermissionOverride(member.getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT), null)
                            .addRolePermissionOverride(channel.getGuild().getPublicRole().getIdLong(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT))
                            .complete();
            channel.getGuild().moveVoiceMember(member, personalChannel).queue();

            personalChannels.put(member, personalChannel);
        } else if (event instanceof GuildVoiceLeaveEvent vcLeaveEvent) {
            Member member = vcLeaveEvent.getMember();
            VoiceChannel channel = vcLeaveEvent.getChannelLeft();

            if(!personalChannels.containsKey(member)) return;

            if(channel.getId().equals(personalChannels.get(member).getId())) {
                personalChannels.get(member).delete().queue();
                personalChannels.remove(member);
            }
        }
    }
}