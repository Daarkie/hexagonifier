package com.technologicky_andrea.hexagonifier.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class HexagonifierMainController {
    @FXML
    public Button filePickerButton;
    @FXML
    public Button downloadButton;
    @FXML
    public Button hexagonifyButton;
    @FXML
    public ImageView chosenImageView;
    @FXML
    public ImageView hexagonifiedImageView;
    @FXML
    public Spinner<Integer> hexColsSpinner;
    @FXML
    public Spinner<Integer> normalizationLevelSpinner;

    private final HexagonifierTool hexagonifierTool = new HexagonifierTool();
    private String imgFileName;

    @FXML
    public void initialize() {
        // set spinners
        hexColsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200, 50));
        normalizationLevelSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 50, 20));
    }

    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imgFileName = file.getName();
            Image beforeImage = new Image(file.toURI().toString());
            hexagonifierTool.setImageToHexagonify(beforeImage);
            chosenImageView.setImage(beforeImage);
            chosenImageView.setFitHeight(400.0);
            chosenImageView.setFitWidth(400.0);
            chosenImageView.setPreserveRatio(true);
            System.out.println("Before image chosen!");
        }
    }

    @FXML
    private void saveImage(){
        Image hexagonifiedImage = hexagonifiedImageView.getImage();
        if (hexagonifiedImage == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("hexagonified-" + imgFileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(hexagonifiedImage, null), "png", file);
                System.out.println("Hexagonified image saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void hexagonify(){
        if (!hexagonifierTool.hasImageToHexagonify()) {
            return;
        }
        hexagonifierTool.setCols(hexColsSpinner.getValue());
        hexagonifierTool.setNormalizationLevel(normalizationLevelSpinner.getValue());
        Image hexagonifiedImage = hexagonifierTool.hexagonify();
        hexagonifiedImageView.setImage(hexagonifiedImage);
        hexagonifiedImageView.setFitWidth(400.0);
        hexagonifiedImageView.setFitHeight(400.0);
        hexagonifiedImageView.setPreserveRatio(true);
        System.out.println("Image hexagonified!");
    }
}
