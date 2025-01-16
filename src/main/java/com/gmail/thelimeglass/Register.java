package com.gmail.thelimeglass;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import com.gmail.thelimeglass.Expressions.*;
import com.gmail.thelimeglass.Utils.EnumClassInfo;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Register {

	@SuppressWarnings("rawtypes")
	private static List<Class> classes = new ArrayList<>();

	public static void events() {
		if (Skellett.syntaxToggleData.getBoolean("Main.PrepareEnchant")) {
			Skript.registerEvent(
					"[on] ([item] enchant prepare|prepare [item] enchant):",
					SimpleEvent.class,
					PrepareItemEnchantEvent.class,
					"[on] ([item] enchant prepare|prepare [item] enchant)"
			);
			EventValues.registerEventValue(
					PrepareItemEnchantEvent.class,
					Block.class,
					PrepareItemEnchantEvent::getEnchantBlock,
					0
			);
			EventValues.registerEventValue(
					PrepareItemEnchantEvent.class,
					Player.class,
					PrepareItemEnchantEvent::getEnchanter,
					0
			);
			EventValues.registerEventValue(
					PrepareItemEnchantEvent.class,
					Number.class,
					PrepareItemEnchantEvent::getEnchantmentBonus,
					0
			);
			EventValues.registerEventValue(
					PrepareItemEnchantEvent.class,
					ItemStack.class,
					PrepareItemEnchantEvent::getItem,
					0
			);
		}
		if (Skellett.syntaxToggleData.getBoolean("Main.Maps")) {
			Skript.registerEvent("[on] [player] map [(initialize|open)]:", SimpleEvent.class, MapInitializeEvent.class, "[on] [player] map [(initialize|open)]");
		}
		if (Skellett.syntaxToggleData.getBoolean("Main.Brewing")) {
			Skript.registerEvent("[on] [skellett] brew[ing]:", SimpleEvent.class, BrewEvent.class, "[on] [skellett] brew[ing]");
			EventValues.registerEventValue(BrewEvent.class, Number.class, BrewEvent::getFuelLevel, 0);
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.MultiBlockPlace")) {
			registerEvent(BlockMultiPlaceEvent.class, "(multi[ple]|double)[ ][block][ ]place");
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.ArrowPickup")) {
			if (Bukkit.getVersion().contains("1.7")) {
				Bukkit.getConsoleSender().sendMessage(Skellett.cc(Skellett.prefix + "The arrow pickup event is only for 1.8+ versions!"));
			} else {
				registerEvent(PlayerPickupArrowEvent.class, "arrow pickup");
				EventValues.registerEventValue(PlayerPickupArrowEvent.class, AbstractArrow.class, PlayerPickupArrowEvent::getArrow, 0);
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.OffhandSwitch")) {
			if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8")) {
				Bukkit.getConsoleSender().sendMessage(Skellett.cc(Skellett.prefix + "The offhand switch event is only for 1.9+ versions!"));
			} else {
				registerEvent(PlayerSwapHandItemsEvent.class, "off[ ]hand (switch|move)");
				EventValues.registerEventValue(PlayerSwapHandItemsEvent.class, ItemStack.class, PlayerSwapHandItemsEvent::getMainHandItem, 0);
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.CreativeInventoryClick")) {
			registerEvent(InventoryCreativeEvent.class, "creative inventory click");
			EventValues.registerEventValue(InventoryCreativeEvent.class, ItemStack.class, InventoryCreativeEvent::getCursor, 0);
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.EntityTeleport")) {
			registerEvent(EntityTeleportEvent.class, "entity teleport");
			EventValues.registerEventValue(EntityTeleportEvent.class, Location.class, EntityTeleportEvent::getTo, 0);
			EventValues.registerEventValue(EntityTeleportEvent.class, Location.class, EntityTeleportEvent::getFrom, -1);
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.VehicleMove")) {
			registerEvent(VehicleMoveEvent.class, "(vehicle|minecart|boat) move");
			EventValues.registerEventValue(VehicleMoveEvent.class, Location.class, VehicleMoveEvent::getFrom, -1);
			EventValues.registerEventValue(VehicleMoveEvent.class, Location.class, VehicleMoveEvent::getTo, 1);
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.EntityBlockChange")) {
			registerEvent(EntityChangeBlockEvent.class, "entity block (change|modify)");
			EventValues.registerEventValue(EntityChangeBlockEvent.class, Block.class, EntityChangeBlockEvent::getBlock, 0);
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.Breeding")) {
			if (Bukkit.getVersion().contains("1.6") || Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")) {
				Bukkit.getConsoleSender().sendMessage(Skellett.cc(Skellett.prefix + "The breeding event is only for 1.10+ versions!"));
			} else {
				registerEvent(EntityBreedEvent.class, "bre[e]d[ing]");
				Skript.registerExpression(ExprBreedingItem.class, ItemStack.class, ExpressionType.SIMPLE, "bre[e]d[ing] (item|material) [used]");
				Skript.registerExpression(ExprBreedingBreeder.class, LivingEntity.class, ExpressionType.SIMPLE, "breeder");
				Skript.registerExpression(ExprBreedingEntity.class, LivingEntity.class, ExpressionType.SIMPLE, "[final] bre[e]d[ed] entity");
				Skript.registerExpression(ExprBreedingXP.class, Number.class, ExpressionType.SIMPLE, "bre[e]d[ing] (xp|experience)");
				Skript.registerExpression(ExprBreedingFather.class, LivingEntity.class, ExpressionType.SIMPLE, "bre[e]d[ing] father");
				Skript.registerExpression(ExprBreedingMother.class, LivingEntity.class, ExpressionType.SIMPLE, "bre[e]d[ing] mother");
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.EntityUnleash")) {
			registerEvent(EntityUnleashEvent.class, "[entity] (un(leash|lead)|(leash|lead) break)");
			EventValues.registerEventValue(EntityUnleashEvent.class, Block.class, (e) -> {
				if (e.getEntity().isDead()) {
						return null;
					}
					return ((LivingEntity) e.getEntity()).getLeashHolder().getLocation().getBlock();
			}, 0);
			EnumClassInfo.create(EntityUnleashEvent.UnleashReason.class, "unleashreason").register();
			Skript.registerExpression(ExprUnleashReason.class, EntityUnleashEvent.UnleashReason.class, ExpressionType.SIMPLE, "(un(leash|lead)|(leash|lead) break) reason");
			Skript.registerExpression(ExprUnleashHitch.class, Entity.class, ExpressionType.SIMPLE, "event-hitch");
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.Elytra")) {
			if (!Bukkit.getServer().getVersion().contains("MC: 1.6") && !Bukkit.getServer().getVersion().contains("MC: 1.7") && !Bukkit.getServer().getVersion().contains("MC: 1.8") && !Bukkit.getServer().getVersion().contains("MC: 1.9")) {
				registerEvent(EntityToggleGlideEvent.class, "[entity] (elytra|glide) [toggle]");
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.BrewingFuel")) {
			if (!Bukkit.getServer().getVersion().contains("MC: 1.6") && !Bukkit.getServer().getVersion().contains("MC: 1.7") && !Bukkit.getServer().getVersion().contains("MC: 1.8") && !Bukkit.getServer().getVersion().contains("MC: 1.9") && !Bukkit.getServer().getVersion().contains("MC: 1.10") && !Bukkit.getServer().getVersion().contains("MC: 1.11)") && !Bukkit.getServer().getVersion().contains("MC: 1.11.1")) {
				registerEvent(BrewingStandFuelEvent.class, "brew[ing] [stand] fuel [increase]");
				EventValues.registerEventValue(BrewingStandFuelEvent.class, Number.class, BrewingStandFuelEvent::getFuelPower, 0);
				EventValues.registerEventValue(BrewingStandFuelEvent.class, ItemStack.class, BrewingStandFuelEvent::getFuel, 0);
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.AnvilPrepare")) {
			if (!Bukkit.getServer().getVersion().contains("MC: 1.6") && !Bukkit.getServer().getVersion().contains("MC: 1.7") && !Bukkit.getServer().getVersion().contains("MC: 1.8")) {
				registerEvent(PrepareAnvilEvent.class, "([item] anvil prepare|prepare [item] anvil)");
				Classes.registerClass(new ClassInfo<AnvilInventory>(AnvilInventory.class, "anvilinventory")
					.name("anvilinventory")
					.description("A getter for AnvilInventory")
					.parser(new Parser<AnvilInventory>() {
						@Override
						@Nullable
						public AnvilInventory parse(String obj, ParseContext context) {
							return null;
						}
						@Override
						public String toString(AnvilInventory e, int flags) {
							return e.toString();
						}
						@Override
						public String toVariableNameString(AnvilInventory b) {
							return b.toString();
						}
				}));
				EventValues.registerEventValue(PrepareAnvilEvent.class, ItemStack.class, PrepareInventoryResultEvent::getResult, 0);
				EventValues.registerEventValue(PrepareAnvilEvent.class, Number.class, (e) -> e.getInventory().getRepairCost(), 0);
				EventValues.registerEventValue(PrepareAnvilEvent.class, String.class, (e) -> e.getInventory().getRenameText(), 0);
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.SlimeSplit")) {
			registerEvent(SlimeSplitEvent.class, "slime split");
		}
		if (Skellett.getInstance().getConfig().getBoolean("PluginHooks.Npc")) {
			registerEvent(NPCDamageByEntityEvent.class, "(npc|citizen) damage (by|from) [a[n]] entity");
			EventValues.registerEventValue(NPCDamageByEntityEvent.class, Entity.class, NPCDamageByEntityEvent::getDamager, 0);
			EventValues.registerEventValue(NPCDamageByEntityEvent.class, String.class, (e) -> e.getCause().toString(), 0);
			registerEvent(NPCDamageByBlockEvent.class, "(npc|citizen) damage (by|from) [a] block");
			EventValues.registerEventValue(NPCDamageByBlockEvent.class, Block.class, NPCDamageByBlockEvent::getDamager, 0);
			registerEvent(EntityTargetNPCEvent.class, "entity target (npc|citizen)");
			EventValues.registerEventValue(EntityTargetNPCEvent.class, Entity.class, EntityTargetNPCEvent::getEntity, 0);
			registerEvent(NPCClickEvent.class, "(npc|citizen) click");
			EventValues.registerEventValue(NPCClickEvent.class, Player.class, NPCClickEvent::getClicker, 0);
			registerEvent(NPCLeftClickEvent.class, "(npc|citizen) left[(-| )]click");
			EventValues.registerEventValue(NPCLeftClickEvent.class, Player.class, NPCLeftClickEvent::getClicker, 0);
			registerEvent(NPCRightClickEvent.class, "(npc|citizen) right[(-| )]click");
			EventValues.registerEventValue(NPCRightClickEvent.class, Player.class, NPCRightClickEvent::getClicker, 0);
			registerEvent(NPCCollisionEvent.class, "(npc|citizen) [entity] colli(sion|de)");
			EventValues.registerEventValue(NPCCollisionEvent.class, Entity.class, NPCCollisionEvent::getCollidedWith, 0);
			registerEvent(NPCCombustByBlockEvent.class, "(npc|citizen) (combust[ion]|ignition) (by|from) [a] block");
			EventValues.registerEventValue(NPCCombustByBlockEvent.class, Block.class, NPCCombustByBlockEvent::getCombuster, 0);
			registerEvent(NPCCombustByBlockEvent.class, "(npc|citizen) (combust[ion]|ignition) (by|from) [a[n]] entity");
			EventValues.registerEventValue(NPCCombustByEntityEvent.class, Entity.class, NPCCombustByEntityEvent::getCombuster, 0);
			registerEvent(NPCCreateEvent.class, "(npc|citizen) create");
			registerEvent(NPCDamageEntityEvent.class, "(npc|citizen) [entity] damage");
			EventValues.registerEventValue(NPCDamageEntityEvent.class, Entity.class, NPCDamageEntityEvent::getDamaged, 0);
			//TODO Death: Dropped items and XP
			registerEvent(NPCDeathEvent.class, "(npc|citizen) death");
			registerEvent(NPCDespawnEvent.class, "(npc|citizen) despawn");
			Boolean enderTp = true;
			try {
				Class.forName("net/citizensnpcs/api/event/NPCEnderTeleportEvent");
			} catch( ClassNotFoundException e ) {
				enderTp = false;
			}
			//TODO: Return and settable vector
			registerEvent(NPCPushEvent.class, "(npc|citizen) (push|(vector|velocity) change)");
			registerEvent(NPCRemoveEvent.class, "(npc|citizen) remove");
			//TODO: Trait events
			registerEvent(NPCSelectEvent.class, "(npc|citizen) sel[ect[ed]]");
			EventValues.registerEventValue(NPCSelectEvent.class, Player.class, (e) -> {
					if (Bukkit.getPlayer(e.getSelector().getName()) != null) {
						return Bukkit.getPlayer(e.getSelector().getName());
					}
					return null;
			}, 0);
			registerEvent(NPCSpawnEvent.class, "(npc|citizen) spawn");
			EventValues.registerEventValue(NPCSpawnEvent.class, Location.class, NPCSpawnEvent::getLocation, 0);
			Boolean npcTp = true;
			try {
				Class.forName("net/citizensnpcs/api/event/NPCTeleportEvent");
			} catch( ClassNotFoundException e ) {
				npcTp = false;
			}
			if (npcTp) {
				//TODO: Get from and to location
				registerEvent(NPCTeleportEvent.class, "(npc|citizen) teleport");
				EventValues.registerEventValue(NPCTeleportEvent.class, Location.class, NPCTeleportEvent::getTo, 0);
			}
			registerEvent(PlayerCreateNPCEvent.class, "player create (npc|citizen)");
			EventValues.registerEventValue(PlayerCreateNPCEvent.class, Player.class, PlayerCreateNPCEvent::getCreator, 0);
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.SpawnerSpawn")) {
			if (!Bukkit.getServer().getVersion().contains("MC: 1.8")) {
				registerEvent(SpawnerSpawnEvent.class, "spawner spawn");
				EventValues.registerEventValue(SpawnerSpawnEvent.class, Block.class,
						(e) -> e.getSpawner().getBlock(), 0);
			}
		}
		if (Skellett.syntaxToggleData.getBoolean("Syntax.Events.Hanging")) {
			registerEvent(HangingBreakEvent.class, "(unhang|[entity] unhung)");
			EventValues.registerEventValue(HangingBreakEvent.class, Entity.class, HangingBreakEvent::getEntity, 0);
			registerEvent(HangingPlaceEvent.class, "(hang|[entity] hung)");
			EventValues.registerEventValue(HangingPlaceEvent.class, Entity.class, HangingPlaceEvent::getEntity, 0);
			EventValues.registerEventValue(HangingPlaceEvent.class, Player.class, HangingPlaceEvent::getPlayer, 0);
			EventValues.registerEventValue(HangingPlaceEvent.class, Block.class, HangingPlaceEvent::getBlock, 0);
			registerEvent(HangingBreakByEntityEvent.class, "([un]hang[ing]|[un]hung) [entity] (remove|break|destroy|damage)");
			EventValues.registerEventValue(HangingBreakByEntityEvent.class, Entity.class, HangingBreakByEntityEvent::getRemover, 0);
			EventValues.registerEventValue(HangingBreakByEntityEvent.class, Entity.class, HangingBreakByEntityEvent::getEntity, 1);
		}
		//TODO: registerEvent(PlayerInteractEntityEvent.class, "item[ ]frame rotate");
	}
	public static void types() {
		if (Skellett.syntaxToggleData.getBoolean("Main.Scoreboards")) {
			if (Classes.getExactClassInfo(Objective.class) == null)
				Classes.registerClass(new ClassInfo<Objective>(Objective.class, "objective")
					.name("scoreboard objective")
					.description("A getter for scoreboard objectives.")
					.parser(new Parser<Objective>() {
						@Override
						@Nullable
						public Objective parse(String obj, ParseContext context) {
							return null;
						}
						@Override
						public String toString(Objective o, int flags) {
							return o.toString();
						}
						@Override
						public String toVariableNameString(Objective o) {
							return o.toString();
						}
				}));
			if (Classes.getExactClassInfo(Scoreboard.class) == null)
				Classes.registerClass(new ClassInfo<Scoreboard>(Scoreboard.class, "scoreboard")
					.name("scoreboard")
					.description("A getter for skellett scoreboards.")
					.parser(new Parser<Scoreboard>() {
						@Override
						@Nullable
						public Scoreboard parse(String obj, ParseContext context) {
							return null;
						}
						@Override
						public String toString(Scoreboard s, int flags) {
							return s.toString();
						}
						@Override
						public String toVariableNameString(Scoreboard s) {
							return s.toString();
						}
				}));
			if (Classes.getExactClassInfo(Score.class) == null)
				Classes.registerClass(new ClassInfo<Score>(Score.class, "score")
					.name("scoreboard score")
					.description("A getter for scoreboard scores.")
					.parser(new Parser<Score>() {
						@Override
						@Nullable
						public Score parse(String score, ParseContext context) {
							return null;
						}
						@Override
						public String toString(Score s, int flags) {
							return s.toString();
						}
						@Override
						public String toVariableNameString(Score s) {
							return s.toString();
						}
				}));
			if (Classes.getExactClassInfo(Team.class) == null)
				Classes.registerClass(new ClassInfo<Team>(Team.class, "team")
					.name("scoreboard team")
					.user("(skellettteam|scoreboard ?team)s?")
					.description("A getter for scoreboard teams.")
					.parser(new Parser<Team>() {
						@Override
						@Nullable
						public Team parse(String team, ParseContext context) {
							return null;
						}
						@Override
						public String toString(Team t, int flags) {
							return t.toString();
						}
						@Override
						public String toVariableNameString(Team t) {
							return t.toString();
						}
				}));
			if (!Bukkit.getServer().getVersion().contains("MC: 1.6") && !Bukkit.getServer().getVersion().contains("MC: 1.7") && !Bukkit.getServer().getVersion().contains("MC: 1.8")) {
				EnumClassInfo.create(Team.Option.class, "teamoption").register();
				if (Bukkit.getPluginManager().getPlugin("skRayFall") == null) {
					EnumClassInfo.create(Team.OptionStatus.class, "optionstatus").register();
				}
			}
		}
		if (Skellett.getInstance().getConfig().getBoolean("PluginHooks.Npc")) {
			//EnumClassInfo.create(EntityType.class, "skellettentitytype").register();
			if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
				Classes.registerClass(new ClassInfo<NPC>(NPC.class, "citizen")
					.name("citizens")
					.user("(npc|citizen)s?")
					.description("A getter for npc")
					.defaultExpression(new EventValueExpression<NPC>(NPC.class))
					.parser(new Parser<NPC>() {
						@Override
						@Nullable
						public NPC parse(String obj, ParseContext context) {
							return null;
						}
						@Override
						public String toString(NPC e, int flags) {
							return e.toString();
						}
						@Override
						public String toVariableNameString(NPC e) {
							return e.toString();
						}
				}));
			}
		}
		Classes.registerClass(new ClassInfo<UUID>(UUID.class, "uuid")
			.name("uuid")
			.description("A getter for uuid's")
			.defaultExpression(new EventValueExpression<UUID>(UUID.class))
			.parser(new Parser<UUID>() {
				@Override
				@Nullable
				public UUID parse(String obj, ParseContext context) {
					return null;
				}
				@Override
				public String toString(UUID e, int flags) {
					return e.toString();
				}
				@Override
				public String toVariableNameString(UUID e) {
					return e.toString();
				}
		}));
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Class> getClasses() {
		return classes;
	}
	
	@SuppressWarnings("rawtypes")
	public static void addEvent(Class c) {
		classes.add(c);
	}
	
	@SuppressWarnings("unchecked")
	public static void registerEvent(@SuppressWarnings("rawtypes") Class clazz, String syntax) {
		Skript.registerEvent(syntax, SimpleEvent.class, clazz, "[skellett] " + syntax);
		classes.add(clazz);
	}
	
	public static void dump() {
		classes.clear();
	}

	public static void metrics(Metrics metrics) {
		metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
		metrics.addCustomChart(new Metrics.SimplePie("using_customevents", () -> Skellett.ceData.getBoolean("CustomEvents", false) ? "true" : "false"));
		metrics.addCustomChart(new Metrics.SingleLineChart("player_database", () -> Bukkit.getOfflinePlayers().length));
	}
}