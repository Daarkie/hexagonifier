package com.technologicky_andrea.hexagonifier.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class HexagonifierTool {
    private Image imageToHexagonify = null;
    private int cols;
    private int rows;
    private int widthOut;
    private double normalizationLevelRemainder;

    private double normalize(double colorComponent) {
        int leveled = (int) (colorComponent / normalizationLevelRemainder);
        return leveled * normalizationLevelRemainder;
    }

    private javafx.scene.paint.Color normalizeColor(javafx.scene.paint.Color color) {
        double red = normalize(color.getRed());
        double green = normalize(color.getGreen());
        double blue = normalize(color.getBlue());
        double alpha = normalize(color.getOpacity());
        return new javafx.scene.paint.Color(red, green, blue, alpha);
    }

    private Color[][] predominantColors() {
        // get hexagon dims and number or rows
        int widthIn = (int) (imageToHexagonify.getWidth() / (0.75 * cols + 0.25));
        widthIn = Math.max(widthIn, 1);
        widthOut = Math.min(widthIn, 4);
        int heightIn = (int) (Math.sqrt(3) / 2.0 * widthIn);
        heightIn = Math.max(heightIn, 1);
        rows = (int) (imageToHexagonify.getHeight() / heightIn + 0.5);
        rows = Math.max(rows, 1);

        PixelReader pixR = imageToHexagonify.getPixelReader();
        // store colors per coordinate
        javafx.scene.paint.Color[][] coordinatedColors = new javafx.scene.paint.Color[cols][rows];

        //  iterate through the coordinates
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Map<javafx.scene.paint.Color, Integer> normalizedColors = new HashMap<>();

                // iterate through the pixels inside
                for (int x = 0; x < widthIn; x++) {
                    for (int y = 0; y < heightIn; y++) {
                        // ignore triangles
                        int triangleX = (x * 2 > widthIn ? widthIn - x : x) * 2;
                        int triangleY = y * 2 > heightIn ? heightIn - y : y;
                        int extraTriangle = (widthIn + heightIn) / 4;
                        if (triangleY + triangleX < extraTriangle) {
                            continue;
                        }

                        // make hashmap to count frequency of colors
                        javafx.scene.paint.Color leveledColor = normalizeColor(pixR.getColor(x, y));
                        if (normalizedColors.containsKey(leveledColor)) {
                            int freq = normalizedColors.get(leveledColor);
                            normalizedColors.put(leveledColor, ++freq);
                        } else {
                            normalizedColors.put(leveledColor, 1);
                        }
                    }
                }

                // find the most common color and add it to the coordinatedColors
                int freq = -1;
                for (Map.Entry<javafx.scene.paint.Color, Integer> colorEntry : normalizedColors.entrySet()){
                    if (freq < colorEntry.getValue()) {
                        freq = colorEntry.getValue();
                        coordinatedColors[c][r] = colorEntry.getKey();
                    }
                }
            }
        }
        return coordinatedColors;
    }

    public Image hexagonify() {
        // get predominant colors per coordinates
        javafx.scene.paint.Color[][] coorColors = predominantColors();

        // create an image with appropriate dims
        int heightOut = (int) (Math.sqrt(3) / 2.0 * widthOut);
        int imgWidth = (int) Math.ceil(cols * widthOut * 0.75 + cols * 0.25);
        int imgHeight = (int) Math.ceil(rows == 1 ? heightOut : rows * heightOut + 0.5 * heightOut);
        WritableImage wImage = new WritableImage(imgWidth, imgHeight);
        PixelWriter pixW = wImage.getPixelWriter();
        return wImage;
    }

    public boolean hasImageToHexagonify() {
        return imageToHexagonify != null;
    }

    public void setImageToHexagonify(Image imageToHexagonify) {
        this.imageToHexagonify = imageToHexagonify;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setNormalizationLevel(int normalizationLevel) {
        this.normalizationLevelRemainder = 1.0 / normalizationLevel;
    }
}
