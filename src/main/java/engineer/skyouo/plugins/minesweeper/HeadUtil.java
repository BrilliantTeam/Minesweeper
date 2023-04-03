package engineer.skyouo.plugins.minesweeper;

import net.kyori.adventure.text.format.TextColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HeadUtil {
    public static TextColor[][] UNKNOWN;
    public static TextColor[][] FLAGGED;
    public static TextColor[][] ZERO;
    public static TextColor[][] ONE;
    public static TextColor[][] TWO;
    public static TextColor[][] THREE;
    public static TextColor[][] FOUR;
    public static TextColor[][] FIVE;
    public static TextColor[][] SIX;
    public static TextColor[][] SEVEN;
    public static TextColor[][] EIGHT;
    public static TextColor[][] TNT;

    private HeadUtil() {}

    public static void init() {
        try {
            UNKNOWN = getSkinFromTexture("c7f1ea26ea5e685b2f2f8764901be914fe35559cb1ecb1ef34b7e46abc8ee540");
            FLAGGED = getSkinFromTexture("784a7fcb247406e353a36e556ad19578c3ebe4e15581db106d15a5cb9dad");
            ZERO = get2LayerSkinFromTexture("74c284a4e974005ea8d1d4d0674ec0894efd8f6dd0248639a6cfa94f85388");
            ONE = get2LayerSkinFromTexture("67fac71e36d50a1ad2e2c10e32e0c59eedefb0d35446a8fb4848138fe26fc9");
            TWO = get2LayerSkinFromTexture("b4968955bd948708041e12b164adfcdb46399c2d381059ed71ce74ae24f8df");
            THREE = get2LayerSkinFromTexture("1f678cfcc4eeb259d8e57b26f4f6a35a76d7437043be6c23ae855c47c8a2e9");
            FOUR = get2LayerSkinFromTexture("bb6971abb18bca9fafaad9ed5a1596132beccffb88a31c829320c867ee477");
            FIVE = get2LayerSkinFromTexture("c6206e47614c8f983413dee332f2f32e8da37fa57c4ceba1d14b1643b25957");
            SIX = get2LayerSkinFromTexture("66f97e563d85dc4d73871d4cdfcc26d8cd44e89fafb1504c8d9a2ac5a56c");
            SEVEN = get2LayerSkinFromTexture("87ef6185add419735793c8c2a847d9c4e391a2c5b9b2ec262cea95575b0d0");
            EIGHT = get2LayerSkinFromTexture("8271cdd38e8a7c74231af8a155618f4ffcb7f917e8826c2b3c1836d1bd116d3");
            TNT = get2LayerSkinFromTexture("3af59776f2f03412c7b5947a63a0cf283d51fe65ac6df7f2f882e08344565e9");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextColor[][] getSkinFromTexture(String textureId) throws IOException {
        TextColor[][] playerHead = new TextColor[8][8];

        BufferedImage image = ImageIO.read(
                        new URL(String.format("https://textures.minecraft.net/texture/%s", textureId))
        );

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int rgb = image.getRGB(x + 40, y);
                playerHead[y][x] = TextColor.color(rgb);
            }
        }

        return playerHead;
    }

    public static TextColor[][] get2LayerSkinFromTexture(String textureId) throws IOException {
        TextColor[][] playerHead = new TextColor[8][8];

        BufferedImage image = accessCacheOrRtAccess(textureId);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int rgb = image.getRGB(x + 40, y) != 0 ? image.getRGB(x + 40, y) : image.getRGB(x + 8, y);
                playerHead[y][x] = TextColor.color(rgb);
            }
        }

        return playerHead;
    }

    private static BufferedImage accessCacheOrRtAccess(String textureId) throws IOException {
        File cachedFile = new File("plugins/Minesweeper/" + textureId + ".png");
        if (cachedFile.exists()) {
            return ImageIO.read(cachedFile);
        } else {
            BufferedImage image = ImageIO.read(
                    new URL(String.format("https://textures.minecraft.net/texture/%s", textureId))
            );
            new File("plugins/Minesweeper").mkdirs();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            Files.write(cachedFile.toPath(), output.toByteArray());

            return image;
        }
    }
}
