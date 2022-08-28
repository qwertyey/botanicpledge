package yerova.botanicpledge.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yerova.botanicpledge.BotanicPledge;
import yerova.botanicpledge.common.items.protectors.GaiaProtector;
import yerova.botanicpledge.common.items.protectors.ManaProtector;
import yerova.botanicpledge.common.items.protectors.TerraProtector;
import yerova.botanicpledge.common.items.protectors.YggdralProtector;
import yerova.botanicpledge.common.items.relic.MariasCore;
import yerova.botanicpledge.common.items.relic.MarinasCore;

public class ItemInit {
    //Custom Rarity
    public static final Rarity UNIQUE = Rarity.create("Unique", ChatFormatting.AQUA);

    //Item Registry
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BotanicPledge.MOD_ID);


    //Protectors
    public static final RegistryObject<Item> MANA_PROTECTOR = ITEMS.register("mana_protector", () -> new ManaProtector(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(Rarity.COMMON).stacksTo(1)));

    public static final RegistryObject<Item> TERRA_PROTECTOR = ITEMS.register("terra_protector", () -> new TerraProtector(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(Rarity.RARE).stacksTo(1)));

    public static final RegistryObject<Item> GAIA_PROTECTOR = ITEMS.register("gaia_protector", () -> new GaiaProtector(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(Rarity.EPIC).stacksTo(1)));
    public static final RegistryObject<Item> YGGDRAL_PROTECTOR = ITEMS.register("yggdral_protector", () -> new YggdralProtector(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(ItemInit.UNIQUE).stacksTo(1)));

    //weapon
    public static final RegistryObject<Item> YGGDRASIL_SCEPTER = ITEMS.register("yggdral_scepter", () -> new YggdralScepter(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(ItemInit.UNIQUE).stacksTo(1)));


    //cores
    public static final RegistryObject<Item> MARIAS_CORE = ITEMS.register("marias_core", () -> new MariasCore(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(ItemInit.UNIQUE).stacksTo(1)));

    public static final RegistryObject<Item> MARINAS_CORE = ITEMS.register("marinas_core", () -> new MarinasCore(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(ItemInit.UNIQUE).stacksTo(1)));




    //Items
    public static final RegistryObject<Item> YGGDRALIUM_INGOT = ITEMS.register("yggdralium_ingot", () -> new Item(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(Rarity.COMMON)));

    public static RegistryObject<Item> YGGSRALIUM_SHARD = ITEMS.register("yggdralium_shard", () -> new Item(
            new Item.Properties().tab(ModItemGroup.BOTANIC_PLEDGE_TAB).fireResistant().rarity(Rarity.COMMON)));



}
