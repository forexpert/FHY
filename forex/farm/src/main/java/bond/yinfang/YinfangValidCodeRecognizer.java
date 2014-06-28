package bond.yinfang;

import com.mrj.research.textrecognition.ValidCodeRecogizer;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by I074995 on 6/24/2014.
 */
public class YinfangValidCodeRecognizer implements ValidCodeRecogizer {
    static Logger logger = Logger.getLogger(YinfangValidCodeRecognizer.class);
    private static String DEBUG_PATH = "C:\\Users\\I074995\\temp\\";
    private static String DEBUG_ORIGINAL_IMAGE = DEBUG_PATH + "test\\original.png";
    private static String DEBUG_PREPREPARE_IMAGE = DEBUG_PATH + "test\\prePrepare.png";
    private static String DEBUG_SPLIT_IMAGE = "test\\split";
    private static String DEBUG_SPLIT_IMAGE_SUFFIX = ".png";

    private Map<Character, List<BufferedImage>> trainMap = null;

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new FileInputStream(DEBUG_PATH + "yinfang\\training\\nsai4.jpg"));
        YinfangValidCodeRecognizer recognizer = new YinfangValidCodeRecognizer();
        String testStrting = recognizer.recognize(originalImage);
        logger.info("The validation code is " + testStrting);
    }

    @Override

    public String recognize(byte[] image) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(image));
        return recognize(originalImage);
    }


    /**
     * 图片预处理，分割，训练，识别
     */
    @Override
    public String recognize(BufferedImage originalImage) throws IOException {
        logImage(originalImage, DEBUG_ORIGINAL_IMAGE);
        BufferedImage prePrepareImage = prePrepare(originalImage);
        logImage(prePrepareImage, DEBUG_PREPREPARE_IMAGE);
        List<BufferedImage> imageSnippets = splitImage(prePrepareImage);
        logImages(imageSnippets, DEBUG_SPLIT_IMAGE, DEBUG_SPLIT_IMAGE_SUFFIX);
        loadTrainData();
        return recoginzeOneByOne(imageSnippets);
    }

    private String recoginzeOneByOne(List<BufferedImage> imageSnippets) {
        String re = "";
        for(BufferedImage image: imageSnippets){
            re += recoginzeOne(image);
        }
        return re;
    }

    private String recoginzeOne(BufferedImage image){

        double maxSimilarity = 0;
        Character possibleChar = null;
        for(Character c : trainMap.keySet()){
            List<BufferedImage> cImages  = trainMap.get(c);
            for(BufferedImage libImage : cImages){
                double newSimilarity = similarity(image, libImage);
                if(newSimilarity > maxSimilarity){
                    maxSimilarity = newSimilarity;
                    possibleChar = c;
                }
            }
        }
        if(maxSimilarity > 0.5){
            return possibleChar.toString();
        } else {
            return "";
        }
    }

    /**
     * return a double value as similarity.  1 means identical ; 0 means totally different
     * @param image
     * @param libImage
     * @return
     */
    private double similarity(BufferedImage image, BufferedImage libImage) {
        int width = Math.min(libImage.getWidth(), image.getWidth());
        int height =  Math.min(libImage.getHeight(), image.getHeight());
        int sameCount = 0;
        int diffCount = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isWhite(image.getRGB(x, y)) != isWhite(libImage.getRGB(x, y))) {
                    diffCount++;
                } else {
                    sameCount ++;
                }
            }
        }
        return sameCount/(1.0* width * height);
    }

    /**
     * load training data
     *
     * @return
     * @throws Exception
     */
    private Map<Character, List<BufferedImage>> loadTrainData() {
        if (trainMap == null) {
            trainMap = new HashMap<Character, List<BufferedImage>>();
            File dir = new File("C:\\Users\\I074995\\temp\\yinfang\\training");
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    BufferedImage originalImage = null;
                    try {
                        originalImage = ImageIO.read(file);
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                    BufferedImage prePrepareImage = prePrepare(originalImage);
                    List<BufferedImage> imageSnippets = splitImage(prePrepareImage);
                    char[] chars = file.getName().split("\\.")[0].toCharArray();
                    if (chars.length == imageSnippets.size()) {
                        for (int i = 0; i < chars.length; i++) {
                            List<BufferedImage> images = trainMap.get(chars[i]);
                            if (images == null) {
                                images = new ArrayList<BufferedImage>();
                                trainMap.put(chars[i], images);
                            }
                            images.add(imageSnippets.get(i));
                        }
                    }
                }
            }
        }
        return trainMap;
    }


    public List<BufferedImage> splitImage(BufferedImage img) {
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        int width = img.getWidth();
        int height = img.getHeight();
        List<Integer> weightlist = new ArrayList<Integer>();
        for (int x = 0; x < width; ++x) {
            int count = 0;
            for (int y = 0; y < height; ++y) {
                if (isBlack(img.getRGB(x, y)) == 1) {
                    count++;
                }
            }
            weightlist.add(count);
        }
        int min_letter_width = 3;
        int doulbe_letter_width = 25;
        for (int i = 0; i < weightlist.size(); ) {
            int length = 0;
            while (weightlist.get(i++) > 0 && i <  weightlist.size()) {
                length++;
            }
            if (length > doulbe_letter_width) {
                subImgs.add(removeUpperBottomEmpty(img.getSubimage((i - 1) - length, 0,
                        length / 2, height)));
                subImgs.add(removeUpperBottomEmpty(img.getSubimage((i - 1) - length + length / 2, 0,
                        length / 2, height)));
            } else {
                if (length > min_letter_width) {
                    subImgs.add(removeUpperBottomEmpty(img.getSubimage((i - 1) - length, 0,
                            length, height)));
                }
            }

        }
        return subImgs;
    }

    public BufferedImage removeUpperBottomEmpty(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int start = 0;
        int end = 0;
        boolean setStart = false;
        boolean setEnd = false;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (isBlack(img.getRGB(x, y)) == 1) {
                    start = y;
                    setStart = true;
                    break;
                }
            }
            if (setStart) break;
        }
        for (int y = height - 1; y >= 0; --y) {
            for (int x = 0; x < width; ++x) {
                if (isBlack(img.getRGB(x, y)) == 1) {
                    end = y;
                    setEnd = true;
                    break;
                }
            }
            if (setEnd) break;
        }
        return img.getSubimage(0, start, width, end - start + 1);
    }

    private void logImage(BufferedImage image, String name) {
        try {
            ImageIO.write(image, "png", new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logImages(List<BufferedImage> imageSnippets, String debug_split_image, String suffix) {
        int i = 0;
        for (BufferedImage image : imageSnippets) {
            logImage(image, DEBUG_PATH + debug_split_image + "m" + (i++) + suffix);
        }
    }

    /**
     * removeBackgroud
     *
     * @param img image
     * @return BufferedImage
     */
    private BufferedImage prePrepare(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isWhite(img.getRGB(x, y)) == 1) {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    private int isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 500) {
            return 1;
        }
        return 0;
    }

    private int isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 500) {
            return 1;
        }
        return 0;
    }

}
