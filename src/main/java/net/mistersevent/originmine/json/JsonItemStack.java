package net.mistersevent.originmine.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JsonItemStack {
    private static final String[] BYPASS_CLASS = new String[]{"CraftMetaBlockState", "CraftMetaItem", "GlowMetaItem"};

    public static String toJson(ItemStack itemStack) {
        Gson gson = new Gson();
        JsonObject itemJson = new JsonObject();
        itemJson.addProperty("type", itemStack.getType().name());
        if (itemStack.getDurability() > 0) {
            itemJson.addProperty("data", itemStack.getDurability());
        }

        if (itemStack.getAmount() != 1) {
            itemJson.addProperty("amount", itemStack.getAmount());
        }

        if (itemStack.hasItemMeta()) {
            JsonObject metaJson = new JsonObject();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) {
                metaJson.addProperty("displayname", meta.getDisplayName());
            }

            if (meta.hasLore()) {
                JsonArray flags = new JsonArray();
                meta.getLore().forEach((str) -> {
                    flags.add(new JsonPrimitive(str));
                });
                metaJson.add("lore", flags);
            }

            if (meta.hasEnchants()) {
                JsonArray flags = new JsonArray();
                meta.getEnchants().forEach((enchantment, integer) -> {
                    flags.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
                });
                metaJson.add("enchants", flags);
            }

            if (!meta.getItemFlags().isEmpty()) {
                JsonArray flags = new JsonArray();
                meta.getItemFlags().stream().map(Enum::name).forEach((str) -> {
                    flags.add(new JsonPrimitive(str));
                });
                metaJson.add("flags", flags);
            }

            String[] var9 = BYPASS_CLASS;
            int var6 = var9.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String clazz = var9[var7];
                if (meta.getClass().getSimpleName().equals(clazz)) {
                    itemJson.add("item-meta", metaJson);
                    return gson.toJson(itemJson);
                }
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta)meta;
                if (skullMeta.hasOwner()) {
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("owner", skullMeta.getOwner());
                    metaJson.add("extra-meta", extraMeta);
                }
            } else {
                if (meta instanceof BannerMeta) {
                    BannerMeta bannerMeta = (BannerMeta)meta;
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("base-color", bannerMeta.getBaseColor().name());
                    if (bannerMeta.numberOfPatterns() > 0) {
                        JsonArray effects = new JsonArray();
                        bannerMeta.getPatterns().stream().map((pattern) -> {
                            return pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier();
                        }).forEach((str) -> {
                            effects.add(new JsonPrimitive(str));
                        });
                        extraMeta.add("patterns", effects);
                    }

                    metaJson.add("extra-meta", extraMeta);
                } else if (meta instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta)meta;
                    if (esmeta.hasStoredEnchants()) {
                        JsonObject extraMeta = new JsonObject();
                        JsonArray effects = new JsonArray();
                        esmeta.getStoredEnchants().forEach((enchantment, integer) -> {
                            effects.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
                        });
                        extraMeta.add("stored-enchants", effects);
                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (meta instanceof LeatherArmorMeta) {
                    LeatherArmorMeta lameta = (LeatherArmorMeta)meta;
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("color", Integer.toHexString(lameta.getColor().asRGB()));
                    metaJson.add("extra-meta", extraMeta);
                } else if (meta instanceof BookMeta) {
                    BookMeta bmeta = (BookMeta)meta;
                    if (bmeta.hasAuthor() || bmeta.hasPages() || bmeta.hasTitle()) {
                        JsonObject extraMeta = new JsonObject();
                        if (bmeta.hasTitle()) {
                            extraMeta.addProperty("title", bmeta.getTitle());
                        }

                        if (bmeta.hasAuthor()) {
                            extraMeta.addProperty("author", bmeta.getAuthor());
                        }

                        if (bmeta.hasPages()) {
                            JsonArray effects  = new JsonArray();
                            bmeta.getPages().forEach((str) -> {
                                effects.add(new JsonPrimitive(str));
                            });
                            extraMeta.add("pages", effects);
                        }

                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (meta instanceof PotionMeta) {
                    PotionMeta pmeta = (PotionMeta)meta;
                    if (pmeta.hasCustomEffects()) {
                        JsonObject extraMeta = new JsonObject();
                        JsonArray effects  = new JsonArray();
                        pmeta.getCustomEffects().forEach((potionEffect) -> {
                            effects.add(new JsonPrimitive(potionEffect.getType().getName() + ":" + potionEffect.getAmplifier() + ":" + potionEffect.getDuration() / 20));
                        });
                        extraMeta.add("custom-effects", effects);
                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (meta instanceof FireworkEffectMeta) {
                    FireworkEffectMeta femeta = (FireworkEffectMeta)meta;
                    if (femeta.hasEffect()) {
                        FireworkEffect effect = femeta.getEffect();
                        JsonObject extraMeta = new JsonObject();
                        extraMeta.addProperty("type", effect.getType().name());
                        if (effect.hasFlicker()) {
                            extraMeta.addProperty("flicker", true);
                        }

                        if (effect.hasTrail()) {
                            extraMeta.addProperty("trail", true);
                        }


                        if (!effect.getColors().isEmpty()) {
                            JsonArray fadeColors = new JsonArray();
                            effect.getColors().forEach((color) -> {
                                fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB())));
                            });
                            extraMeta.add("colors", fadeColors);
                        }

                        if (!effect.getFadeColors().isEmpty()) {
                            JsonArray fadeColors = new JsonArray();
                            effect.getFadeColors().forEach((color) -> {
                                fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB())));
                            });
                            extraMeta.add("fade-colors", fadeColors);
                        }

                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (meta instanceof FireworkMeta) {
                    FireworkMeta fmeta = (FireworkMeta)meta;
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("power", fmeta.getPower());
                    if (fmeta.hasEffects()) {
                        JsonArray effects = new JsonArray();
                        fmeta.getEffects().forEach((effectx) -> {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("type", effectx.getType().name());
                            if (effectx.hasFlicker()) {
                                jsonObject.addProperty("flicker", true);
                            }

                            if (effectx.hasTrail()) {
                                jsonObject.addProperty("trail", true);
                            }

                            if (!effectx.getColors().isEmpty()) {
                                JsonArray fadeColors = new JsonArray();
                                effectx.getColors().forEach((color) -> {
                                    fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB())));
                                });
                                jsonObject.add("colors", fadeColors);
                            }

                            if (!effectx.getFadeColors().isEmpty()) {
                                JsonArray fadeColors = new JsonArray();
                                effectx.getFadeColors().forEach((color) -> {
                                    fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB())));
                                });
                                jsonObject.add("fade-colors", fadeColors);
                            }

                            effects.add(jsonObject);
                        });
                        extraMeta.add("effects", effects);
                    }

                    metaJson.add("extra-meta", extraMeta);
                } else if (meta instanceof MapMeta) {
                    MapMeta mmeta = (MapMeta)meta;
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("scaling", mmeta.isScaling());
                    metaJson.add("extra-meta", extraMeta);
                }
            }

            itemJson.add("item-meta", metaJson);
        }

        return gson.toJson(itemJson);
    }

    public static ItemStack fromJson(String string) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(string);
        if (!element.isJsonObject()) {
            return null;
        } else {
            JsonObject itemJson = element.getAsJsonObject();
            JsonElement typeElement = itemJson.get("type");
            JsonElement dataElement = itemJson.get("data");
            JsonElement amountElement = itemJson.get("amount");
            if (typeElement.isJsonPrimitive()) {
                String type = typeElement.getAsString();
                short data = dataElement != null ? dataElement.getAsShort() : 0;
                int amount = amountElement != null ? amountElement.getAsInt() : 1;
                ItemStack itemStack = new ItemStack(Material.getMaterial(type));
                itemStack.setDurability(data);
                itemStack.setAmount(amount);
                JsonElement itemMetaElement = itemJson.get("item-meta");
                if (itemMetaElement != null && itemMetaElement.isJsonObject()) {
                    ItemMeta meta = itemStack.getItemMeta();
                    JsonObject metaJson = itemMetaElement.getAsJsonObject();
                    JsonElement displaynameElement = metaJson.get("displayname");
                    JsonElement loreElement = metaJson.get("lore");
                    JsonElement enchants = metaJson.get("enchants");
                    JsonElement flagsElement = metaJson.get("flags");
                    if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
                        meta.setDisplayName(displaynameElement.getAsString());
                    }

                    if (loreElement != null && loreElement.isJsonArray()) {
                        JsonArray jarray = loreElement.getAsJsonArray();
                        List<String> lore = new ArrayList(jarray.size());
                        jarray.forEach((jsonElement) -> {
                            if (jsonElement.isJsonPrimitive()) {
                                lore.add(jsonElement.getAsString());
                            }

                        });
                        meta.setLore(lore);
                    }

                    if (enchants != null && enchants.isJsonArray()) {
                        JsonArray jarray = enchants.getAsJsonArray();
                        jarray.forEach((jsonElement) -> {
                            if (jsonElement.isJsonPrimitive()) {
                                String enchantString = jsonElement.getAsString();
                                if (enchantString.contains(":")) {
                                    try {
                                        String[] splitEnchant = enchantString.split(":");
                                        Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                        int level = Integer.parseInt(splitEnchant[1]);
                                        if (enchantment != null && level > 0) {
                                            meta.addEnchant(enchantment, level, true);
                                        }
                                    } catch (NumberFormatException var6) {
                                        var6.printStackTrace();
                                    }
                                }
                            }

                        });
                    }

                    if (flagsElement != null && flagsElement.isJsonArray()) {
                        JsonArray jarray = flagsElement.getAsJsonArray();
                        jarray.forEach((jsonElement) -> {
                            if (jsonElement.isJsonPrimitive()) {
                                ItemFlag[] var2 = ItemFlag.values();
                                int var3 = var2.length;

                                for(int var4 = 0; var4 < var3; ++var4) {
                                    ItemFlag flag = var2[var4];
                                    if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                                        meta.addItemFlags(new ItemFlag[]{flag});
                                        break;
                                    }
                                }
                            }

                        });
                    }

                    String[] var33 = BYPASS_CLASS;
                    int var34 = var33.length;

                    for(int var20 = 0; var20 < var34; ++var20) {
                        String clazz = var33[var20];
                        if (meta.getClass().getSimpleName().equals(clazz)) {
                            return itemStack;
                        }
                    }

                    JsonElement extrametaElement = metaJson.get("extra-meta");
                    if (extrametaElement != null && extrametaElement.isJsonObject()) {
                        try {
                            JsonObject extraJson = extrametaElement.getAsJsonObject();
                            if (meta instanceof SkullMeta) {
                                JsonElement effectTypeElement = extraJson.get("owner");
                                if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                    SkullMeta smeta = (SkullMeta)meta;
                                    smeta.setOwner(effectTypeElement.getAsString());
                                }
                            } else {
                                if (meta instanceof BannerMeta) {
                                    JsonElement effectTypeElement = extraJson.get("base-color");
                                    JsonElement flickerElement = extraJson.get("patterns");
                                    BannerMeta bmeta = (BannerMeta)meta;
                                    if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                        try {
                                            Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter((dyeColor) -> {
                                                return dyeColor.name().equalsIgnoreCase(effectTypeElement.getAsString());
                                            }).findFirst();
                                            if (color.isPresent()) {
                                                bmeta.setBaseColor((DyeColor)color.get());
                                            }
                                        } catch (NumberFormatException var31) {
                                        }
                                    }

                                    if (flickerElement != null && flickerElement.isJsonArray()) {
                                        JsonArray jarray = flickerElement.getAsJsonArray();
                                        List<Pattern> patterns = new ArrayList(jarray.size());
                                        jarray.forEach((jsonElement) -> {
                                            String patternString = jsonElement.getAsString();
                                            if (patternString.contains(":")) {
                                                String[] splitPattern = patternString.split(":");
                                                Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter((dyeColor) -> {
                                                    return dyeColor.name().equalsIgnoreCase(splitPattern[0]);
                                                }).findFirst();
                                                PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                                                if (color.isPresent() && patternType != null) {
                                                    patterns.add(new Pattern((DyeColor)color.get(), patternType));
                                                }
                                            }

                                        });
                                        if (!patterns.isEmpty()) {
                                            bmeta.setPatterns(patterns);
                                        }
                                    }
                                } else {
                                    if (meta instanceof EnchantmentStorageMeta) {
                                        JsonElement effectTypeElement = extraJson.get("stored-enchants");
                                        if (effectTypeElement != null && effectTypeElement.isJsonArray()) {
                                            EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta)meta;
                                            JsonArray jarray = effectTypeElement.getAsJsonArray();
                                            jarray.forEach((jsonElement) -> {
                                                if (jsonElement.isJsonPrimitive()) {
                                                    String enchantString = jsonElement.getAsString();
                                                    if (enchantString.contains(":")) {
                                                        try {
                                                            String[] splitEnchant = enchantString.split(":");
                                                            Enchantment enchantment = Enchantment.getByName(splitEnchant[0]);
                                                            int level = Integer.parseInt(splitEnchant[1]);
                                                            if (enchantment != null && level > 0) {
                                                                esmeta.addStoredEnchant(enchantment, level, true);
                                                            }
                                                        } catch (NumberFormatException var6) {
                                                        }
                                                    }
                                                }

                                            });
                                        }
                                    } else if (meta instanceof LeatherArmorMeta) {
                                        JsonElement effectTypeElement = extraJson.get("color");
                                        if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                            LeatherArmorMeta lameta = (LeatherArmorMeta)meta;

                                            try {
                                                lameta.setColor(Color.fromRGB(Integer.parseInt(effectTypeElement.getAsString(), 16)));
                                            } catch (NumberFormatException var30) {
                                            }
                                        }
                                    } else {
                                        if (meta instanceof BookMeta) {
                                            JsonElement effectTypeElement = extraJson.get("title");
                                            JsonElement flickerElement = extraJson.get("author");
                                            JsonElement trailElement = extraJson.get("pages");
                                            BookMeta bmeta = (BookMeta)meta;
                                            if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                                bmeta.setTitle(effectTypeElement.getAsString());
                                            }

                                            if (flickerElement != null && flickerElement.isJsonPrimitive()) {
                                                bmeta.setAuthor(flickerElement.getAsString());
                                            }

                                            if (trailElement != null && trailElement.isJsonArray()) {
                                                JsonArray jarray = trailElement.getAsJsonArray();
                                                List<String> pages = new ArrayList(jarray.size());
                                                jarray.forEach((jsonElement) -> {
                                                    if (jsonElement.isJsonPrimitive()) {
                                                        pages.add(jsonElement.getAsString());
                                                    }

                                                });
                                                bmeta.setPages(pages);
                                            }
                                        } else if (meta instanceof PotionMeta) {
                                            JsonElement effectTypeElement = extraJson.get("custom-effects");
                                            if (effectTypeElement != null && effectTypeElement.isJsonArray()) {
                                                PotionMeta pmeta = (PotionMeta)meta;
                                                JsonArray jarray = effectTypeElement.getAsJsonArray();
                                                jarray.forEach((jsonElement) -> {
                                                    if (jsonElement.isJsonPrimitive()) {
                                                        String enchantString = jsonElement.getAsString();
                                                        if (enchantString.contains(":")) {
                                                            try {
                                                                String[] splitPotions = enchantString.split(":");
                                                                PotionEffectType potionType = PotionEffectType.getByName(splitPotions[0]);
                                                                int amplifier = Integer.parseInt(splitPotions[1]);
                                                                int duration = Integer.parseInt(splitPotions[2]) * 20;
                                                                if (potionType != null) {
                                                                    pmeta.addCustomEffect(new PotionEffect(potionType, amplifier, duration), true);
                                                                }
                                                            } catch (NumberFormatException var7) {
                                                            }
                                                        }
                                                    }

                                                });
                                            }
                                        } else if (meta instanceof FireworkEffectMeta) {
                                            JsonElement effectTypeElement = extraJson.get("type");
                                            JsonElement flickerElement = extraJson.get("flicker");
                                            JsonElement trailElement = extraJson.get("trail");
                                            JsonElement colorsElement = extraJson.get("colors");
                                            JsonElement fadeColorsElement = extraJson.get("fade-colors");
                                            if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                                FireworkEffectMeta femeta = (FireworkEffectMeta)meta;
                                                Type effectType = Type.valueOf(effectTypeElement.getAsString());
                                                if (effectType != null) {
                                                    List<Color> colors = new ArrayList();
                                                    if (colorsElement != null && colorsElement.isJsonArray()) {
                                                        colorsElement.getAsJsonArray().forEach((colorElement) -> {
                                                            if (colorElement.isJsonPrimitive()) {
                                                                colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                            }

                                                        });
                                                    }

                                                    List<Color> fadeColors = new ArrayList();
                                                    if (fadeColorsElement != null && fadeColorsElement.isJsonArray()) {
                                                        fadeColorsElement.getAsJsonArray().forEach((colorElement) -> {
                                                            if (colorElement.isJsonPrimitive()) {
                                                                fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                            }

                                                        });
                                                    }

                                                    Builder builder = FireworkEffect.builder().with(effectType);
                                                    if (flickerElement != null && flickerElement.isJsonPrimitive()) {
                                                        builder.flicker(flickerElement.getAsBoolean());
                                                    }

                                                    if (trailElement != null && trailElement.isJsonPrimitive()) {
                                                        builder.trail(trailElement.getAsBoolean());
                                                    }

                                                    if (!colors.isEmpty()) {
                                                        builder.withColor(colors);
                                                    }

                                                    if (!fadeColors.isEmpty()) {
                                                        builder.withFade(fadeColors);
                                                    }

                                                    femeta.setEffect(builder.build());
                                                }
                                            }
                                        } else if (meta instanceof FireworkMeta) {
                                            FireworkMeta fmeta = (FireworkMeta)meta;
                                            AtomicReference<JsonElement> flickerElement = new AtomicReference<>(extraJson.get("effects"));
                                            AtomicReference<JsonElement> trailElement = new AtomicReference<>(extraJson.get("power"));
                                            if (trailElement.get() != null && trailElement.get().isJsonPrimitive()) {
                                                fmeta.setPower(trailElement.get().getAsInt());
                                            }

                                            if (flickerElement.get() != null && flickerElement.get().isJsonArray()) {
                                                flickerElement.get().getAsJsonArray().forEach((jsonElement) -> {
                                                    if (jsonElement.isJsonObject()) {
                                                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                                                        JsonElement effectTypeElement = jsonObject.get("type");
                                                        flickerElement.set(jsonObject.get("flicker"));
                                                        trailElement.set(jsonObject.get("trail"));
                                                        JsonElement colorsElement = jsonObject.get("colors");
                                                        JsonElement fadeColorsElement = jsonObject.get("fade-colors");
                                                        if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                                            Type effectType = Type.valueOf(effectTypeElement.getAsString());
                                                            if (effectType != null) {
                                                                List<Color> colors = new ArrayList();
                                                                if (colorsElement != null && colorsElement.isJsonArray()) {
                                                                    colorsElement.getAsJsonArray().forEach((colorElement) -> {
                                                                        if (colorElement.isJsonPrimitive()) {
                                                                            colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                                        }

                                                                    });
                                                                }

                                                                List<Color> fadeColors = new ArrayList();
                                                                if (fadeColorsElement != null && fadeColorsElement.isJsonArray()) {
                                                                    fadeColorsElement.getAsJsonArray().forEach((colorElement) -> {
                                                                        if (colorElement.isJsonPrimitive()) {
                                                                            fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                                        }

                                                                    });
                                                                }

                                                                Builder builder = FireworkEffect.builder().with(effectType);
                                                                if (flickerElement.get() != null && flickerElement.get().isJsonPrimitive()) {
                                                                    builder.flicker(flickerElement.get().getAsBoolean());
                                                                }

                                                                if (trailElement.get() != null && trailElement.get().isJsonPrimitive()) {
                                                                    builder.trail(trailElement.get().getAsBoolean());
                                                                }

                                                                if (!colors.isEmpty()) {
                                                                    builder.withColor(colors);
                                                                }

                                                                if (!fadeColors.isEmpty()) {
                                                                    builder.withFade(fadeColors);
                                                                }

                                                                fmeta.addEffect(builder.build());
                                                            }
                                                        }
                                                    }

                                                });
                                            }
                                        } else if (meta instanceof MapMeta) {
                                            MapMeta mmeta = (MapMeta)meta;
                                            JsonElement flickerElement = extraJson.get("scaling");
                                            if (flickerElement != null && flickerElement.isJsonPrimitive()) {
                                                mmeta.setScaling(flickerElement.getAsBoolean());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception var32) {
                            return null;
                        }
                    }

                    itemStack.setItemMeta(meta);
                }

                return itemStack;
            } else {
                return null;
            }
        }
    }
}
