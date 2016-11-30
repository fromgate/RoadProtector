package me.fromgate.roadprotector;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

/*
 * permissions:
 * roadprotector.edit
 * roadprotector.config
 * roadprotector.wand
 * roadprotector.speedway
 * roadprotector.walk
 *
 */

/*
 * TODO
 * - запрет распространения огня
 * 
 * 
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
 * v0.0.7
 * - разлив воды и лавы
 * - перевод
 * - метрики
 * - проверка версий
 * 
 * v0.0.8 
 * - рельсы (закапывается глубже ;))
 * 
 * v0.9.0
 * - Спидевеи!!!
 * 
 * v0.1.0
 * - Оптимизация процесса проверки версий и вообще использование FGUtilCore
 * - Убирание протекторов
 * - Walk-режим
 * 
 * v0.1.1
 * - возможность убрать префикс в сообщении (надо какой-то извращенный способ ;))
 * - поддержка data в списке для спидвеев
 * 
 * v0.1.2
 * - Now you can skip data values in blocklists. For example you can use "44", but not "44:0,44:1,44:2...."
 * - Now you can use material names in blocklists. For example: "GRAVEL,SLAB:2" 
 * 
 * v0.1.3
 * - очистка дорог от снега
 * 
 * TODO
 * - кэш
 * 
 */


public class RoadProtector extends JavaPlugin {
    protected final Logger log = Logger.getLogger("Minecraft");
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
    String language = "english"; //меняется только из конфига
    boolean language_save = false;
    boolean version_check = true;
    int unprotector = 3; //dirt
    boolean walkroad = true;
    String chocolate = "";


    //Speedways
    boolean speedway = false;
    int speed = 0;
    String speedblocks = "44,43"; //13 - гравий

    boolean spotion = false;

    boolean snowblower = true;


    //прочие переменные
    String rails = "27,28,66";
    String ignorewalk = "0,8,9,10,11,6,29,31,33,34,37,38,39,40,50,54,55,59,64,65,66,69,70,71,72,75,76,77,83,84,93,94,130,131,132";

    protected String prtmsg = "";      // если пусто - используется сообщения из
    protected String prtclickmsg = ""; // файла перевода


    HashMap<String, Boolean> editmode = new HashMap<String, Boolean>();
    HashMap<String, Boolean> wandmode = new HashMap<String, Boolean>();
    HashMap<String, Boolean> walkmode = new HashMap<String, Boolean>();
    HashMap<String, Location> walkprevious = new HashMap<String, Location>();


    RPListener listener; //= new RPListener (this);
    RPCommands commander;// = new RPCommands (this);
    RPUtil u;


    @Override
    public void onEnable() {

        des = getDescription();
        log.config("Road Protector v" + des.getVersion() + " enabled");
        config = this.getConfig();
        LoadCfg();
        SaveCfg();

        u = new RPUtil(this, version_check, language_save, language, "road-protector", "Road Protector", "rp", "&3[RP] &f");

        listener = new RPListener(this);
        commander = new RPCommands(this);
        getCommand("rp").setExecutor(commander);

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(this.listener, this);

        if (prtmsg.isEmpty()) prtmsg = ChatColor.stripColor(u.getMSG("msg_warn_build"));
        if (prtclickmsg.isEmpty()) prtclickmsg = ChatColor.stripColor(u.getMSG("msg_warn_switch"));


        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            u.log("Failed to submit stats to the Metrics (mcstats.org)");
        }

    }


    protected void LoadCfg() {
        dxz = getConfig().getInt("roadprotector.width-radius", 2);
        ydwn = getConfig().getInt("roadprotector.depth", 2);
        yup = getConfig().getInt("roadprotector.height", 4);
        protector = getConfig().getInt("roadprotector.protector-block", 7);
        rpwand = getConfig().getInt("roadprotector.rp-wand", 337);
        crmedit = getConfig().getBoolean("roadprotector.creative-as-edit", true);
        effect = getConfig().getBoolean("roadprotector.effect.show", true);
        efftype = getConfig().getInt("roadprotector.effect.type", 0);
        if (efftype > 3) efftype = 0;
        prtmsg = getConfig().getString("roadprotector.message.breakplace", "");
        prtclickmsg = getConfig().getString("roadprotector.message.switch", "");
        switchprt = getConfig().getString("roadprotector.protected-switch-list", "54,61,62,64,69,77,96,84,107,23");
        exclusion_break = getConfig().getString("roadprotector.exclusion.break", "59,31");
        exclusion_place = getConfig().getString("roadprotector.exclusion.place", "60,59");
        explosion_protect = getConfig().getBoolean("roadprotector.explosion-protection", true);
        lavaprotect = getConfig().getBoolean("roadprotector.lava-flow-protection", true);
        waterprotect = getConfig().getBoolean("roadprotector.water-flow-protection", true);
        speedway = getConfig().getBoolean("roadprotector.speedways.enable", true);
        speedblocks = getConfig().getString("roadprotector.speedways.speed-blocks", "43,44");
        speed = getConfig().getInt("roadprotector.speedways.speed", 0);
        language = getConfig().getString("roadprotector.language", "english");
        language_save = getConfig().getBoolean("roadprotector.language-save", false);
        version_check = getConfig().getBoolean("roadprotector.version-check", true);
        unprotector = getConfig().getInt("roadprotector.unprotector-block", 3);
        walkroad = getConfig().getBoolean("roadprotector.walkmode-road-only", true);
        snowblower = getConfig().getBoolean("roadprotector.snow-form-prevention", true);
        chocolate = getConfig().getString("roadprotector.best-kind-of-donation", "Type here answer for remove [RP] prefix :)");
    }

    protected void SaveCfg() {
        config.set("roadprotector.width-radius", dxz);
        config.set("roadprotector.depth", ydwn);
        config.set("roadprotector.height", yup);
        config.set("roadprotector.protector-block", protector);
        config.set("roadprotector.rp-wand", rpwand);
        config.set("roadprotector.creative-as-edit", crmedit);
        config.set("roadprotector.effect.show", effect);
        config.set("roadprotector.effect.type", efftype);
        config.set("roadprotector.message.breakplace", prtmsg);
        config.set("roadprotector.message.switch", prtclickmsg);
        config.set("roadprotector.protected-switch-list", switchprt);
        config.set("roadprotector.explosion-protection", explosion_protect);
        config.set("roadprotector.exclusion.break", exclusion_break);
        config.set("roadprotector.exclusion.place", exclusion_place);
        config.set("roadprotector.lava-flow-protection", lavaprotect);
        config.set("roadprotector.water-flow-protection", waterprotect);
        config.set("roadprotector.snow-form-prevention", snowblower);
        config.set("roadprotector.speedways.enable", speedway);
        config.set("roadprotector.speedways.speed-blocks", speedblocks);
        config.set("roadprotector.speedways.speed", speed);
        config.set("roadprotector.language", language);
        config.set("roadprotector.version-check", version_check);
        config.set("roadprotector.unprotector-block", unprotector);
        config.set("roadprotector.walkmode-road-only", walkroad);
        config.set("roadprotector.best-kind-of-donation", chocolate);
        saveConfig();
    }


    protected boolean EditMode(Player player) {
        if (crmedit)
            if (player.getGameMode() == GameMode.CREATIVE) return true;

        if (editmode.containsKey(player.getName()))
            if (editmode.get(player.getName())) return true;

        return false;
    }

    protected boolean WalkMode(Player p) {
        return (walkmode.containsKey(p.getName()) && walkmode.get(p.getName()));
    }


    protected void ShowEffect(Location loc) {
        Effect eff = Effect.SMOKE;
        if (efftype == 1) eff = Effect.MOBSPAWNER_FLAMES;
        else if (efftype == 2) eff = Effect.ENDER_SIGNAL;
        else if (efftype == 3) eff = Effect.CLICK1;
        loc.getWorld().playEffect(loc, eff, 4);

    }

    protected String Eff2Str(int tp) {
        String str = "unknown";
        int eftp = tp;
        if (eftp < 0) eftp = efftype;
        if (eftp == 0) str = "smoke";
        else if (eftp == 1) str = "flames";
        else if (eftp == 2) str = "ender signal";
        else if (eftp == 3) str = "click sound";
        return str;
    }

    protected void PrintCfg(Player p) {
        u.printMsg(p, "&6&lRoad Protector v" + des.getVersion() + " &r&6| " + u.getMSG("cfg_configuration", '6'));
        u.printMSG(p, "cfg_prtwand", protector, rpwand, unprotector);
        u.printMSG(p, "cfg_prtarea", Integer.toString(dxz) + " / " + Integer.toString(yup) + " / " + Integer.toString(ydwn));
        u.printMSG(p, "cfg_effects", u.EnDis(effect), Eff2Str(efftype));
        u.printMSG(p, "cfg_crmode", crmedit);
        u.printMSG(p, "cmd_walkroadmode", walkroad);
        u.printMSG(p, "cfg_switchprt", switchprt);
        u.printMSG(p, "cfg_explace", exclusion_place);
        u.printMSG(p, "cfg_exbreak", exclusion_break);
        u.printMSG(p, "cfg_explosion", explosion_protect);
        u.printMSG(p, "cfg_snowblower", snowblower);
        u.printMSG(p, "cfg_lavaflow", lavaprotect);
        u.printMSG(p, "cfg_waterflow", waterprotect);
        u.printMSG(p, "cfg_speedways", u.EnDis(speedway), speed, speedblocks);
        u.printMSG(p, "cfg_psettings", '6');
        u.printMSG(p, "cfg_peditwand", u.EnDis(EditMode(p)), u.EnDis(wandmode.get(p.getName())), u.EnDis(walkmode.get(p.getName())));
    }


    @SuppressWarnings("deprecation")
    protected void protectWalking(Player p) {
        if (WalkMode(p) && p.hasPermission("roadprotector.walk") &&
                (!u.isIdInList(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getTypeId(), ignorewalk))) {
            Block b = p.getLocation().getBlock();
            if (b.getType() != Material.STEP) b = b.getRelative(BlockFace.DOWN);
            b = b.getRelative(BlockFace.DOWN);
            if (walkroad && (!isPlayerOnRoad(p))) return;
            if (checkDistance(p)) {
                b.setTypeId(protector);
                if (effect) ShowEffect(p.getLocation());
            }
        }
    }

    protected boolean checkDistance(Player p) {
        String pn = p.getName();
        if (walkprevious.containsKey(pn) &&
                (walkprevious.get(pn).getWorld() == p.getWorld()) &&
                (walkprevious.get(pn).distance(p.getLocation()) < 2)) return false;

        walkprevious.put(pn, p.getLocation());
        return true;

    }

    @SuppressWarnings("deprecation")
    protected int unProtect(Block b, int r, int yu, int yd, int unprotector) {
        int count = 0;
        int miny = 0;
        if (protector == 7) miny = 5;
        World w = b.getWorld();
        for (int dy = Math.max(miny, b.getY() - yu); dy <= Math.min(b.getY() + yd, b.getWorld().getMaxHeight() - 1); dy++)
            for (int dx = b.getX() - r; dx <= b.getX() + r; dx++)
                for (int dz = b.getZ() - r; dz <= b.getZ() + r; dz++) {
                    Block tb = w.getBlockAt(dx, dy, dz);
                    if (tb.getTypeId() == protector) {
                        count++;
                        tb.setTypeIdAndData(unprotector, (byte) 0, false);
                    }
                }
        return count;
    }

    protected void printRpMsg(Player p, String str) {
        if (chocolate.equalsIgnoreCase("chocolate")) u.printMsg(p, "&c" + str);
        else u.printPxMsg(p, "&c" + str);
    }

	
	/*
	 * API
	 * 
	 */

    /*
     * Remove protection around defined block in area specified by radius
     * Protector will be replaced with "unprotector" defined in config
     */
    public int unProtect(Block b, int r) {
        return unProtect(b, r, r, r, unprotector);
    }

    /*
     * Remove protection around defined block in area specified by radius
     * Protector will be replaced with defined "unprotector"
     */
    public int unProtect(Block b, int r, int unprotector) {
        return unProtect(b, r, r, r, unprotector);
    }

    /*
     * Remove all protectors that could affect to defined block
     * (area specified by protection field size, defined in config)
     * Protector will be replaced with "unprotector" defined in config
     */
    public int unProtect(Block b) {
        return unProtect(b, dxz, yup, ydwn, unprotector);
    }


    /*
     * Check is defined location protected by the RoadProtector
     * returns "true" if location is protected
     */
    public boolean isProtected(Location loc) {
        return isProtected(loc.getBlock());
    }

    /*
     * Check is defined block protected by the RoadProtector
     * returns "true" if block is protected
     */
    @SuppressWarnings("deprecation")
    public boolean isProtected(Block b) {
        int miny = 0;
        if (protector == 7) miny = 5;
        World w = b.getWorld();
        for (int dy = Math.max(miny, b.getY() - yup); dy <= Math.min(b.getY() + ydwn, b.getWorld().getMaxHeight() - 1); dy++)
            for (int dx = b.getX() - dxz; dx <= b.getX() + dxz; dx++)
                for (int dz = b.getZ() - dxz; dz <= b.getZ() + dxz; dz++)
                    if (w.getBlockAt(dx, dy, dz).getTypeId() == protector) return true;
        return false;
    }

    /*
     * Check is player is above the "speed-blocks"
     * returns "true" he walk on the road.
     * This methods did not check protection on player location
     */
    @SuppressWarnings("deprecation")
    public boolean isPlayerOnRoad(Player p) {
        Block b = p.getLocation().getBlock();
        if (b.getType() != Material.STEP) b = b.getRelative(BlockFace.DOWN);
        return (u.isItemInList(b.getTypeId(), b.getData(), speedblocks));
    }

    /*
     * Check is block is located above the "speed-blocks"
     * This methods did not check block protection state
     */
    @SuppressWarnings("deprecation")
    public boolean isBlockAboveRoad(Block block) {
        Block b = block.getRelative(BlockFace.DOWN);
        return (u.isItemInList(b.getTypeId(), b.getData(), speedblocks));
    }

}




