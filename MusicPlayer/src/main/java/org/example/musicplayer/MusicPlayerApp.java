package org.example.musicplayer;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;

public class MusicPlayerApp extends Application {

    private MediaPlayer mediaPlayer;
    private final List<File> playlist = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    private final Preferences prefs = Preferences.userNodeForPackage(MusicPlayerApp.class);
    private static final String PREF_LAST_DIR = "last_music_path";
    private static final String PREF_VOLUME = "volume";

    private Label nowPlayingLabel = new Label("🎶 No file playing");
    private Slider seekSlider = new Slider();
    private Slider volumeSlider = new Slider(0, 1, 0.5);

    @Override
    public void start(Stage primaryStage) {
        // Buttons
        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");

        Button prevButton = new Button("Previous");
        Button nextButton = new Button("Next");
        Button shuffleButton = new Button("Shuffle");
        Button repeatButton = new Button("Repeat");

        Button loadFileButton = new Button("Load File");
        Button loadFolderButton = new Button("Load Folder");

        // Apply common button styles
        String buttonStyle = "-fx-background-radius: 10; -fx-font-size: 14px; -fx-padding: 6 12 6 12;";
        for (Button btn : List.of(playButton, pauseButton, stopButton, prevButton, nextButton, shuffleButton, repeatButton, loadFileButton, loadFolderButton)) {
            btn.setStyle(buttonStyle);
        }

        nowPlayingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        // Layout
        HBox controlButtons = new HBox(10, playButton, pauseButton, stopButton);
        HBox navButtons = new HBox(10, prevButton, nextButton, shuffleButton, repeatButton);
        HBox loadButtons = new HBox(10, loadFileButton, loadFolderButton);

        seekSlider.setPrefWidth(300);
        HBox seekBox = new HBox(10, new Label("Seek:"), seekSlider);
        seekBox.setAlignment(Pos.CENTER);
        seekBox.setPadding(new Insets(10));

        volumeSlider.setPrefWidth(150);
        HBox volumeBox = new HBox(10, new Label("Volume:"), volumeSlider);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.setPadding(new Insets(10));

        controlButtons.setAlignment(Pos.CENTER);
        navButtons.setAlignment(Pos.CENTER);
        loadButtons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, nowPlayingLabel, controlButtons, navButtons, loadButtons, seekBox, volumeBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e1e1e;");

        Scene scene = new Scene(root, 500, 350);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Music Player made-by Me");
        primaryStage.show();

        // Load preferences
        volumeSlider.setValue(prefs.getDouble(PREF_VOLUME, 0.5));
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
            prefs.putDouble(PREF_VOLUME, newVal.doubleValue());
        });

        // Load last file or folder
        String lastPath = prefs.get(PREF_LAST_DIR, null);
        if (lastPath != null) {
            File file = new File(lastPath);
            if (file.exists()) {
                if (file.isFile()) {
                    playlist.clear();
                    playlist.add(file);
                    currentIndex = 0;
                    playSong(file);
                } else if (file.isDirectory()) {
                    File[] files = file.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a"));
                    if (files != null && files.length > 0) {
                        playlist.clear();
                        playlist.addAll(Arrays.asList(files));
                        currentIndex = 0;
                        playSong(playlist.get(currentIndex));
                    }
                }
            }
        }

        // Event Handlers
        playButton.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.play();
        });

        pauseButton.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.pause();
        });

        stopButton.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.stop();
        });

        prevButton.setOnAction(e -> playPrevious());
        nextButton.setOnAction(e -> playNext());

        shuffleButton.setOnAction(e -> {
            isShuffle = !isShuffle;
            shuffleButton.setText(isShuffle ? "Shuffle: ON" : "Shuffle");
        });

        repeatButton.setOnAction(e -> {
            isRepeat = !isRepeat;
            repeatButton.setText(isRepeat ? "Repeat: ON" : "Repeat");
        });

        loadFileButton.setOnAction(e -> loadSingleFile(primaryStage));
        loadFolderButton.setOnAction(e -> loadFolder(primaryStage));

        seekSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
            }
        });
    }

    private void loadSingleFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Audio File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            prefs.put(PREF_LAST_DIR, file.getAbsolutePath());
            playlist.clear();
            playlist.add(file);
            currentIndex = 0;
            playSong(file);
        }
    }

    private void loadFolder(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Music Folder");

        File folder = chooser.showDialog(stage);
        if (folder != null && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a"));
            if (files != null && files.length > 0) {
                prefs.put(PREF_LAST_DIR, folder.getAbsolutePath());
                playlist.clear();
                playlist.addAll(Arrays.asList(files));
                currentIndex = 0;
                playSong(playlist.get(currentIndex));
            }
        }
    }

    private void playSong(File file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        nowPlayingLabel.setText("🎶 Playing: " + file.getName());
        mediaPlayer.setVolume(volumeSlider.getValue());
        mediaPlayer.setOnEndOfMedia(() -> {
            if (isRepeat) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            } else {
                playNext();
            }
        });

        mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
            if (media.getDuration().greaterThan(Duration.ZERO)) {
                seekSlider.setMax(media.getDuration().toSeconds());
                seekSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
            }
        });

        mediaPlayer.play();
    }

    private void playNext() {
        if (playlist.isEmpty()) return;
        currentIndex = isShuffle ? new Random().nextInt(playlist.size()) : (currentIndex + 1) % playlist.size();
        playSong(playlist.get(currentIndex));
    }

    private void playPrevious() {
        if (playlist.isEmpty()) return;
        currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        playSong(playlist.get(currentIndex));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
