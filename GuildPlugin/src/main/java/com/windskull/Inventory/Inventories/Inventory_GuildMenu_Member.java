package com.windskull.Inventory.Inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.windskull.GuildPlugin.Guild;
import com.windskull.Inventory.InventoryTransactionGui_YesNo;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsManager;

public class Inventory_GuildMenu_Member extends Inventory_GuildMenu {

	public Inventory_GuildMenu_Member(Player p) {
		super(p);
		createInventory();
	}

	protected void createInventory() {
		clearInventory();
		createInventory_Member();
	}

	private void createInventory_Member()
	{
		clearInventory();
		this.setItem(15, ItemsCreator.getItemStack(Material.BARRIER,GuildsManager._ItemsColorNamePrimal +  "Opusc gildie"), e -> this.acceptLeaveGuild());
	}
	
	
	private void acceptLeaveGuild()
	{
		InventoryTransactionGui_YesNo accept = new InventoryTransactionGui_YesNo(this,(e) -> {leaveGuild();return true;}, player, "Potwierdz opuszczenie gildi",false);
		player.openInventory(accept.getInventory());
	}
	
	private void leaveGuild()
	{
		Guild g = guildPlayer.getGuild();
		g.removePlayerFromGuild(guildPlayer);
		g.getAllOnlineGuildPlayers().forEach(gp ->{ gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Gracz " + player.getName() + " opuscil gildie");});
		player.sendMessage("Opusciles gildie " + g.getName() );
	}
	
	
	
}
