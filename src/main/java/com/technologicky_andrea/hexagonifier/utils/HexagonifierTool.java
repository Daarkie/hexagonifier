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
    private double widthOut;
    private double normalizationLevelRemainder;

    private double normalize(double colorComponent) {
        int leveled = (int) Math.round(colorComponent / normalizationLevelRemainder);
        return leveled * normalizationLevelRemainder;
    }

    private javafx.scene.paint.Color normalizeColor(javafx.scene.paint.Color coloredPixel) {
        double red = normalize(coloredPixel.getRed());
        double green = normalize(coloredPixel.getGreen());
        double blue = normalize(coloredPixel.getBlue());
        double alpha = normalize(coloredPixel.getOpacity());
        return new javafx.scene.paint.Color(red, green, blue, alpha);
    }

    private javafx.scene.paint.Color[][] predominantColors() {
        // get hexagon dims and number or rows
        double widthIn = imageToHexagonify.getWidth() / (0.75 * cols + 0.25);
        widthIn = Math.max(widthIn, 1);
        widthOut = Math.max(widthIn, 4);
        double heightIn = (Math.sqrt(3.0) / 2.0) * widthIn;
        heightIn = Math.max(heightIn, 1);
        rows = (int) ((imageToHexagonify.getHeight() - heightIn * 0.5) / heightIn);
        rows = Math.max(rows, 1);

        PixelReader pixR = imageToHexagonify.getPixelReader();
        // store colors per coordinate
        javafx.scene.paint.Color[][] coordinatedColors = new javafx.scene.paint.Color[cols][rows];

        //  iterate through the coordinates
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                // make hashmap to count frequency of colors
                Map<Color, Integer> normalizedColors = new HashMap<>();

                // iterate through the pixels inside
                for (int x = 0; x < widthIn; x++) {
                    for (int y = 0; y < heightIn; y++) {
                        // ignore triangles
                        int triangleX = (int) (x * 2 > widthIn ? widthIn - x : x) * 2;
                        int triangleY = y * 2 > heightIn ? (int) heightIn - y : y;
                        int extraTriangle = (int) (widthIn + heightIn) / 4;
                        if (triangleY + triangleX < extraTriangle) {
                            continue;
                        }

                        int coorX = x + (int) (c * widthIn * 0.75);
                        int coorY = y + (int) (r * heightIn);
                        coorY = c % 2 == 0 ? coorY : coorY + (int) (heightIn * 0.5);

                        Color color = pixR.getColor(coorX, coorY);
                        javafx.scene.paint.Color leveledColor = normalizeColor(color);
                        normalizedColors.put(leveledColor, normalizedColors.getOrDefault(leveledColor, 0) + 1);
                    }
                }

                // find the most common color and add it to the coordinatedColors
                int freq = -1;
                for (Map.Entry<Color, Integer> colorEntry : normalizedColors.entrySet()){
                    if (freq < colorEntry.getValue()) {
                        freq = colorEntry.getValue();
                        coordinatedColors[c][r]  = colorEntry.getKey();
                    }
                }
                normalizedColors.clear();
            }
        }
        return coordinatedColors;
    }

    public Image hexagonify() {
        // get predominant colors per coordinates
        javafx.scene.paint.Color[][] coorColors = predominantColors();

        // create an image with appropriate dims
        double heightOut = (Math.sqrt(3.0) / 2.0) * widthOut;
        int imgWidth = (int) Math.ceil(cols * widthOut * 0.75 + widthOut * 0.25);
        int imgHeight = (int) Math.ceil(rows == 1 ? heightOut : rows * heightOut + 0.5 * heightOut);

        System.out.println("Image dims: " + imgWidth + " x " + imgHeight);
        WritableImage wImage = new WritableImage(imgWidth, imgHeight);

        // paint hexagons on image
        PixelWriter pixW = wImage.getPixelWriter();
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Color hexColor = coorColors[c][r];
                for (int x = 0; x < widthOut; x++) {
                    for (int y = 0; y < heightOut; y++) {
                        // ignore triangles
                        int triangleX = (int) ((x * 2 > widthOut ? widthOut - x : x) * 2);
                        int triangleY = y * 2 > heightOut ? (int) (heightOut - y) : y;
                        int extraTriangle = (int) ((widthOut + heightOut) / 4);
                        if (triangleY + triangleX < extraTriangle) {
                            continue;
                        }
                        int coorX = x + (int) (c * widthOut * 0.75);
                        int coorY = (int) (y + r * heightOut);
                        coorY = c % 2 == 0 ? coorY : coorY + (int) (heightOut * 0.5);
                        pixW.setColor(coorX, coorY, hexColor);
                    }
                }
            }
        }

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
