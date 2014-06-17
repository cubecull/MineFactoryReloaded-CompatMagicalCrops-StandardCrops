package cubecull.mfr.compat.magicalcrops;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCropPlant;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MFRCompatMagicalCrops._ID, name = MFRCompatMagicalCrops._NAME, version = MFRCompatMagicalCrops._VERSION, dependencies = MFRCompatMagicalCrops._DEPENDENCIES)
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MFRCompatMagicalCrops
{

	public static final String _ID = "MineFactoryReloaded|CompatMagicalCrops|StandardCrops";
	public static final String _NAME = "MineFactoryReloaded Compatability: MagicalCrops (Standard)";
	public static final String _VERSION = "0.0.1";
	public static final String _DEPENDENCIES = "after:PowerCrystalsCore;after:MineFactoryReloaded;after:magicalcrops";

	public static final String[] foodCrops =
	{
			"Blackberry",
			"Blueberry",
			"Chili",
			"Cucumber",
			"Grape",
			"Raspberry",
			"Strawberry",
			"Sweetcorn",
			"Tomato",
			"SugarCane"
	};

	@Instance(_ID)
	public static MFRCompatMagicalCrops instance;
	private Logger _log;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		try {
			_log = event.getModLog();

			if(!Loader.isModLoaded("magicalcrops"))
			{
				_log.log(Level.SEVERE, "Magical Crops missing - MineFactoryReloaded Compatability: Magical Crops (Standard) not loading");
				throw new Exception();
			}
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		try
		{
			_log.log(Level.INFO, "Loading MineFactoryReloaded Compatibility for Magical Crops (Standard)");
			registerInMFR();
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		_log.log(Level.INFO, "MineFactoryReloaded Compatibility for Magical Crops (Standard) successfully initialized");
	}
	
	private void registerInMFR() throws Exception
	{
		Class<?> cropsModClass = Class.forName("magicalcrops.mod_sCrops");

		_log.log(Level.INFO, "Registering food crops in MineFactoryReloaded");
		registerCrops(cropsModClass, foodCrops);
	}

	private void registerCrops(Class mod, String[] crops) throws Exception
	{
		for (String cropName : crops)
		{
			Block cropBlock = null;
			Item seedItem = null;

			try {
				cropBlock = ((Block)mod.getField("Crop" + cropName).get(null));
			} catch (Exception exception)
			{
				_log.log(Level.SEVERE, "Tried to get Crop: " + cropName + " but failed.");
				continue;
			}
			
			try {
				seedItem = ((Item)mod.getField("Seeds" + cropName).get(null));
			} catch (Exception exception)
			{
				_log.log(Level.SEVERE, "Tried to get Seeds: " + cropName + " but failed.");
				continue;
			}
			
			try {
				MFRRegistry.registerHarvestable(new HarvestableCropPlant(cropBlock.blockID, 7));
				_log.log(Level.INFO, "Registered Harvestable Crop: Crop" + cropName);
			} catch (Exception exception)
			{
				_log.log(Level.SEVERE, "Tried to register Harvestable Crop: Crop" + cropName + " but failed.");
				throw exception;
			}
			
			try {
				MFRRegistry.registerPlantable(new PlantableCropPlant(seedItem.itemID, cropBlock.blockID));
				_log.log(Level.INFO, "Registered Plantable Seed: Seeds" + cropName);
			} catch (Exception exception)
			{
				_log.log(Level.SEVERE, "Tried to register Plantable Seed: Seeds" + cropName + " but failed.");
				throw exception;
			}
		}
	}
}
