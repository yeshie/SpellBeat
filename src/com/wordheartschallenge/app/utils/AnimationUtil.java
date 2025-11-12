package com.wordheartschallenge.app.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;

public class AnimationUtil {

    // Shake effect for wrong guesses
	 public static void shake(Node node) {
	        TranslateTransition shake = new TranslateTransition(Duration.millis(100), node);
	        shake.setByX(10);
	        shake.setCycleCount(6);
	        shake.setAutoReverse(true);
	        shake.play();
	    }
	    // Pulse effect for low hearts (infinite until stopped)
	    public static void pulse(Node node) {
	        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), node);
	        pulse.setToX(1.2);
	        pulse.setToY(1.2);
	        pulse.setCycleCount(Animation.INDEFINITE);
	        pulse.setAutoReverse(true);
	        pulse.play();
	    }
	    // Stop pulse (call when hearts increase)
	    public static void stopPulse(Node node) {
	        node.setScaleX(1.0);
	        node.setScaleY(1.0);
	    }
	    // Fade-in effect (optional, for hints/definitions)
	    public static void fadeIn(Node node) {
	        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
	        fade.setFromValue(0.0);
	        fade.setToValue(1.0);
	        fade.play();
	    }
}