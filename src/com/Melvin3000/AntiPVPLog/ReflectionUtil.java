package com.Melvin3000.AntiPVPLog;

import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

public class ReflectionUtil {

	private static String version;

	/**
	 * Converts ItemStack to a JSON String that can be used with hoverable ChatComponent messages
	 * Uses reflection to go from a bukkit ItemStack to NMS ItemStack which can invoke the save method
	 * and finally turns the result into a string.
	 * https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964
	 * https://gist.github.com/sainttx/34fdd8fa7657024414ba
	 * @param item ItemStack to use
	 * @return String JSON representation of the ItemStack, or null if something goes wrong
	 */
	public static String getItemStackAsJSON(ItemStack item) {
		Object nmsItemStack;
		Class<?> craftItemStackClass = getClass("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack");
		Method asNMSCopyMethod = getMethod(craftItemStackClass, "asNMSCopy", ItemStack.class);
		try {
			nmsItemStack = asNMSCopyMethod.invoke(null, item);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Class<?> nmsItemStackClass = getClass("net.minecraft.server." + getVersion() + ".ItemStack");
		Class<?> NBTTagCompoundClass = getClass("net.minecraft.server." + getVersion() + ".NBTTagCompound");
		Method nmsItemStackSaveMethod = getMethod(nmsItemStackClass, "save", NBTTagCompoundClass);
		try {
			Object jsonObject = nmsItemStackSaveMethod.invoke(nmsItemStack, NBTTagCompoundClass.newInstance());
			return jsonObject.toString();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Class<?> getClass(String className) {
		Class<?> c;
		try {
			c = Class.forName(className);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
		return c;
	}

	private static Method getMethod(Class<?> c, String methodName, Class<?>... params) {
		try {
			return c.getMethod(methodName, params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getVersion() {
		if (version == null) {
			version = AntiPVPLog.instance.getServer().getClass().getPackage().getName();
			version = version.substring(version.lastIndexOf('.') + 1, version.length());
		}
		return version;
	}
}
