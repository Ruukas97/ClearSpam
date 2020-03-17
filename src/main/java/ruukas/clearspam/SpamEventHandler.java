package ruukas.clearspam;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class SpamEventHandler {
	private static ArrayList<SpamCount> messages = new ArrayList<SpamCount>();
	private static ArrayList<String> ignored = new ArrayList<String>();
	private static boolean isDisabled = false;

	@SubscribeEvent
	public static void onChat(ClientChatReceivedEvent event) {
		if (isDisabled) {
			return;
		}

		ITextComponent mes = event.getMessage();

		for (String ig : ignored) {
			if (mes.getUnformattedText().contains(ig)) {
				TextComponentString igMes = new TextComponentString("Ignored");
				igMes.getStyle().setColor(TextFormatting.RED);
				igMes.getStyle().setHoverEvent(new HoverEvent(Action.SHOW_TEXT, mes));
				Minecraft.getMinecraft().player.sendMessage(igMes);
				mes = igMes;
				//event.setCanceled(true);
			}
		}

		/*
		 * int mesLength = mes.getUnformattedText().length(), caps = 0;
		 * 
		 * for(char c : mes.getUnformattedText().toCharArray()){
		 * if(Character.isUpperCase(c)){ caps++; } }
		 */

		boolean existsAlready = false;

		for (SpamCount spam : messages) {
			if (spam.isSame(mes)) {
				existsAlready = true;
				Long currentTime = System.currentTimeMillis();
				if ((currentTime - spam.getTime()) < 30000) {// 30 seconds
					spam.increaseCounter();
					mes.appendText(TextFormatting.GOLD + " [" + TextFormatting.GRAY + "x" + TextFormatting.RED
							+ spam.getCounter() + TextFormatting.GOLD + "]");
				} else {
					spam.resetCounter();
				}
				spam.setTime(currentTime);
			}
		}

		if (!existsAlready) {
			messages.add(new SpamCount(mes));
		} else {
			List<ChatLine> chatLines = ClearSpam.getChatLines();

			if (chatLines != null) {
				for (int i = 0; i < chatLines.size(); i++) {
					ChatLine line = chatLines.get(i);
					String counterStr = TextFormatting.GOLD + " [" + TextFormatting.GRAY + "x";

					String lineStr = line.getChatComponent().getUnformattedText();

					int lastIndex = lineStr.lastIndexOf(counterStr);
					if (lastIndex > 0) {
						lineStr = lineStr.substring(0, lastIndex);
					}

					String mesStr = mes.getUnformattedText();

					lastIndex = mesStr.lastIndexOf(counterStr);
					if (lastIndex > 0) {
						mesStr = mesStr.substring(0, lastIndex);
					}

					if (lineStr.equals(mesStr)) {
						chatLines.remove(line);
					}
				}
			}

			ClearSpam.getChatGui().refreshChat();
		}

	}

	@SubscribeEvent
	public static void onDisconnect(ClientDisconnectionFromServerEvent event) {
		messages.clear();
	}

	@SubscribeEvent
	public static void onSendChat(ClientChatEvent event) {
		String mes = event.getMessage();
		if (mes.startsWith(".clearspam")) {
			event.setCanceled(true);
			ClearSpam.getChatGui().addToSentMessages(mes);
			if (mes.equals(".clearspam toggle")) {
				isDisabled = !isDisabled;
				sendMessage(("ClearSpam has been " + (isDisabled ? "disabled" : "enabled" + ".")));
			} else if (mes.equals(".clearspam on")) {
				isDisabled = false;
				sendMessage(("ClearSpam has been enabled."));
			} else if (mes.equals(".clearspam off")) {
				isDisabled = true;
				sendMessage(("ClearSpam has been disabled."));
			} else if (mes.equals(".clearspam reset")) {
				messages.clear();
				sendMessage(("ClearSpam counter has been reset."));
			} else if (mes.startsWith(".clearspam block")) {
				String ignoreStr = mes.substring(17);
				ignored.add(ignoreStr);
				sendMessage(("Messages containing \"" + ignoreStr + "\" will no longer reach chat."));
			} else if (mes.equals(".clearspam") || mes.equals(".clearspam help")) {
				sendCommandList();
			} else {
				sendMessage(TextFormatting.RED + "That ClearSpam command was not recognized.");
				sendMessage("Type \".clearspam help\" for a list of commands.");
			}
		}
	}

	public static void sendCommandList() {
		sendMessage("-=-=-=-=-= ClearSpam Help =-=-=-=-=-", TextFormatting.DARK_GREEN);
		sendMessage(".clearspam toggle" + TextFormatting.GRAY + " - Toggles ClearSpam");
		sendMessage(".clearspam on/off" + TextFormatting.GRAY + " - Turns ClearSpam on or off");
		sendMessage(".clearspam reset" + TextFormatting.GRAY + " - Resets the ClearSpam counters");
		sendMessage(".clearspam block <string>" + TextFormatting.GRAY + " - Block messages that contain the string");
	}

	public static void sendMessage(String message) {
		sendMessage(message, TextFormatting.GOLD);
	}

	public static void sendMessage(String message, TextFormatting color) {
		TextComponentString mes = new TextComponentString(message);
		mes.getStyle().setColor(color);
		Minecraft.getMinecraft().player.sendMessage(mes);
	}
}
