package exclude.old;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import misc.FolderUtils;

public class AggregateDraw {

    final static String q = File.separator;

    public static void drawC(int x, int y, int len, BufferedImage image) {
        for (int p = 0; p < len; p++) {
            image.setRGB(x + (len / 2), y + p, 0x808080);
            image.setRGB(x + p, y + (len / 2), 0x808080);
        }
    }

    public static void main(String[] args) throws IOException {

        String src = "result" + q + "experiments.TestExp" + q + "init2" + q;
        String dst = FolderUtils.openOrCreate();
        BufferedImage black = ImageIO.read(new File("black.png"));
        File folder = new File(src);

        int border = 10;
        int len = 400;
        int num = 5;

        for (File subFolder : folder.listFiles()) {
            try {

                System.out.println(subFolder);
                int w = num * (len + border) + border;
                int h = len + 2 * border;

                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        image.setRGB(x, y, 0xFFFFFF);
                    }
                }

                Graphics2D graphics = (Graphics2D) image.getGraphics();

                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                int x = border, y = border;
                {
                    graphics.drawImage(black, x - 1, y - 1, len + 2, len + 2, null);
                    BufferedImage img = ImageIO.read(new File(subFolder + q + "gen" + ".png"));
                    graphics.drawImage(img, x, y, len, len, null);
                    drawC(x, y, len, image);
                    x += border + len;
                }
                {
                    graphics.drawImage(black, x - 1, y - 1, len + 2, len + 2, null);
                    BufferedImage img = ImageIO.read(new File(subFolder + q + "init" + ".png"));
                    graphics.drawImage(img, x, y, len, len, null);
                    drawC(x, y, len, image);
                    x += border + len;
                }
                {
                    graphics.drawImage(black, x - 1, y - 1, len + 2, len + 2, null);
                    BufferedImage img = ImageIO.read(new File(subFolder + q + "step0" + ".png"));
                    graphics.drawImage(img, x, y, len, len, null);
                    drawC(x, y, len, image);
                    x += border + len;
                }
                {
                    graphics.drawImage(black, x - 1, y - 1, len + 2, len + 2, null);
                    BufferedImage img = ImageIO.read(new File(subFolder + q + "step3" + ".png"));
                    graphics.drawImage(img, x, y, len, len, null);
                    drawC(x, y, len, image);
                    x += border + len;
                }
                {
                    graphics.drawImage(black, x - 1, y - 1, len + 2, len + 2, null);
                    BufferedImage img = ImageIO.read(new File(subFolder + q + "step11" + ".png"));
                    graphics.drawImage(img, x, y, len, len, null);
                    drawC(x, y, len, image);
                    x += border + len;
                }
                ImageIO.write(image, "png", new File(dst + subFolder.getName() + ".png"));
            } catch (IOException exception) {
                System.err.println(exception);
            }
            // break;
        }

    }
}
