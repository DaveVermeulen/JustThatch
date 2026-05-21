package org.fynixx.just_thatch;

import com.teamabnormals.blueprint.common.block.thatch.ThatchBlock;
import com.teamabnormals.blueprint.common.block.thatch.ThatchSlabBlock;
import com.teamabnormals.blueprint.common.block.thatch.ThatchStairBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JustThatch.MODID)
public class JustThatch {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "just_thatch";
    // Create a Deferred Register to hold Blocks which will all be registered under the "just_thatch" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "just_thatch" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "just_thatch" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final BlockBehaviour.Properties THATCH_PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.GRASS).noOcclusion();

    public static final DeferredBlock<Block> GRASS_THATCH = registerBlock("grass_thatch", () -> new ThatchBlock(JustThatch.THATCH_PROPERTIES));
    public static final DeferredBlock<Block> GRASS_THATCH_SLAB = registerBlock("grass_thatch_slab", () -> new ThatchSlabBlock(JustThatch.THATCH_PROPERTIES));
    public static final DeferredBlock<Block> GRASS_THATCH_STAIRS = registerBlock("grass_thatch_stairs", () -> new ThatchStairBlock(GRASS_THATCH.get().defaultBlockState(), JustThatch.THATCH_PROPERTIES));

    public static final DeferredBlock<Block> WHEAT_THATCH = registerBlock("wheat_thatch", () -> new ThatchBlock(JustThatch.THATCH_PROPERTIES));
    public static final DeferredBlock<Block> WHEAT_THATCH_SLAB = registerBlock("wheat_thatch_slab", () -> new ThatchSlabBlock(JustThatch.THATCH_PROPERTIES));
    public static final DeferredBlock<Block> WHEAT_THATCH_STAIRS = registerBlock("wheat_thatch_stairs", () -> new ThatchStairBlock(WHEAT_THATCH.get().defaultBlockState(), JustThatch.THATCH_PROPERTIES));

    public static final DeferredBlock<Block> BURNT_THATCH = registerBlock("burnt_thatch", () -> new ThatchBlock(JustThatch.THATCH_PROPERTIES));
    public static final DeferredBlock<Block> BURNT_THATCH_SLAB = registerBlock("burnt_thatch_slab", () -> new ThatchSlabBlock(JustThatch.THATCH_PROPERTIES));
    public static final DeferredBlock<Block> BURNT_THATCH_STAIRS = registerBlock("burnt_thatch_stairs", () -> new ThatchStairBlock(BURNT_THATCH.get().defaultBlockState(), JustThatch.THATCH_PROPERTIES));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        JustThatch.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Creates a creative tab with the id "just_thatch:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THATCH_TAB = CREATIVE_MODE_TABS.register("just_thatch_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.just_thatch")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> WHEAT_THATCH.asItem().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(GRASS_THATCH);
        output.accept(GRASS_THATCH_STAIRS);
        output.accept(GRASS_THATCH_SLAB);
        output.accept(WHEAT_THATCH);
        output.accept(WHEAT_THATCH_STAIRS);
        output.accept(WHEAT_THATCH_SLAB);
        output.accept(BURNT_THATCH);
        output.accept(BURNT_THATCH_STAIRS);
        output.accept(BURNT_THATCH_SLAB);
    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public JustThatch(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Just_Thatch) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
        }
    }
}
