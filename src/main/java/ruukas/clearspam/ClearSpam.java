package ruukas.clearspam;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

@Mod(name = "ClearSpam", modid = ClearSpam.MODID, version = ClearSpam.VERSION)
public class ClearSpam {
	public static final String MODID = "clearspam";
	public static final String VERSION = "0.3.1";
	
	//TODO remove caps
	//TODO add a [x] on a filtered message, which will show the violation on mouseover¨
	//TODO add config to save settings
	//TODO add command to change timer
	//TODO simplify links

	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(SpamEventHandler.class);
	}

	@Nullable
	public static List<ChatLine> getChatLines() {
		List<ChatLine> chatLine = null;

		try {
			Field chatLinesField = ReflectionHelper.findField(GuiNewChat.class,
					new String[] { "chatLines", "field_146252_h" });
			// Useful source for getting SRG names: mcpbot.bspk.rs
			// field_146252_h,chatLines,0,Chat lines to be displayed in the chat
			// box
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(chatLinesField, chatLinesField.getModifiers() & ~Modifier.FINAL);

			chatLine = (List<ChatLine>) chatLinesField.get(getChatGui());

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnableToFindFieldException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return chatLine;
	}

	public static GuiNewChat getChatGui() {
		return Minecraft.getMinecraft().ingameGUI.getChatGUI();
	}
}
