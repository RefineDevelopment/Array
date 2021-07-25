package xyz.refinedev.practice.events.meta.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Color;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/30/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public enum EventGroupColor {

    AMARANTH("Amaranth", Color.fromRGB(229, 43, 80)),
    AMBER("Amber", Color.fromRGB(255, 191, 0)),
    AMETHYST("Amethyst", Color.fromRGB(153, 102, 204)),
    APRICOT("Apricot", Color.fromRGB(251, 206, 177)),
    AQUAMARINE("Aquamarine", Color.fromRGB(127, 255, 212)),
    AZURE("Azure", Color.fromRGB(0, 127, 255)),
    BABY_BLUE("Baby blue", Color.fromRGB(137, 207, 240)),
    BRICK_RED("Brick red", Color.fromRGB(203, 65, 84)),
    BLACK("Black", Color.fromRGB(0, 0, 0)),
    BLUE("Blue", Color.fromRGB(0, 0, 255)),
    BLUE_GREEN("Blue green", Color.fromRGB(0, 149, 182)),
    BLUE_VIOLET("Blue violet", Color.fromRGB(138, 43, 226)),
    BLUSH("Blush", Color.fromRGB(222, 93, 131)),
    BRONZE("Bronze", Color.fromRGB(205, 127, 50)),
    BROWN("Brown", Color.fromRGB(150, 75, 0)),
    BURGUNDY("Burgundy", Color.fromRGB(128, 0, 32)),
    BYZANTIUM("Byzantium", Color.fromRGB(112, 41, 99)),
    CARMINE("Carmine", Color.fromRGB(150, 0, 24)),
    CERISE("Cerise", Color.fromRGB(222, 49, 99)),
    CERULEAN("Cerulean", Color.fromRGB(0, 123, 167)),
    CHAMPAGNE("Champagne", Color.fromRGB(247, 231, 206)),
    CHOCOLATE("Chocolate", Color.fromRGB(123, 63, 0)),
    COBALT_BLUE("Cobalt blue", Color.fromRGB(0, 71, 171)),
    COFFEE("Coffee", Color.fromRGB(111, 78, 55)),
    CORAL("Coral", Color.fromRGB(255, 127, 80)),
    CRIMSON("Crimson", Color.fromRGB(220, 20, 60)),
    CYAN("Cyan", Color.fromRGB(0, 255, 255)),
    DESERT_SAND("Desert sand", Color.fromRGB(237, 201, 175)),
    ELECTRIC_BLUE("Electric blue", Color.fromRGB(125, 249, 255)),
    EMERALD("Emerald", Color.fromRGB(80, 200, 120)),
    GOLD("Gold", Color.fromRGB(255, 215, 0)),
    GRAY("Gray", Color.fromRGB(128, 128, 128)),
    GREEN("Green", Color.fromRGB(0, 128, 0)),
    INDIGO("Indigo", Color.fromRGB(75, 0, 130)),
    JADE("Jade", Color.fromRGB(0, 168, 107)),
    LAVENDER("Lavender", Color.fromRGB(181, 126, 220)),
    LEMON("Lemon", Color.fromRGB(255, 247, 0)),
    LILAC("Lilac", Color.fromRGB(200, 162, 200)),
    LIME("Lime", Color.fromRGB(191, 255, 0)),
    MAGENTA("Magenta", Color.fromRGB(255, 0, 255)),
    MAROON("Maroon", Color.fromRGB(128, 0, 0)),
    MAUVE("Mauve", Color.fromRGB(224, 176, 255)),
    NAVY_BLUE("Navy blue", Color.fromRGB(0, 0, 128)),
    OCHRE("Ochre", Color.fromRGB(204, 119, 34)),
    OLIVE("Olive", Color.fromRGB(128, 128, 0)),
    ORANGE("Orange", Color.fromRGB(255, 102, 0)),
    ORANGE_RED("Orange red", Color.fromRGB(255, 69, 0)),
    ORCHID("Orchid", Color.fromRGB(218, 112, 214)),
    PEACH("Peach", Color.fromRGB(255, 229, 180)),
    PEAR("Pear", Color.fromRGB(209, 226, 49)),
    PERIWINKLE("Periwinkle", Color.fromRGB(204, 204, 255)),
    PERSIAN_BLUE("Persian blue", Color.fromRGB(28, 57, 187)),
    PINK("Pink", Color.fromRGB(253, 108, 158)),
    PLUM("Plum", Color.fromRGB(142, 69, 133)),
    PRUSSIAN_BLUE("Prussian blue", Color.fromRGB(0, 49, 83)),
    PUCE("Puce", Color.fromRGB(204, 136, 153)),
    PURPLE("Purple", Color.fromRGB(128, 0, 128)),
    RASPBERRY("Raspberry", Color.fromRGB(227, 11, 92)),
    RED("Red", Color.fromRGB(255, 0, 0)),
    ROSE("Rose", Color.fromRGB(255, 0, 127)),
    RUBY("Ruby", Color.fromRGB(224, 17, 95)),
    SALMON("Salmon", Color.fromRGB(250, 128, 114)),
    SANGRIA("Sangria", Color.fromRGB(146, 0, 10)),
    SAPPHIRE("Sapphire", Color.fromRGB(15, 82, 186)),
    SILVER("Silver", Color.fromRGB(192, 192, 192)),
    SLATE_GRAY("Slate gray", Color.fromRGB(112, 128, 144)),
    SPRING_BUD("Spring bud", Color.fromRGB(167, 252, 0)),
    TAN("Tan", Color.fromRGB(210, 180, 140)),
    TAUPE("Taupe", Color.fromRGB(72, 60, 50)),
    TEAL("Teal", Color.fromRGB(0, 128, 128)),
    TURQUOISE("Turquoise", Color.fromRGB(64, 224, 208)),
    VIOLET("Violet", Color.fromRGB(127, 0, 255)),
    VIRIDIAN("Viridian", Color.fromRGB(64, 130, 109)),
    WHITE("White", Color.fromRGB(255, 255, 255)),
    YELLOW("Yellow", Color.fromRGB(255, 255, 0));

    private final String title;
    private final Color color;
}
