package me.fromgate.roadprotector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RPCommands implements CommandExecutor {

    private RoadProtector plg;
    private RPUtil u;

    protected RPCommands(RoadProtector plg) {
        this.plg = plg;
        this.u = plg.u;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        boolean result = false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("roadprotector.edit")) {
                if (!plg.editmode.containsKey(p.getName())) plg.editmode.put(p.getName(), false);
                if (!plg.wandmode.containsKey(p.getName())) plg.wandmode.put(p.getName(), false);
                if (!plg.walkmode.containsKey(p.getName())) plg.walkmode.put(p.getName(), false);
            }
            if ((args.length > 0) && (u.checkCmdPerm(p, args[0]))) {
                if (args.length == 1) result = ExecuteCmd(p, args[0]);
                else if (args.length >= 1) {
                    String arg = "";
                    if (args.length == 2) arg = args[1];
                    else {
                        for (int i = 1; i < args.length; i++) arg = arg + " " + args[i];
                        arg = arg.trim();
                    }
                    result = ExecuteCmd(p, args[0], arg);
                }
                if ((result) && (u.equalCmdPerm(args[0], "config"))) plg.SaveCfg();

            } else u.printPxMsg(p, u.getMSG("cmd_wrong", '4'));
        } else u.printPxMsg(sender, u.getMSG("cmd_console", 'c'));
        return result;
    }

    // Без параметров
    protected boolean ExecuteCmd(Player p, String cmd) {
        String pname = p.getName();
        if (cmd.equalsIgnoreCase("edit")) {
            plg.editmode.put(pname, !plg.editmode.get(pname));
            u.printMSG(p, "cmd_editmode", u.EnDis(plg.editmode.get(pname)));
        } else if (cmd.equalsIgnoreCase("unpr")) {
            if (plg.isProtected(p.getLocation().getBlock()))
                u.printMSG(p, "clp_prtmsgdef", plg.unProtect(p.getLocation().getBlock()));
            else u.printMSG(p, "clp_notprotected");
        } else if (cmd.equalsIgnoreCase("prtmsg")) {
            plg.prtmsg = "";
            u.printMSG(p, "cfg_prtmsg", u.getMSG("msg_warn_build"));
        } else if (cmd.equalsIgnoreCase("swmsg")) {
            plg.prtclickmsg = "";
            u.printMSG(p, "cfg_swmsg", u.getMSG("msg_warn_switch"));
        } else if (cmd.equalsIgnoreCase("snowblower")) {
            plg.snowblower = !plg.snowblower;
            u.printEnDis(p, "cmd_snowblower", plg.snowblower);
        } else if (cmd.equalsIgnoreCase("wand")) {
            plg.wandmode.put(pname, !plg.wandmode.get(pname));
            u.printMSG(p, "cmd_wandmode", u.EnDis(plg.wandmode.get(pname)));
        } else if (cmd.equalsIgnoreCase("walk")) {
            plg.walkmode.put(pname, !plg.walkmode.get(pname));
            u.printMSG(p, "cmd_walkmode", u.EnDis(plg.walkmode.get(pname)));
        } else if (cmd.equalsIgnoreCase("walkroad")) {
            plg.walkroad = !plg.walkroad;
            u.printMSG(p, "cmd_walkroadmode", u.EnDis(plg.walkroad));
        } else if (cmd.equalsIgnoreCase("cfg")) {
            plg.PrintCfg(p);
        } else if (cmd.equalsIgnoreCase("reload")) {
            plg.LoadCfg();
            u.printMSG(p, "cmd_configreload");
        } else if (cmd.equalsIgnoreCase("effect")) {
            plg.effect = !plg.effect;
            if (plg.effect) u.printMSG(p, "cmd_showeff");
            else u.printMSG(p, "cmd_showmsg");
        } else if (cmd.equalsIgnoreCase("crmode")) {
            plg.crmedit = !plg.crmedit;
            if (plg.crmedit) u.printMSG(p, "cmd_crmode_on");
            else u.printMSG(p, "cmd_crmode_off");
        } else if (cmd.equalsIgnoreCase("lava")) {
            plg.lavaprotect = !plg.lavaprotect;
            u.printEnDis(p, "cfg_lavaflow", plg.lavaprotect);
        } else if (cmd.equalsIgnoreCase("speedway")) {
            plg.speedway = !plg.speedway;
            u.printEnDis(p, "cfg_speedway", plg.speedway);
        } else if (cmd.equalsIgnoreCase("water")) {
            plg.waterprotect = !plg.waterprotect;
            u.printEnDis(p, "cfg_waterflow", plg.waterprotect);
        } else if (cmd.equalsIgnoreCase("explosion")) {
            plg.explosion_protect = !plg.explosion_protect;
            u.printEnDis(p, "cfg_explosion", plg.explosion_protect);
        } else if (cmd.equalsIgnoreCase("swlist")) {
            plg.switchprt = "";
            u.printMSG(p, "cfg_swithcprt", u.getMSG("empty"));
        } else if (cmd.equalsIgnoreCase("explace")) {
            plg.exclusion_place = "";
            u.printMSG(p, "cfg_allowplace", u.getMSG("empty"));
        } else if (cmd.equalsIgnoreCase("exbreak")) {
            plg.exclusion_break = "";
            u.printMSG(p, "cfg_allowbreak", u.getMSG("empty"));
        } else if (cmd.equalsIgnoreCase("help")) {
            u.PrintHLP(p);
        } else return false;
        return true;
    }

    //команда + параметры
    protected boolean ExecuteCmd(Player p, String cmd, String arg) {
        if (cmd.equalsIgnoreCase("swlist")) {
            plg.switchprt = arg.trim().replaceAll(" ", "");
            u.printMSG(p, "cfg_switchprt", plg.switchprt);
        } else if (cmd.equalsIgnoreCase("unpr") && arg.matches("[1-9]+[0-9]*")) {
            int prt = plg.unProtect(p.getLocation().getBlock(), Integer.parseInt(arg));
            if (prt > 0) u.printMSG(p, "clp_prtmsgdef", prt);
            else u.printMSG(p, "clp_notprotected");
        } else if (cmd.equalsIgnoreCase("prtmsg")) {
            plg.prtmsg = arg.trim();
            u.printMSG(p, "cfg_prtmsg", plg.prtmsg);
        } else if (cmd.equalsIgnoreCase("swmsg")) {
            plg.prtclickmsg = arg.trim();
            u.printMSG(p, "cfg_swmsg", plg.prtclickmsg);
        } else if (cmd.equalsIgnoreCase("speedblock")) {
            plg.speedblocks = arg.trim().replace(" ", "");
            u.printMSG(p, "cfg_speedblocks", plg.speedblocks);
        } else if (cmd.equalsIgnoreCase("speed")) {
            plg.speed = 0;
            if (arg.matches("[1-9]+[0-9]*")) plg.speed = Integer.parseInt(arg);
            u.printMSG(p, "cfg_speed", Integer.toString(plg.speed));
        } else if (cmd.equalsIgnoreCase("explace")) {
            plg.exclusion_place = arg.trim().replaceAll(" ", "");
            u.printMSG(p, "cfg_explace", plg.exclusion_place);
        } else if (cmd.equalsIgnoreCase("exbreak")) {
            plg.exclusion_break = arg.trim().replaceAll(" ", "");
            u.printMSG(p, "cfg_exbreak", plg.exclusion_place);
        } else if (cmd.equalsIgnoreCase("efftype")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.efftype = Integer.parseInt(arg);
                u.printMSG(p, "cmd_effect", plg.Eff2Str(plg.efftype));
            } else {
                plg.efftype = 0;
                u.printMSG(p, "cmd_effectdefault", plg.Eff2Str(plg.efftype));
            }
        } else if (cmd.equalsIgnoreCase("w")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.dxz = Integer.parseInt(arg);
                u.printMSG(p, "cmd_prtwidth", Integer.toString(plg.dxz));
            } else u.printMSG(p, "cmd_prtwidthwrong", Integer.toString(plg.dxz));
        } else if (cmd.equalsIgnoreCase("h")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.yup = Integer.parseInt(arg);
                u.printMSG(p, "cmd_prtheight", Integer.toString(plg.yup));
            } else u.printMSG(p, "cmd_prtheightwrong", Integer.toString(plg.yup));
        } else if (cmd.equalsIgnoreCase("d")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.ydwn = Integer.parseInt(arg);
                u.printMSG(p, "cmd_prtdepth", Integer.toString(plg.ydwn));
            } else u.printMSG(p, "cmd_prtdepthwrong", Integer.toString(plg.ydwn));
        } else if (cmd.equalsIgnoreCase("setwand")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.rpwand = Integer.parseInt(arg);
                u.printMSG(p, "cmd_rpwand", Integer.toString(plg.rpwand));
            } else {
                plg.rpwand = 337;
                u.printMSG(p, "cmd_rpwanddefault", Integer.toString(plg.rpwand));
            }
        } else if (cmd.equalsIgnoreCase("prblock")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.protector = Integer.parseInt(arg);
                u.printMSG(p, "cmd_prtblock", Integer.toString(plg.protector));
            } else {
                plg.protector = 7;
                u.printMSG(p, "cmd_prtblockdefault", Integer.toString(plg.protector));
            }
        } else if (cmd.equalsIgnoreCase("unprblock")) {
            if (arg.matches("[1-9]+[0-9]*")) {
                plg.unprotector = Integer.parseInt(arg);
                u.printMSG(p, "cmd_unprtblock", Integer.toString(plg.unprotector));
            } else {
                plg.protector = 1;
                u.printMSG(p, "cmd_unprtblockdefault", Integer.toString(plg.unprotector));
            }
        } else if (cmd.equalsIgnoreCase("help")) {
            u.printHLP(p, arg);
        } else return false;
        return true;
    }

}
