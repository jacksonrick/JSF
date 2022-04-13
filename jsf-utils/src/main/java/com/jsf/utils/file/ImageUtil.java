package com.jsf.utils.file;

import com.jsf.utils.exception.SysException;
import com.jsf.utils.string.StringUtil;
import com.jsf.utils.system.LogManager;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 图片工具类
 * Created by xujunfei on 2017/6/7.
 */
public class ImageUtil {

    /**
     * HTML转图片
     * <p>可以由freemarker等模版引擎输出</p>
     * <p>格式为png</p>
     *
     * @param html
     * @param output 输出路径，以/结尾
     * @param width  图片宽
     * @param height 图片高
     * @return
     */
    public static String htmlToImage(String html, String output, int width, int height) {
        if (StringUtil.isBlank(html)) {
            return "";
        }
        try {
            byte[] bytes = html.getBytes();
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(bin);
            Java2DRenderer renderer = new Java2DRenderer(document, width, height);
            BufferedImage img = renderer.getImage();
            String filepath = output + StringUtil.randomFilename() + ".png";
            ImageIO.write(img, "png", new File(filepath));
            return filepath;
        } catch (Exception e) {
            LogManager.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 添加图片水印
     *
     * @param stream
     * @param ext
     * @return
     */
    public static byte[] waterMark(InputStream stream, String ext) {
        byte[] b = null;
        try {
            // 读取原图片信息
            Image srcImg = ImageIO.read(stream);
            int width = srcImg.getWidth(null);
            int height = srcImg.getHeight(null);
            // 加水印
            BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, width, height, null);
            // Logo(需要在resources放置logo图片)
            Image imageLogo = ImageIO.read(ImageUtil.class.getClassLoader().getResourceAsStream("logo.png"));
            int width1 = imageLogo.getWidth(null);
            int height1 = imageLogo.getHeight(null);

            // 最小
            if (width <= 150) width1 = 40;
            if (height <= 150) height1 = 16;

            int x = width - width1 - 5;
            int y = height - height1 - 5;

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8F));
            g.drawImage(imageLogo, x, y, width1, height1, null);
            g.dispose();
            // 输出图片
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufImg, ext, out);
            b = out.toByteArray();
            out.flush();
            out.close();
            return b;
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
    }

    /**
     * 添加文字水印
     *
     * @param stream
     * @param content
     * @param ext
     * @return
     */
    public static byte[] waterMark(InputStream stream, String content, String ext) {
        byte[] b = null;
        try {
            // 读取原图片信息
            Image srcImg = ImageIO.read(stream);
            int width = srcImg.getWidth(null);
            int height = srcImg.getHeight(null);
            // 加水印
            BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, width, height, null);

            Font font = new Font("Default", Font.PLAIN, 50);
            g.setColor(Color.WHITE);
            g.setFont(font);
            int x = width - getWatermarkLength(content, g) - 8;
            int y = height - 8;
            g.drawString(content, x, y);
            g.dispose();
            // 输出图片
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufImg, ext, out);
            b = out.toByteArray();
            out.flush();
            out.close();
            return b;
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
    }

    private static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

}
