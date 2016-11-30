package me.fromgate.roadprotector;


public class RPUtil extends FGUtilCore {

    RoadProtector plg;


    public RPUtil(RoadProtector plg, boolean vcheck, boolean savelng, String language, String devbukkitname, String version_name, String plgcmd, String px) {
        super(plg, savelng, language, plgcmd);
        this.initUpdateChecker(version_name, "34970", "a2a7b26dd4dc9bc496c80de4b49e87cb42e34ae3", devbukkitname, vcheck);

        this.plg = plg;
        FillMSG();

        setPermPrefix("roadprotector");

        InitCmd();
        if (savelng) SaveMSG();
    }


    protected void InitCmd() {

        this.cmds.clear();
        this.cmdlist = "";

        addCmd("help", "edit", "hlp_helpcmd", "/rp help");
        addCmd("cfg", "edit", "hlp_cfg", "/rp cfg");
        addCmd("reload", "config", "hlp_reload", "/rp reload");
        addCmd("edit", "edit", "hlp_edit", "/rp edit");
        addCmd("wand", "edit", "hlp_wand", "/rp wand");
        addCmd("walk", "walk", "hlp_walk", "/rp walk");
        addCmd("walkroad", "config", "hlp_walkroad", "/rp walkroad");
        addCmd("unpr", "edit", "hlp_clp", "/rp clp [radius]");

        addCmd("setwand", "config", "hlp_setwand", "/rp wand [" + getMSG("hlp_cmdparam_item_id") + "]");
        addCmd("effect", "edit", "hlp_effect", "/rp effect");
        addCmd("efftype", "edit", "hlp_efftype", "/rp efftype [" + getMSG("hlp_cmdparam_efftype") + "]");
        addCmd("crmode", "config", "hlp_crmode", "/rp crmode");
        addCmd("w", "config", "hlp_w", "/rp w <" + getMSG("hlp_cmdparam_radius") + ">");
        addCmd("h", "config", "hlp_h", "/rp h <" + getMSG("hlp_cmdparam_height") + ">");
        addCmd("d", "config", "hlp_d", "/rp d <" + getMSG("hlp_cmdparam_depth") + ">");
        addCmd("swlist", "config", "hlp_swlist", "/rp swlist [" + getMSG("hlp_cmdparam_id") + "1," + getMSG("hlp_cmdparam_id") + "2,...," + getMSG("hlp_cmdparam_id") + "N]");
        addCmd("explace", "config", "hlp_explace", "/rp explace [" + getMSG("hlp_cmdparam_id") + "1," + getMSG("hlp_cmdparam_id") + "2,...," + getMSG("hlp_cmdparam_id") + "N]");
        addCmd("exbreak", "config", "hlp_exbreak", "/rp exbreak [" + getMSG("hlp_cmdparam_id") + "1," + getMSG("hlp_cmdparam_id") + "2,...," + getMSG("hlp_cmdparam_id") + "N]");
        addCmd("prblock", "config", "hlp_prblock", "/rp prblock [" + getMSG("hlp_cmdparam_block_id") + "]");
        addCmd("unprblock", "config", "hlp_unprblock", "/rp unprblock [" + getMSG("hlp_cmdparam_block_id") + "]");
        addCmd("prtmsg", "config", "hlp_prtmsg", "/rp prtmsg [" + getMSG("hlp_cmdparam_text") + "]");
        addCmd("swmsg", "config", "hlp_prtmsg", "/rp prtmsg [" + getMSG("hlp_cmdparam_text") + "]");
        addCmd("explosion", "config", "hlp_explosion", "/rp explosion");
        addCmd("lava", "config", "hlp_lava", "/rp lava");
        addCmd("water", "config", "hlp_water", "/rp water");
        addCmd("speedway", "config", "hlp_speedway", "/rp speedway");
        addCmd("speedblock", "config", "hlp_speedblock", "/rp speedblock <id1,id2,...,idN");
        addCmd("speed", "config", "hlp_speed", "/rp speed <speed potion level>");
        addCmd("snowblower", "config", "hlp_snowblower", "/rp snowblower");

    }

    protected void FillMSG() {
        //msg.clear();
        //msglist="";

        addMSG("disabled", "disabled");
        msg.put("disabled", "&c" + msg.get("disabled"));
        addMSG("empty", "Empty");
        msg.put("empty", "&c" + msg.get("empty"));
        addMSG("enabled", "enabled");
        msg.put("enabled", "&2" + msg.get("enabled"));
        addMSG("hlp_cmdparam_radius", "radius");
        addMSG("hlp_cmdparam_height", "height");
        addMSG("hlp_cmdparam_depth", "depth");
        addMSG("hlp_cmdparam_text", "text");
        addMSG("hlp_cmdparam_item_id", "item id");
        addMSG("hlp_cmdparam_efftype", "effect type");
        addMSG("hlp_cmdparam_id", "id");
        addMSG("hlp_cmdparam_block_id", "block id");
        addMSG("hlp_cmdparam_command", "command");
        addMSG("hlp_cmdparam_parameter", "parameter");
        addMSG("hlp_helpcmd", "%1% - show help page");
        addMSG("hlp_cfg", "%1% - current configuration");
        addMSG("hlp_reload", "%1% - reload configuration from file");
        addMSG("hlp_edit", "%1% - toggle edit-mode");
        addMSG("hlp_wand", "%1% - toggle wand-mode");
        addMSG("hlp_walk", "%1% - toggle walk-mode");


        addMSG("hlp_setwand", "%1% - set the wand item (default - clay)");
        addMSG("hlp_effect", "%1% - toggle effect/message when protector installed");
        addMSG("hlp_efftype", "%1% - effect type: 0 - smoke, 1 - flames, 2 - endermena signal, 3 - click sound");
        addMSG("hlp_crmode", "%1% - switch using creative mode as edit mode");
        addMSG("hlp_w", "%1% - set radius of protected area width");
        addMSG("hlp_h", "%1% - set protected area heigth");
        addMSG("hlp_d", "%1% - set protected area depth");
        addMSG("hlp_swlist", "%1% - set the switch-block list");
        addMSG("hlp_explace", "%1% - set the place-exclusion block list");
        addMSG("hlp_exbreak", "%1% - set the break-exclusion block list");
        addMSG("hlp_prblock", "%1% - set protector-block id (default - 7)");
        addMSG("hlp_prtmsg", "%1% - set warning message when building and breaking blocks");
        addMSG("hlp_swmsg", "%1% - set warning message when clicking blocks (defined by /rp swlist)");
        addMSG("hlp_explosion", "%1% - switch explosion protection");
        addMSG("hlp_lava", "%1% - switch protection from lava flow");
        addMSG("hlp_water", "%1% - switch protection from water flow");
        addMSG("msg_warn_build", "This place is protected!");
        addMSG("msg_warn_switch", "This place is protected!");
        addMSG("cfg_configuration", "Configuration");
        addMSG("cfg_prtwand", "Protector block: %1% Wand item id: %2% Unprotector: %3%");
        addMSG("cfg_prtarea", "Protected area dimensions (radius/height/depth):  %1%");
        addMSG("cfg_prtmsg", "Warning message (building, breaking) is set to: %1%");
        addMSG("cfg_swmsg", "Warning message (click levers, chests...) is set to: %1%");
        addMSG("cfg_effects", "Show effects: %1% Effect type: %2%");
        addMSG("cfg_crmode", "Ignoring protection in creative: %1%");
        addMSG("cfg_switchprt", "Protected switch list:  %1%");
        addMSG("cfg_explace", "Block allowed to place: %1%");
        addMSG("cfg_exbreak", "Block allowed to break: %1%");
        addMSG("cfg_explosion", "Explosion protection:  %1%");
        addMSG("cfg_snowblower", "Snowblower (protect from snow-forming): %1%");
        addMSG("cfg_lavaflow", "Protect from lava flow: %1%");
        addMSG("cfg_waterflow", "Protect from water flow: %1%");
        addMSG("cfg_psettings", "Player settings: ");
        addMSG("cfg_peditwand", "Edit mode: %1% Wand mode: %2% Walk mode: %3%");
        addMSG("cmd_wrong", "Something wrong (check command, permissions)");
        addMSG("cmd_console", "Sorry but you can use this command in-game only!");
        addMSG("cmd_editmode", "Edit mode is %1%");
        addMSG("cmd_wandmode", "Wand mode is %1%");
        addMSG("cmd_walkmode", "Walk mode is %1%");
        addMSG("cmd_walkroadmode", "Walk mode binding to roads only is %1%");
        addMSG("cmd_configreload", "Configuration reloaded from file:");
        addMSG("cmd_showeff", "Effects will played after installing new protector block");
        addMSG("cmd_showmsg", "You will see message after installing new protector block");
        addMSG("cmd_crmode_on", "Creative mode is works as road-protector edit mode");
        addMSG("cmd_crmode_off", "Creative mode is no longer road-protector edit mode");
        addMSG("cmd_prtswitchlist", "Protected switch-block list is set to: %1%");
        addMSG("cmd_allowplace", "Blocks allowed to place: %1%");
        addMSG("cmd_allowbreak", "Blocks allowed to break: %1%");
        addMSG("hlp_help", "Help");
        addMSG("hlp_helpexec", "%1% - execute command ");
        addMSG("hlp_helpcmdlist", "%1% - to get additional help");
        addMSG("hlp_commands", "Commands:");
        addMSG("cmd_prtwidth", "Protection zone's width radius is set to %1%");
        addMSG("cmd_prtwidthwrong", "try /rp w [width-radius], where width-radius is integer ");
        addMSG("cmd_prtheight", "Protection zone's height is set to %1%");
        addMSG("cmd_prtheightwrong", "try /rp h [height], where height is integer");
        addMSG("cmd_prtdepth", "Protection zone's depth is set to %1%");
        addMSG("cmd_prtdepthwrong", "try /rp d [depth], where depth is integer");
        addMSG("cmd_rpwand", "Road protector wand is set to %1%");
        addMSG("cmd_rpwanddefault", "Road protector wand is set to default - %1%");
        addMSG("cmd_prtblock", "Protector-block is set to %1%");
        addMSG("cmd_prtblockdefault", "Protector-block is set to default - %1%");
        addMSG("cmd_unknown", "Unknown command: %1%");
        addMSG("msg_prtinstall", "Road protector installed at %1%");
        addMSG("cfg_speedway", "Speedways: %1%");
        addMSG("cfg_speedblocks", "Road pavement: %1%");
        addMSG("cfg_speed", "Speedway speed (potion effect level): %1%");
        addMSG("cfg_speedways", "Speedways: %1% Speed: %2% Pavement blocks: %3%");
        addMSG("cmd_unprtblock", "Unprotector block is set to %1%");
        addMSG("cmd_unprtblockdefault", "Unprotector block is set to default - %1%");
        addMSG("hlp_speedway", "%1% - enable/disable speedways");
        addMSG("hlp_speedblock", "%1% - set the pavement blocklist");
        addMSG("hlp_speed", "%1% - set speedway sprinting speed");
        addMSG("hlp_clp", "%1% - remove protection around player");
        addMSG("clp_prtmsgdef", "Removed %1% protectors");
        addMSG("clp_notprotected", "No protectors found");
        addMSG("hlp_walkroad", "%1% - toggle walkmode binding to roads");
        addMSG("hlp_unprblock", "%1% - set unprotector block id (default - 1)");
        addMSG("hlp_snowblower", "%1% - toggle snowblower (preventing snow-forming on the roads)");
        addMSG("cmd_snowblower", "Snowblower");


    }

}
