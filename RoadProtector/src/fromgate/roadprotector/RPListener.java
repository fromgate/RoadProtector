package fromgate.roadprotector;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RPListener implements Listener {
	RoadProtector plg;
	RPUtil u;

	public RPListener (RoadProtector plg) {
		this.plg = plg;
		this.u = plg.u;
	}


	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerMove (PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
	//	u.BC("Player speed: "+ p.getWalkSpeed());
		p.setWalkSpeed(1);
		
		if (plg.speedway&&p.isSprinting()&&
				p.hasPermission("roadprotector.speedway")&&
				plg.isPlayerOnRoad(p)&&
				(plg.isProtected(p.getLocation().getBlock())))
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,15,plg.speed));



		plg.protectWalking(p); //все проверки внутри процедуры ;)

	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplodeEvent (EntityExplodeEvent event) {
		if (plg.explosion_protect)
			if (event.blockList().size()>0)
				for (int i = event.blockList().size()-1;i>=0;i--)
					if (plg.isProtected(event.blockList().get(i))) event.blockList().remove(i);

	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak (BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if ((!plg.EditMode (p))&&(!u.isItemInList(b.getTypeId(),b.getData(), plg.exclusion_break))&&(plg.isProtected (event.getBlock()))) {
			plg.printRpMsg(p, plg.prtmsg);
			event.setCancelled(true);			
		}
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockFromTo (BlockFromToEvent event) {
		if ((plg.lavaprotect||plg.waterprotect)&&(plg.isProtected (event.getToBlock()))){
			if ((plg.lavaprotect)&&((event.getBlock().getType()==Material.STATIONARY_LAVA)||
					(event.getBlock().getType()==Material.LAVA))) event.setCancelled(true);
			if ((plg.waterprotect)&&((event.getBlock().getType()==Material.STATIONARY_WATER)||
					(event.getBlock().getType()==Material.WATER))) event.setCancelled(true);	
		}
	}


	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace (BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if ((!plg.EditMode (p))&&(!u.isItemInList(b.getTypeId(), b.getData(),plg.exclusion_place))&&(plg.isProtected (event.getBlock()))) {
			plg.printRpMsg(p, plg.prtmsg);
			event.setCancelled(true);			
		}
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract (PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)||event.getAction().equals(Action.LEFT_CLICK_BLOCK))
			if (u.isIdInList(event.getClickedBlock().getTypeId(), plg.switchprt)) {
				if ((!plg.EditMode (p)&&(plg.isProtected (event.getClickedBlock())))) {
					plg.printRpMsg(p, plg.prtclickmsg);
					event.setCancelled(true);
				}
			}

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)&&
				(!plg.EditMode (p))&&
				((p.getItemInHand().getType()==Material.BUCKET)||
						(p.getItemInHand().getType()==Material.WATER_BUCKET)||
						(p.getItemInHand().getType()==Material.LAVA_BUCKET))&&
						(plg.isProtected (event.getClickedBlock()))){
			plg.printRpMsg(p, plg.prtmsg);
			event.setCancelled(true);
		}
	}


	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin (PlayerJoinEvent event) {
		Player p = event.getPlayer();
		u.UpdateMsg(p);

		if (plg.editmode.containsKey(p.getName())) plg.editmode.put(p.getName(), false);
		if (plg.wandmode.containsKey(p.getName())) plg.wandmode.put(p.getName(), false);
		if (plg.walkmode.containsKey(p.getName())) plg.walkmode.put(p.getName(), false);


	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerTeleport (PlayerTeleportEvent event){
		if ((event.getCause() != TeleportCause.UNKNOWN)&&plg.walkmode.containsKey(event.getPlayer().getName()))
			plg.walkmode.put(event.getPlayer().getName(), false);
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlaceProtector (PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK))&&
				(plg.wandmode.containsKey(p.getName()))&&
				(p.getItemInHand().getTypeId()==plg.rpwand)&&
				(plg.wandmode.get(p.getName()))&&
				(p.hasPermission("roadprotector.edit"))	){
			int dd = -1;
			if (u.isIdInList(event.getClickedBlock().getTypeId(), plg.rails)) dd = -2;
			Block nb = event.getClickedBlock().getRelative(0, dd, 0);
			nb.setTypeId(plg.protector);
			if (plg.effect) plg.ShowEffect(nb.getRelative(0, 2, 0).getLocation());
			else u.PrintMSG (p,"msg_prtinstall", " ["+nb.getWorld().getName()+"] ("+Integer.toString(nb.getX())+", "+Integer.toString(nb.getY())+", "+Integer.toString(nb.getZ())+")"); 
		}
	}
}