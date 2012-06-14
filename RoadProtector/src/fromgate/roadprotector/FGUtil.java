package fromgate.roadprotector;


import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/* +    1. Проверка версий
 * +	2. Процедуры для обработчика комманда (перечень, печать хелпа -)
 */


public class FGUtil {
	RoadProtector plg;
	
	
	//конфигурация утилит
	public String px = ChatColor.translateAlternateColorCodes('&', "&3[RP]&f ");
	String pxlog = "[RP] "; // префикс для лог файла
	private String permprefix="roadprotector.";
	private boolean version_check = false; // включить после заливки на девбукит
	private String version_check_url = "http://dev.bukkit.org/server-mods/road-protector/files.rss";
	private String version_name = "Road Protector"; // идентификатор на девбукките (всегда должен быть такой!!!)
	private String version_info_perm = permprefix+"config"; // кого оповещать об обнволениях
	private String language="english";
	
	// Сообщения+перевод
	private HashMap<String,String> msg = new HashMap<String,String>(); //массив сообщений
	private char c1 = 'a'; //цвет 1 (по умолчанию для текста)
	private char c2 = '2'; //цвет 2 (по умолчанию для значений)
	private String msglist ="";
	
	private HashMap<String,Cmd> cmds = new HashMap<String,Cmd>();
	private String cmdlist ="";
	PluginDescriptionFile des;
	private double version_current=0;
	private double version_new=0;
	private String version_new_str="unknown";
	private Logger log = Logger.getLogger("Minecraft");
	
	public FGUtil(RoadProtector plg, boolean vcheck, String language){
		this.plg = plg;
		this.des = plg.getDescription();
		this.version_current = Double.parseDouble(des.getVersion().replaceFirst("\\.", "").replace("/", ""));
		this.version_check=vcheck;
		this.language = language;
		this.LoadMSG();
		//this.SaveMSG(); //для получения списка
		this.InitCmd();
	}

	
	/*
	 * Проверка версии 
	 */
	public void SetVersionCheck (boolean vc){
		this.version_check = vc;
	}
	
	
	// Вставить вызов в обработчик PlayerJoinEvent
	public void UpdateMsg (Player p){
		
		if (version_check){
			if (p.hasPermission(this.version_info_perm)){
				version_new = getNewVersion (version_current);
				if (version_new>version_current){
					PrintMSG(p, "msg_outdated","&6WeatherMan v"+plg.des.getVersion(),'e','6');
					PrintMSG(p,"msg_pleasedownload",version_new_str,'e','6');
					PrintMsg(p, "&3"+version_check_url.replace("files.rss", ""));
				}
			}
		}
	}

	// Вставить в обработчик onEnable
	public void UpdateMsg (){
		if (version_check){
			version_new = getNewVersion (version_current);
			if (version_new>version_current){
				log.info(pxlog+des.getName()+" v"+des.getVersion()+" is outdated! Recommended version is v"+version_new_str);
				log.info(pxlog+version_check_url.replace("files.rss", ""));
			}			
		}
	}
	
	private double getNewVersion(double currentVersion){
		if (version_check){
			try {
				URL url = new URL(version_check_url);
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("item");
				Node firstNode = nodes.item(0);
				if (firstNode.getNodeType() == 1) {
					Element firstElement = (Element)firstNode;
					NodeList firstElementTagName = firstElement.getElementsByTagName("title");
					Element firstNameElement = (Element)firstElementTagName.item(0);
					NodeList firstNodes = firstNameElement.getChildNodes();
					version_new_str = firstNodes.item(0).getNodeValue().replace(version_name+" v", "").trim();
					return Double.parseDouble(version_new_str.replaceFirst("\\.", "").replace("/", ""));
				}
			}
			catch (Exception e) {
			}
		}
		return currentVersion;
	}
	
	
	/*
	 * Процедуры для обработчика комманд
	 * 
	 */
	
	public void AddCmd (String cmd, String perm, String desc){
		cmds.put(cmd, new Cmd(this.permprefix+perm,desc));
		if (cmdlist.isEmpty()) cmdlist = cmd;
		else cmdlist = cmdlist+", "+cmd;
	}
	
	public void InitCmd(){
		cmds.clear();
		cmdlist = "";
		AddCmd("help", "edit",MSG("hlp_helpcmd","/rp help"));
		AddCmd("cfg", "edit",MSG("hlp_cfg","/rp cfg"));
		AddCmd("reload", "config",MSG("hlp_reload","/rp reload"));
		AddCmd("edit", "edit",MSG("hlp_edit","/rp edit"));
		AddCmd("wand", "edit",MSG("hlp_wand","/rp wand"));
		AddCmd("setwand", "config",MSG("hlp_setwand","/rp wand ["+MSG("hlp_cmdparam_item_id")+"]"));
		AddCmd("effect", "edit",MSG("hlp_effect","/rp effect"));
		AddCmd("efftype", "edit",MSG("hlp_efftype","/rp efftype ["+MSG("hlp_cmdparam_efftype")+"]"));
		AddCmd("crmode", "config",MSG("hlp_crmode","/rp crmode"));
		AddCmd("w", "config",MSG("hlp_w","/rp w <"+MSG("hlp_cmdparam_radius")+">"));
		AddCmd("h", "config",MSG("hlp_h","/rp h <"+MSG("hlp_cmdparam_height")+">"));
		AddCmd("d", "config",MSG("hlp_d","/rp d <"+MSG("hlp_cmdparam_depth")+">"));
		AddCmd("swlist", "config",MSG("hlp_swlist","/rp swlist ["+MSG("hlp_cmdparam_id")+"1,"+MSG("hlp_cmdparam_id")+"2,...,"+MSG("hlp_cmdparam_id")+"N]"));
		AddCmd("explace", "config",MSG("hlp_explace","/rp explace ["+MSG("hlp_cmdparam_id")+"1,"+MSG("hlp_cmdparam_id")+"2,...,"+MSG("hlp_cmdparam_id")+"N]"));
		AddCmd("exbreak", "config",MSG("hlp_exbreak","/rp exbreak ["+MSG("hlp_cmdparam_id")+"1,"+MSG("hlp_cmdparam_id")+"2,...,"+MSG("hlp_cmdparam_id")+"N]"));
		AddCmd("prblock", "config",MSG("hlp_prblock","/rp prblock ["+MSG("hlp_cmdparam_block_id")+"]"));
		AddCmd("prtmsg","config",MSG ("hlp_prtmsg","/rp prtmsg ["+MSG("hlp_cmdparam_text")+"]"));
		AddCmd("swmsg","config",MSG ("hlp_prtmsg","/rp prtmsg ["+MSG("hlp_cmdparam_text")+"]"));
		AddCmd("explosion", "config",MSG("hlp_explosion","/rp explosion"));
		AddCmd("lava", "config",MSG("hlp_lava","/rp lava"));
		AddCmd("water", "config",MSG("hlp_water","/rp water"));
	}
	
	
	public boolean CheckCmdPerm (Player p, String cmd){
		return ((cmds.containsKey(cmd.toLowerCase()))&&
				(cmds.get(cmd.toLowerCase()).perm.isEmpty()||((!cmds.get(cmd.toLowerCase()).perm.isEmpty())&&p.hasPermission(cmds.get(cmd.toLowerCase()).perm))));
	}
	
	public String getCmdList(){
		return cmdlist;
	}
	
	public boolean equalCmdPerm (String cmd, String perm){
		return (cmds.containsKey(cmd.toLowerCase())&&cmds.get(cmd.toLowerCase()).perm.equalsIgnoreCase(perm));
	}
	
	public class Cmd {
		String perm;
		String desc;
		public Cmd (String perm, String desc){
			this.perm = perm;
			this.desc = desc;
		}
	}
	
	
	/*
	 * Разные полезные процедурки 
	 * 
	 */
	public void PrintMsg(Player p, String msg){
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	public void PrintMsgPX(Player p, String msg){
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', px+msg));
	}


	public void BC (String msg){
		plg.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', px+msg));
	}
	
	
	/*
	 * Перевод
	 * 
	 */
	public void LoadMSG(){
		String lngfile = this.language+".lng";
		try {
			YamlConfiguration lng = new YamlConfiguration();
			File f = new File (plg.getDataFolder()+File.separator+lngfile);
			if (f.exists()) lng.load(f);
			
			FillMSG(lng);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void FillMSG(YamlConfiguration cfg){
		msg.clear();
		msglist="";
		addMSG ("disabled",cfg.getString("disabled", "disabled"));
		msg.put("disabled", "&c"+msg.get("disabled"));
		addMSG ("empty",cfg.getString("empty", "Empty"));
		msg.put("empty", "&c"+msg.get("empty"));
		addMSG ("enabled",cfg.getString("enabled", "enabled"));
		msg.put("enabled", "&2"+msg.get("enabled"));
		addMSG ("hlp_cmdparam_radius",cfg.getString("hlp_cmdparam_radius", "radius"));
		addMSG ("hlp_cmdparam_height",cfg.getString("hlp_cmdparam_height", "height"));
		addMSG ("hlp_cmdparam_depth",cfg.getString("hlp_cmdparam_depth", "depth"));
		addMSG ("hlp_cmdparam_text",cfg.getString("hlp_cmdparam_depth", "text"));
		addMSG ("hlp_cmdparam_item_id",cfg.getString("hlp_cmdparam_item_id", "item id"));
		addMSG ("hlp_cmdparam_efftype",cfg.getString("hlp_cmdparam_efftype", "effect type"));
		addMSG ("hlp_cmdparam_id",cfg.getString("hlp_cmdparam_id", "id"));
		addMSG ("hlp_cmdparam_block_id",cfg.getString("hlp_cmdparam_block_id", "block id"));
		addMSG ("hlp_cmdparam_command",cfg.getString("hlp_cmdparam_command", "command"));
		addMSG ("hlp_cmdparam_parameter",cfg.getString("hlp_cmdparam_parameter", "parameter"));
		addMSG ("hlp_helpcmd",cfg.getString("hlp_helpcmd", "%1% - show help page"));
		addMSG ("hlp_cfg",cfg.getString("hlp_cfg", "%1% - current configuration"));
		addMSG ("hlp_reload",cfg.getString("hlp_reload", "%1% - reload configuration from file"));
		addMSG ("hlp_edit",cfg.getString("hlp_edit", "%1% - switch edit-mode"));
		addMSG ("hlp_wand",cfg.getString("hlp_wand", "%1% - switch wand-mode"));
		addMSG ("hlp_setwand",cfg.getString("hlp_setwand", "%1% - set the wand item (default - clay)"));
		addMSG ("hlp_effect",cfg.getString("hlp_effect", "%1% - toggle effect/message when protector installed"));
		addMSG ("hlp_efftype",cfg.getString("hlp_efftype", "%1% - effect type: 0 - smoke, 1 - flames, 2 - endermena signal, 3 - click sound"));
		addMSG ("hlp_crmode",cfg.getString("hlp_crmode", "%1% - switch using creative mode as edit mode"));
		addMSG ("hlp_w",cfg.getString("hlp_w", "%1% - set radius of protected area width"));
		addMSG ("hlp_h",cfg.getString("hlp_h", "%1% - set protected area heigth"));
		addMSG ("hlp_d",cfg.getString("hlp_d", "%1% - set protected area depth"));
		addMSG ("hlp_swlist",cfg.getString("hlp_swlist", "%1% - set the switch-block list"));
		addMSG ("hlp_explace",cfg.getString("hlp_explace", "%1% - set the place-exclusion block list"));
		addMSG ("hlp_exbreak",cfg.getString("hlp_exbreak", "%1% - set the break-exclusion block list"));
		addMSG ("hlp_prblock",cfg.getString("hlp_prblock", "%1% - set protector-block id (default - 7)"));
		addMSG ("hlp_prtmsg",cfg.getString("hlp_prtmsg", "%1% - set warning message when building and breaking blocks"));
		addMSG ("hlp_swmsg",cfg.getString("hlp_prtmsg", "%1% - set warning message when clicking blocks (defined by /rp swlist)"));
		addMSG ("hlp_explosion",cfg.getString("hlp_explosion", "%1% - switch explosion protection"));
		addMSG ("hlp_lava",cfg.getString("hlp_lava", "%1% - switch protection from lava flow"));
		addMSG ("hlp_water",cfg.getString("hlp_water", "%1% - switch protection from water flow"));
		addMSG ("msg_warn_build",cfg.getString("msg_warn_build", "This place is protected!"));
		addMSG ("msg_warn_switch",cfg.getString("msg_warn_switch", "This place is protected!"));
		addMSG ("cfg_configuration",cfg.getString("cfg_configuration", "Configuration"));
		addMSG ("cfg_prtwand",cfg.getString("cfg_prtwand", "Protector block id: %1% Wand item id: %2%"));
		addMSG ("cfg_prtarea",cfg.getString("cfg_prtarea", "Protected area dimensions (radius/height/depth):  %1%"));
		addMSG ("cfg_prtmsg",cfg.getString("cfg_prtmsg", "Warning message (building, breaking) is set to: %1%"));
		addMSG ("cfg_swmsg",cfg.getString("cfg_swmsg", "Warning message (click levers, chests...) is set to: %1%"));
		addMSG ("cfg_effects",cfg.getString("cfg_effects", "Show effects: %1% Effect type: %2%"));
		addMSG ("cfg_crmode",cfg.getString("cfg_crmode", "Ignoring protection in creative: %1%"));
		addMSG ("cfg_switchprt",cfg.getString("cfg_switchprt", "Protected switch list:  %1%"));
		addMSG ("cfg_explace",cfg.getString("cfg_explace", "Block allowed to place: %1%"));
		addMSG ("cfg_exbreak",cfg.getString("cfg_exbreak", "Block allowed to break: %1%"));
		addMSG ("cfg_explosion",cfg.getString("cfg_explosion", "Explosion protection:  %1%"));
		addMSG ("cfg_lavaflow",cfg.getString("cfg_lavaflow", "Protect from lava flow: %1%"));
		addMSG ("cfg_waterflow",cfg.getString("cfg_waterflow", "Protect from water flow: %1%"));
		addMSG ("cfg_psettings",cfg.getString("cfg_psettings", "Player settings: "));
		addMSG ("cfg_peditwand",cfg.getString("cfg_peditwand", "Edit mode: %1% Wand mode: %2%"));
		addMSG ("cmd_wrong",cfg.getString("cmd_wrong", "Something wrong (check command, permissions)"));
		addMSG ("cmd_console",cfg.getString("cmd_console", "Sorry but you can use this command in-game only!"));
		addMSG ("cmd_editmode",cfg.getString("cmd_editmode", "Edit mode is %1%"));
		addMSG ("cmd_wandmode",cfg.getString("cmd_wandmode", "Wand mode is %1%"));
		addMSG ("cmd_configreload",cfg.getString("cmd_configreload", "Configuration reloaded from file:"));
		addMSG ("cmd_showeff",cfg.getString("cmd_showeff", "Effects will played after installing new protector block"));
		addMSG ("cmd_showmsg",cfg.getString("cmd_showmsg", "You will see message after installing new protector block"));
		addMSG ("cmd_crmode_on",cfg.getString("cmd_crmode_on", "Creative mode is works as road-protector edit mode"));
		addMSG ("cmd_crmode_off",cfg.getString("cmd_crmode_off", "Creative mode is no longer road-protector edit mode"));
		addMSG ("cmd_prtswitchlist",cfg.getString("cmd_prtswitchlist", "Protected switch-block list is set to: %1%"));
		addMSG ("cmd_allowplace",cfg.getString("cmd_allowplace", "Blocks allowed to place: %1%"));
		addMSG ("cmd_allowbreak",cfg.getString("cmd_allowbreak", "Blocks allowed to break: %1%"));
		addMSG ("hlp_help",cfg.getString("hlp_help", "Help"));
		addMSG ("hlp_helpexec",cfg.getString("hlp_helpexec", "%1% - execute command "));
		addMSG ("hlp_helpcmdlist",cfg.getString("hlp_helpcmdlist", "%1% - to get additional help"));
		addMSG ("hlp_commands",cfg.getString("hlp_commands", "Commands:"));
		addMSG ("cmd_prtwidth",cfg.getString("cmd_prtwidth", "Protection zone's width radius is set to %1%"));
		addMSG ("cmd_prtwidthwrong",cfg.getString("cmd_prtwidthwrong", "try /rp w [width-radius], where width-radius is integer "));
		addMSG ("cmd_prtheight",cfg.getString("cmd_prtheight", "Protection zone's height is set to %1%"));
		addMSG ("cmd_prtheightwrong",cfg.getString("cmd_prtheightwrong", "try /rp h [height], where height is integer"));
		addMSG ("cmd_prtdepth",cfg.getString("cmd_prtdepth", "Protection zone's depth is set to %1%"));
		addMSG ("cmd_prtdepthwrong",cfg.getString("cmd_prtdepthwrong", "try /rp d [depth], where depth is integer"));
		addMSG ("cmd_rpwand",cfg.getString("cmd_rpwand", "Road protector wand is set to %1%"));
		addMSG ("cmd_rpwanddefault",cfg.getString("cmd_rpwanddefault", "Road protector wand is set to default - %1%"));
		addMSG ("cmd_prtblock",cfg.getString("cmd_prtblock", "Protector-block is set to %1%"));
		addMSG ("cmd_prtblockdefault",cfg.getString("cmd_prtblockdefault", "Protector-block is set to default - %1%"));
		addMSG ("cmd_unknown",cfg.getString("cmd_unknown", "Unknown command: %1%"));
		addMSG ("msg_prtinstall",cfg.getString("msg_prtinstall", "Road protector installed at %1%"));
		addMSG ("msg_oudated",cfg.getString("msg_outdated", "%1% is outdated!"));
		addMSG ("msg_pleasedownload",cfg.getString("msg_pleasedownload", "Please download new version (%1%) from "));
	}
	
	public void addMSG(String key, String txt){
		msg.put(key, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', txt)));
		if (msglist.isEmpty()) msglist=key;
		else msglist=msglist+","+key;
	}

	
	///// процедура для формирования файла
	public void SaveMSG(){
		String lngfile = this.language+".lng";
		String [] keys = msglist.split(",");
		try {
			File f = new File (plg.getDataFolder()+File.separator+lngfile);
			if (!f.exists()) f.createNewFile();
			YamlConfiguration cfg = new YamlConfiguration();
			for (int i = 0; i<keys.length;i++)
				cfg.set(keys[i], msg.get(keys[i]));
			cfg.save(f);
		} catch (Exception e){
			e.printStackTrace();
		}
	} 
	
	public String MSG(String id){
		return MSG (id,"",this.c1, this.c2);
	}
	
	public String MSG(String id, char c){
		return MSG (id,"",c, this.c2);
	}

	public String MSG(String id, String keys){
		return MSG (id,keys,this.c1, this.c2);
	}

	public String MSG(String id, String keys, char c){
		return MSG (id,keys,this.c1, c);
	}

	public String MSG(String id, String keys, char c1, char c2){
		String str = "&4Unknown message ("+id+")";
		if (msg.containsKey(id)){
			str = "&"+c1+msg.get(id);
			String ln[] = keys.split(";");
			if (ln.length>0)
				for (int i = 0; i<ln.length;i++)
					str = str.replace("%"+Integer.toString(i+1)+"%", "&"+c2+ln[i]+"&"+c1);
		} 
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public void PrintMSG (Player p, String msg_key, String keys){
		p.sendMessage(MSG (msg_key, keys, this.c1, this.c2));
	}

	public void PrintMSG (Player p, String msg_key, String keys, char c1, char c2){
		p.sendMessage(MSG (msg_key, keys, c1, c2));
	}

	public void PrintMSG (Player p, String msg_key, char c2){
		p.sendMessage(MSG (msg_key, "", c2));
	}

	public void PrintMSG (Player p, String msg_key){
		p.sendMessage(MSG (msg_key));
	}
	
	public void PrintHLP (Player p){
		PrintMsg(p, "&6&l"+version_name+" v"+plg.des.getVersion()+" &r&6| "+MSG("hlp_help",'6'));
		PrintMSG(p, "hlp_helpcmd","/rp help");
		PrintMSG(p, "hlp_helpexec","/rp <"+MSG("hlp_cmdparam_command",'2')+"> ["+MSG("hlp_cmdparam_parameter",'2')+"]");
		PrintMSG(p, "hlp_helpcmdlist","/rp help <"+MSG("hlp_cmdparam_command",'2'));
		PrintMsg(p, MSG("hlp_commands")+" &2"+getCmdList());
		
	}
	
	public void PrintHLP (Player p, String cmd){
		if (cmds.containsKey(cmd)){
			PrintMsg(p, "&6&l"+version_name+" v"+plg.des.getVersion()+" &r&6| "+MSG("hlp_help",'6'));
			PrintMsg(p, cmds.get(cmd).desc);
		} else PrintMSG(p,"cmd_unknown",cmd);
	}
	
	public String EnDis (boolean b){
		if (b) return MSG ("enabled");//ChatColor.DARK_GREEN+"enabled";
		else return MSG ("disabled");//ChatColor.RED+"disabled";
	}

	public void PrintEnDis (Player p, String msg_id, boolean b){
		p.sendMessage(MSG (msg_id)+": "+EnDis(b));
	}
}
