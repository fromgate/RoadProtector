package fromgate.roadprotector;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * permissions:
 * roadprotector.edit
 * roadprotector.config
 * roadprotector.wand
 * roadprotector.speedway
 *
 */

/*
 * ChangeLog:
 * v0.03:
 * 1. Fix протекта на 128-м блоке
 * 2. Оптимизация
 * 3. Добавлены эффекты вместо собщения в чате
 * 4. Добавлены комманды по управлению эффектами, просмотра конфигурации, перезагрузки конфигурации из файла
 * 
 * v0.04:
 * 1. Можно задавать предупреждающее сообщение 
 * 2. Команда /rp cfg
 * 3. Можно включить защиту дверей, сундуков и т.п.
 *  
 * v0.0.5
 * 1. Защита от взрывов 
 *  
 * v0.0.6
 * 1. Списки исключений
 * 
 * v0.0.6/2
 * 1. Исправление ошибок
 * 
 * v0.7.0
 * - разлив воды и лавы
 * - перевод
 * - метрики
 * - проверка версий
 * 
 * v0.8.0 
 * - рельсы (закапывается глубже ;))
 * 
 * v0.9.0
 * - Спидевеи!!!
 * 
 * 
 */


public class RoadProtector extends JavaPlugin{
	public final Logger log = Logger.getLogger("Minecraft");
	//protected String px = "&3[RP] &f";

	FileConfiguration config;
	PluginDescriptionFile des;
	int dxz = 2;
	int yup = 4;
	int ydwn = 2;
	int protector = 7;  // bedrock
	int rpwand = 337;   // clay
	boolean explosion_protect = true;
	String switchprt = "54,61,62,64,69,77,96,84,107,23"; //empty = non
	String exclusion_place = "60,59";
	String exclusion_break = "59,31";
	boolean crmedit = true;
	boolean effect = true;
	int efftype = 0;
	boolean lavaprotect = true;
	boolean waterprotect = true;
	String language="english"; //меняется только из конфига
	boolean language_save = false;
	boolean version_check = true;



	//Speedways
	boolean speedway=false;
	int speed = 0;
	String speedblocks = "44,43"; //13 - гравий


	//прочие переменные
	String rails = "27,28,66";

	protected String prtmsg = "";      // если пусто - используется сообщения из
	protected String prtclickmsg = ""; // файла перевода



	HashMap<String,Boolean> editmode = new HashMap<String,Boolean>();
	HashMap<String,Boolean> wandmode = new HashMap<String,Boolean>();


	RPListener listener; //= new RPListener (this);
	RPCommands commander;// = new RPCommands (this);
	FGUtil u;


	@Override
	public void onDisable() {
		//SaveCfg();
	}

	@Override
	public void onEnable() {

		des = getDescription();
		log.config("Road Protector v"+des.getVersion()+" enabled");
		config = this.getConfig();
		LoadCfg();
		SaveCfg();

		u = new FGUtil(this, version_check, language_save, language);
		listener = new RPListener (this);
		commander = new RPCommands (this);
		getCommand("rp").setExecutor(commander);

		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this.listener, this);

		u.UpdateMsg();
		if (prtmsg.isEmpty()) prtmsg = ChatColor.stripColor(u.MSG("msg_warn_build"));
		if (prtclickmsg.isEmpty()) prtclickmsg = ChatColor.stripColor(u.MSG("msg_warn_switch"));


		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			log.info("[DT] failed to submit stats to the Metrics (mcstats.org)");
		}

	}


	public void LoadCfg(){
		dxz = getConfig().getInt("roadprotector.width-radius", 2);
		ydwn = getConfig().getInt("roadprotector.depth", 2);
		yup= getConfig().getInt("roadprotector.height", 4);
		protector = getConfig().getInt("roadprotector.protector-block", 7);
		rpwand = getConfig().getInt("roadprotector.rp-wand", 337);
		crmedit = getConfig().getBoolean("roadprotector.creative-as-edit", true);
		effect = getConfig().getBoolean("roadprotector.effect.show", true);
		efftype = getConfig().getInt("roadprotector.effect.type", 0);
		if (efftype>3) efftype = 0;
		prtmsg = getConfig().getString("roadprotector.message.breakplace","");
		prtclickmsg = getConfig().getString("roadprotector.message.switch","");
		switchprt = getConfig().getString("roadprotector.protected-switch-list","54,61,62,64,69,77,96,84,107,23");
		exclusion_break = getConfig().getString("roadprotector.exclusion.break","59,31");
		exclusion_place = getConfig().getString("roadprotector.exclusion.place","60,59");
		explosion_protect = getConfig().getBoolean("roadprotector.explosion-protection", true);
		lavaprotect = getConfig().getBoolean("roadprotector.lava-flow-protection", true);
		waterprotect = getConfig().getBoolean("roadprotector.water-flow-protection", true);
		speedway = getConfig().getBoolean("roadprotector.speedways.enable", true);
		speedblocks = getConfig().getString("roadprotector.speedways.speed-blocks", "43,44");
		speed = getConfig().getInt("roadprotector.speedways.speed", 0);
		language = getConfig().getString("roadprotector.language", "english");
		language_save = getConfig().getBoolean("roadprotector.language-save", false);
		version_check = getConfig().getBoolean("roadprotector.version_check", true);
	}

	public void SaveCfg () {
		config.set("roadprotector.width-radius",dxz);
		config.set("roadprotector.depth",ydwn);
		config.set("roadprotector.height", yup);
		config.set("roadprotector.protector-block", protector);
		config.set("roadprotector.rp-wand", rpwand);
		config.set("roadprotector.creative-as-edit", crmedit);
		config.set("roadprotector.effect.show", effect );
		config.set("roadprotector.effect.type", efftype);
		config.set("roadprotector.message.breakplace",prtmsg );
		config.set("roadprotector.message.switch",prtclickmsg);
		config.set("roadprotector.protected-switch-list",switchprt );
		config.set("roadprotector.explosion-protection", explosion_protect);
		config.set("roadprotector.exclusion.break",exclusion_break);
		config.set("roadprotector.exclusion.place",exclusion_place);
		config.set("roadprotector.lava-flow-protection", lavaprotect);
		config.set("roadprotector.water-flow-protection", waterprotect);
		config.set("roadprotector.speedways.enable", speedway);
		config.set("roadprotector.speedways.speed-blocks", speedblocks);
		config.set("roadprotector.speedways.speed", speed);
		config.set("roadprotector.language", language);
		config.set("roadprotector.version_check", version_check);
		saveConfig();

	}


	public boolean EditMode(Player player) {
		if (crmedit)
			if (player.getGameMode()==GameMode.CREATIVE) return true;

		if (editmode.containsKey(player.getName()))
			if	(editmode.get(player.getName())) return true;

		return false;
	}

	public boolean PlaceGuarded (Block b) {
		int miny = 0; 
		if (protector==7) miny=5;
		World w = b.getWorld();
		for (int dy = Math.max(miny, b.getY()-yup); dy<=Math.min(b.getY()+ydwn, b.getWorld().getMaxHeight()-1); dy++)
			for (int dx = b.getX()-dxz; dx<=b.getX()+dxz; dx++)
				for (int dz = b.getZ()-dxz; dz<=b.getZ()+dxz; dz++)
					if (w.getBlockAt(dx, dy, dz).getTypeId()==protector) return true;
		return false;
	}



	public void ShowEffect (Location loc){
		Effect eff = Effect.SMOKE;
		if (efftype == 1) eff = Effect.MOBSPAWNER_FLAMES;
		else if (efftype == 2) eff = Effect.ENDER_SIGNAL;
		else if (efftype == 3) eff = Effect.CLICK1;
		loc.getWorld().playEffect(loc, eff, 4);

	}

	public String Eff2Str (int tp){
		String str ="unknown";
		int eftp = tp;
		if (eftp<0) eftp = efftype;
		if (eftp == 0) str = "smoke";
		else if (eftp == 1) str = "flames";
		else if (eftp == 2) str= "ender signal";
		else if (eftp == 3) str = "click sound";
		return str;
	}

	public void PrintCfg (Player p){
		u.PrintMsg (p, "&6&lRoad Protector v"+des.getVersion()+" &r&6| "+u.MSG("cfg_configuration",'6'));
		u.PrintMSG(p, "cfg_prtwand", Integer.toString(protector)+";"+Integer.toString(rpwand));
		u.PrintMSG(p, "cfg_prtarea", Integer.toString(dxz)+ " / "+Integer.toString(yup)+" / "+ Integer.toString(ydwn));
		u.PrintMSG(p, "cfg_effects", u.EnDis(effect)+";"+Eff2Str(efftype));
		u.PrintEnDis(p, "cfg_crmode", crmedit);
		u.PrintMSG(p, "cfg_switchprt", switchprt);
		u.PrintMSG (p,"cfg_explace",exclusion_place);
		u.PrintMSG (p,"cfg_exbreak",exclusion_break);
		u.PrintEnDis (p,"cfg_explosion",explosion_protect);
		u.PrintEnDis (p,"cfg_lavaflow",lavaprotect);
		u.PrintEnDis (p,"cfg_waterflow",waterprotect);
		u.PrintMSG (p,"cfg_speedways",u.EnDis(speedway)+";"+speed+";"+speedblocks);
		u.PrintMSG(p, "cfg_psettings");
		u.PrintMSG(p, "cfg_peditwand", u.EnDis(EditMode(p))+";"+u.EnDis(wandmode.get(p.getName())));
	}

	public boolean inListId (int id, String str, boolean empty){
		if (str.isEmpty()) return empty;
		String [] ln = str.split(",");
		for (int i = 0; i<ln.length;i++)
			if (Integer.parseInt(ln[i]) == id) return true;
		return false;
	}

}




