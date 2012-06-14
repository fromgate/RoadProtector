package fromgate.roadprotector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RPCommands implements CommandExecutor {
	
	private RoadProtector plg;
	private FGUtil u; 
	
	public RPCommands (RoadProtector plg) {
		this.plg = plg;
		this.u = plg.u;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		boolean result = false;
		if (sender instanceof Player){
			Player p = (Player) sender;
			if (p.hasPermission("roadprotector.edit")){
				if (!plg.editmode.containsKey(p.getName())) plg.editmode.put(p.getName(), false);
				if (!plg.wandmode.containsKey(p.getName())) plg.wandmode.put(p.getName(), false);
			}
			if ((args.length>0)&&(u.CheckCmdPerm(p, args[0]))){
				if (args.length == 1) result =  ExecuteCmd(p, args[0]);
				else if (args.length >= 1) {
					String arg = "";
					if (args.length == 2) arg = args[1];
					else {
						for (int i=1; i<args.length;i++) arg = arg+" "+args[i]; 
						arg = arg.trim();						
					}
					result = ExecuteCmd(p, args[0], arg);
				}
				if ((result)&&(u.equalCmdPerm(args[0], "roadprotector.config"))) plg.SaveCfg();
				
			} else u.PrintMsgPX(p,u.MSG("cmd_wrong",'4'));
		} else sender.sendMessage(u.px+u.MSG("cmd_console",'c'));
		return result;
	}

	// Без параметров
	public boolean ExecuteCmd (Player p, String cmd){
		String pname = p.getName();
		if (cmd.equalsIgnoreCase("edit")){
			plg.editmode.put(pname, !plg.editmode.get(pname));
			u.PrintMSG (p,"cmd_editmode",u.EnDis(plg.editmode.get(pname)));
		} else if (cmd.equalsIgnoreCase("prtmsg")){
			plg.prtmsg = "";
			u.PrintMSG(p, "cfg_prtmsg",u.MSG("msg_warn_build"));							
			return true;
		} else if (cmd.equalsIgnoreCase("swmsg")){
			plg.prtclickmsg = "";
			u.PrintMSG(p, "cfg_swmsg",u.MSG("msg_warn_switch"));							
			return true;
		} else if (cmd.equalsIgnoreCase("wand")){
			plg.wandmode.put(pname, !plg.wandmode.get(pname));
			u.PrintMSG (p,"cmd_wandmode",u.EnDis(plg.wandmode.get(pname)));
			return true;
		} else if (cmd.equalsIgnoreCase("cfg")){
			plg.PrintCfg(p);
			return true;
		} else if (cmd.equalsIgnoreCase("reload")){
			plg.LoadCfg();
			u.PrintMSG(p, "cmd_configreload");
			plg.PrintCfg(p);
			return true;
		} else if (cmd.equalsIgnoreCase("effect")){
			plg.effect = !plg.effect;
			if (plg.effect) u.PrintMSG (p,"cmd_showeff");
			else u.PrintMSG (p,"cmd_showmsg");
			
			return true;
		} else if (cmd.equalsIgnoreCase("crmode")){
			plg.crmedit = !plg.crmedit;
			if (plg.crmedit) u.PrintMSG (p,"cmd_crmode_on");
			else u.PrintMSG (p,"cmd_crmode_off");
			
			return true;
			
		} else if (cmd.equalsIgnoreCase("lava")){
			plg.lavaprotect = !plg.lavaprotect;
			u.PrintMSG (p,"cfg_lavaflow",u.EnDis(plg.lavaprotect));
			return true;
		} else if (cmd.equalsIgnoreCase("water")){
			plg.waterprotect = !plg.waterprotect;
			u.PrintMSG (p,"cfg_waterflow",u.EnDis(plg.waterprotect));
			return true;
			
		} else if (cmd.equalsIgnoreCase("explosion")){
			plg.explosion_protect= !plg.explosion_protect;
			u.PrintMSG (p,"cfg_explosion",u.EnDis(plg.explosion_protect));
			
			return true;
		} else if (cmd.equalsIgnoreCase("swlist")){
			plg.switchprt = "";
			u.PrintMSG (p,"cfg_swithcprt", u.MSG ("empty"));							
			
			return true;
		} else if (cmd.equalsIgnoreCase("explace")){
			plg.exclusion_place = "";
			u.PrintMSG (p,"cfg_allowplace", u.MSG ("empty"));							
			
			return true;
		} else if (cmd.equalsIgnoreCase("exbreak")){
			plg.exclusion_break= "";
			u.PrintMSG (p,"cfg_allowbreak", u.MSG ("empty"));							
			
			return true;
		} else if (cmd.equalsIgnoreCase("help")){
			u.PrintHLP(p);
			return true;

		}

		return false;
	}

	//команда + параметры
	public boolean ExecuteCmd (Player p, String cmd, String arg){

		if (cmd.equalsIgnoreCase("swlist")){
			plg.switchprt = arg.trim().replaceAll(" ", "");
			u.PrintMSG (p,"cfg_switchprt",plg.switchprt);							
			
			return true;
		} else if (cmd.equalsIgnoreCase("prtmsg")){
			plg.prtmsg = arg.trim();
			u.PrintMSG(p, "cfg_prtmsg",plg.prtmsg);							
			return true;

		} else if (cmd.equalsIgnoreCase("swmsg")){
			plg.prtclickmsg = arg.trim();
			u.PrintMSG(p, "cfg_swmsg",plg.prtclickmsg);							
			return true;
			
		} else if (cmd.equalsIgnoreCase("explace")){
			plg.exclusion_place = arg.trim().replaceAll(" ", "");
			u.PrintMSG(p, "cfg_explace",plg.exclusion_place);							
			
			return true;
		} else if (cmd.equalsIgnoreCase("exbreak")){
			plg.exclusion_break= arg.trim().replaceAll(" ", "");
			u.PrintMSG(p, "cfg_exbreak",plg.exclusion_place);							
			
			return true;
		} else if (cmd.equalsIgnoreCase("efftype")){
			if (arg.matches("[1-9]+[0-9]*")) {
				plg.efftype = Integer.parseInt(arg);
				u.PrintMSG(p,"cmd_effect",plg.Eff2Str(plg.efftype));
				return true;
			} else {
				plg.efftype = 0;
				u.PrintMSG(p,"cmd_effectdefault",plg.Eff2Str(plg.efftype));
			}
			
			return true;				

		} else if (cmd.equalsIgnoreCase("w")){
			if (arg.matches("[1-9]+[0-9]*")) {
				plg.dxz = Integer.parseInt(arg);
				u.PrintMSG(p,"cmd_prtwidth",Integer.toString(plg.dxz));
			} else u.PrintMSG(p,"cmd_prtwidthwrong",Integer.toString(plg.dxz));
			
			return true;
		} else if (cmd.equalsIgnoreCase("h")){
			if (arg.matches("[1-9]+[0-9]*")) {
				plg.yup = Integer.parseInt(arg);
				u.PrintMSG(p,"cmd_prtheight",Integer.toString(plg.yup));
			} else u.PrintMSG(p,"cmd_prtheightwrong",Integer.toString(plg.yup));
			
			return true;
		} else if (cmd.equalsIgnoreCase("d")){
			if (arg.matches("[1-9]+[0-9]*")) {
				plg.ydwn = Integer.parseInt(arg);
				u.PrintMSG(p,"cmd_prtdepth",Integer.toString(plg.ydwn));
			} else u.PrintMSG(p,"cmd_prtdepthwrong",Integer.toString(plg.ydwn));
			
			return true;
		} else if (cmd.equalsIgnoreCase("setwand")){
			if (arg.matches("[1-9]+[0-9]*")) {
				plg.rpwand = Integer.parseInt(arg);
				u.PrintMSG(p,"cmd_rpwand",Integer.toString(plg.rpwand));
			} else {
				plg.rpwand= 337;
				u.PrintMSG(p,"cmd_rpwanddefault",Integer.toString(plg.rpwand));
			}
			
			return true;
		} else if (cmd.equalsIgnoreCase("prblock")){

			if (arg.matches("[1-9]+[0-9]*")) {
				plg.protector = Integer.parseInt(arg);
				u.PrintMSG(p,"cmd_prtblock",Integer.toString(plg.protector));
			} else {
				plg.protector = 7;
				u.PrintMSG(p,"cmd_prtblockdefault",Integer.toString(plg.protector));
			}
			return true;

		} else if (cmd.equalsIgnoreCase("help")){
			u.PrintHLP(p, arg);
			return true;
		}

		return false;
	}

}
