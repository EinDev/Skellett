package com.gmail.thelimeglass.Expressions;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.eclipse.jdt.annotation.Nullable;

import com.gmail.thelimeglass.Utils.Annotations.Config;
import com.gmail.thelimeglass.Utils.Annotations.PropertyType;
import com.gmail.thelimeglass.Utils.Annotations.Syntax;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@Syntax("click[ed] raw slot")
@Config("ClickedRawSlot")
@PropertyType(ExpressionType.SIMPLE)
public class ExprClickedRawSlot extends SimpleExpression<Number> {
	
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}
	public boolean isSingle() {
		return true;
	}
	public boolean init(Expression<?>[] args, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
		if (!getParser().isCurrentEvent(InventoryClickEvent.class)) {
			Skript.error("You can not use clicked raw slot expression in any event but inventory click!");
			return false;
		}
		return true;
	}
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "clicked raw slot";
	}
	@Nullable
	protected Number[] get(Event e) {
		return new Number[]{((InventoryClickEvent)e).getRawSlot()};
	}
}