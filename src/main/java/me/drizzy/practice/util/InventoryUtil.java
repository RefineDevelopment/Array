package me.drizzy.practice.util;

import me.drizzy.practice.Array;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class InventoryUtil {

    public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static String serializeInventory(ItemStack[] source) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : source) {
            builder.append(serializeItemStack(itemStack));
            builder.append(";");
        }

        return builder.toString();
    }

    public static ItemStack[] deserializeInventory(String source) {
        List<ItemStack> items = new ArrayList<>();
        String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeItemStack(piece));
        }

        return items.toArray(new ItemStack[items.size()]);
    }

    public static String serializeEffects(List<PotionEffect> source) {
        StringBuilder builder = new StringBuilder();
        if (source.size() == 0) return null;

        for (PotionEffect potionEffect : source) {
            String potionString = serializeEffect(potionEffect);
            if (potionString == null || potionString == "null") continue;

            builder.append(potionString);
            builder.append(";");
        }

        return builder.toString();
    }

    public static List<PotionEffect> deserializeEffects(String source) {
        List<PotionEffect> items = new ArrayList<>();

        if (source.equalsIgnoreCase(""))
            return null;

        String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeEffect(piece));
        }

        return items;
    }

    public static String serializeItemStack(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        String isType = String.valueOf(item.getType().getId());
        builder.append("t@").append(isType);

        if (item.getDurability() != 0) {
            String isDurability = String.valueOf(item.getDurability());
            builder.append(":d@").append(isDurability);
        }

        if (item.getAmount() != 1) {
            String isAmount = String.valueOf(item.getAmount());
            builder.append(":a@").append(isAmount);
        }

        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        if (enchantments.size() > 0) {
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                builder.append(":e@").append(enchantment.getKey().getId()).append("@").append(enchantment.getValue());
            }
        }

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                builder.append(":dn@").append(itemMeta.getDisplayName());
            }

            if (itemMeta.hasLore()) {
                builder.append(":l@").append(itemMeta.getLore());
            }
        }

//		if (item.getType() == Material.POTION) {
//			Potion potion = Potion.fromItemStack(item);
//
//			builder.append(":pd@")
//			       .append(potion.getType().getDamageValue())
//			       .append("-")
//			       .append(potion.getLevel());
//
//			for (PotionEffect effect : potion.getEffects()) {
//				builder.append("=")
//				       .append(effect.getType().getId())
//				       .append("-")
//				       .append(effect.getDuration())
//				       .append("-")
//				       .append(effect.getAmplifier());
//			}
//		}

        return builder.toString();
    }

    public static ItemStack deserializeItemStack(String in) {
        ItemStack item = null;
        ItemMeta meta = null;

        if (in.equals("null")) {
            return new ItemStack(Material.AIR);
        }

        String[] split = in.split(":");

        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String attributeId = itemAttribute[0];

            switch (attributeId) {
                case "t": {
                    item = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    meta = item.getItemMeta();
                    break;
                }
                case "d": {
                    if (item != null) {
                        item.setDurability(Short.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "a": {
                    if (item != null) {
                        item.setAmount(Integer.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "e": {
                    if (item != null) {
                        item.addUnsafeEnchantment(
                                Enchantment.getById(Integer.valueOf(itemAttribute[1])),
                                Integer.valueOf(itemAttribute[2])
                        );
                        break;
                    }
                    break;
                }
                case "dn": {
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        break;
                    }
                    break;
                }
                case "l": {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);

                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }

                                lore.set(x, s);
                            }
                        }
                    }

                    if (meta != null) {
                        meta.setLore(lore);
                        break;
                    }

                    break;
                }
//				case "pd": {
//					if (item != null && item.getType() == Material.POTION) {
//						String[] effectsList = itemAttribute[1].split("=");
//						String[] potionData = effectsList[0].split("-");
//
//						Potion potion = new Potion(PotionType.getByDamageValue(Integer.valueOf(potionData[0])),
//								Integer.valueOf(potionData[1]));
//						potion.setSplash(item.getDurability() >= 16000);
//
//						PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
//
//						for (int i = 1; i < effectsList.length; i++) {
//							String[] effectData = effectsList[1].split("-");
//
//							PotionEffect potionEffect = new PotionEffect(PotionEffectType.getById(
//									Integer.valueOf(effectData[0])), Double.valueOf(effectData[1]).intValue(),
//									Integer.valueOf(effectData[2]), false
//							);
//
//							potionMeta.addCustomEffect(potionEffect, true);
//						}
//
//						item = potion.toItemStack(item.getAmount());
//						item.setItemMeta(potionMeta);
//					}
//
//					break;
//				}
            }
        }

        if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
        }

        return item;
    }

    public static String serializeEffect(PotionEffect item) {
        StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        builder.append("t@").append(item.getType().getName());
        builder.append(":d@").append(item.getDuration());
        builder.append(":a@").append(item.getAmplifier());

        return builder.toString();
    }

    public static PotionEffect deserializeEffect(String in) {
        PotionEffect effect = null;
        PotionEffectType type = null;
        int duration = 0;
        int amplifier = 0;

        String[] split = in.split(":");

        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String attributeId = itemAttribute[0];

            switch (attributeId) {
                case "t": {
                    type = PotionEffectType.getByName(itemAttribute[1]);
                    break;
                }
                case "d": {
                    duration = Integer.parseInt(itemAttribute[1]);
                    break;
                }
                case "a": {
                    amplifier = Integer.parseInt(itemAttribute[1]);
                    break;
                }
            }
        }

        effect = new PotionEffect(type, duration, amplifier);

        return effect;
    }

    public static void removeCrafting(Material material) {
        Iterator<Recipe> iterator = Array.getInstance().getServer().recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (recipe != null && recipe.getResult().getType() == material) {
                iterator.remove();
            }
        }
    }

}
