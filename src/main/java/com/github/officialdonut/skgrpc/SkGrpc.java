package com.github.officialdonut.skgrpc;

import ch.njol.skript.Skript;
import com.github.officialdonut.skprotobuf.SkProtobuf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class SkGrpc extends JavaPlugin {

    private static SkGrpc instance;
    private RpcManager rpcManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        rpcManager = new RpcManager(SkProtobuf.getInstance().getProtoManager());
        rpcManager.loadDescriptors();

        try {
            Skript.registerAddon(this)
                    .setLanguageFileDirectory("lang")
                    .loadClasses("com.github.officialdonut.skgrpc" , "elements", "events");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to register Skript elements", e);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            SkProtobuf.getInstance().getProtoManager().loadDescriptors();
            rpcManager.loadDescriptors();
            sender.sendMessage("Successfully reloaded SkGrpc.");
            return true;
        }
        return false;
    }

    public static SkGrpc getInstance() {
        return instance;
    }

    public RpcManager getRpcManager() {
        return rpcManager;
    }
}
