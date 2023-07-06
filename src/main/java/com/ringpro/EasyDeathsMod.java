package com.ringpro;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyDeathsMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("easy-deaths");

	@Override
	public void onInitialize() {
		LOGGER.info("Easy Deaths initialized!");
	}

	public static final GameRules.Key<DoubleRule> DEATH_ITEMS_SPREAD_AMOUNT =
			GameRuleRegistry.register("deathItemsSpreadAmount", GameRules.Category.DROPS,
					GameRuleFactory.createDoubleRule(0.1D, 0D, 10D));
	public static final GameRules.Key<GameRules.BooleanRule> DEATH_ITEMS_NEVER_DESPAWN =
			GameRuleRegistry.register("deathItemsNeverDespawn", GameRules.Category.DROPS,
					GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> DEATH_ITEMS_INVULNERABLE =
			GameRuleRegistry.register("deathItemsInvulnerable", GameRules.Category.DROPS,
					GameRuleFactory.createBooleanRule(true));

	public static final GameRules.Key<GameRules.BooleanRule> DEATH_ITEMS_GLOW =
			GameRuleRegistry.register("deathItemsGlow", GameRules.Category.DROPS,
					GameRuleFactory.createBooleanRule(true));
}