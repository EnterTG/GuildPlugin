package com.windskull.Inventory.AnviGui.Wrappers;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.ContainerAccess;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
public class AnvilContainer1_14_4_R1 extends ContainerAnvil {

    public AnvilContainer1_14_4_R1(Player player, int containerId,String title) {
        super(containerId, ((CraftPlayer) player).getHandle().inventory,
                ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
        this.checkReachable = false;
        setTitle(new ChatMessage(title));
    }

    @Override
    public void e() {
        super.e();
        this.levelCost.set(0);
    }

}
