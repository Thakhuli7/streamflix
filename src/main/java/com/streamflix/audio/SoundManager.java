package com.streamflix.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

/*
 * Creates and plays small in-app sound effects without external audio files.
 */
public final class SoundManager {

    // Every effect is a short group of musical notes.
    public enum Effect {
        HOVER(new double[][]{{1250, 40, 0.12}}),
        CLICK(new double[][]{{660, 55, 0.30}, {990, 90, 0.30}}),
        OPEN(new double[][]{{98, 260, 0.55}, {147, 700, 0.55}}),
        BACK(new double[][]{{784, 60, 0.28}, {523, 100, 0.28}});

        private final double[][] notes;

        Effect(double[][] notes) {
            this.notes = notes;
        }
    }

    private static final double SAMPLE_RATE = 22050.0;
    private static final SoundManager INSTANCE = new SoundManager();

    private final Map<Effect, Clip> clips = new EnumMap<>(Effect.class);
    private boolean muted;

    private SoundManager() {
        for (Effect effect : Effect.values()) {
            try {
                byte[] pcm = synthesize(effect.notes);
                Clip clip = AudioSystem.getClip();
                clip.open(new AudioFormat((float) SAMPLE_RATE, 16, 1, true, false), pcm, 0, pcm.length);
                clips.put(effect, clip);
            } catch (Exception | Error ex) {
                
            }
        }
    }

    public static SoundManager getInstance() {
        return INSTANCE;
    }

    public void play(Effect effect) {
        if (muted) {
            return;
        }
        Clip clip = clips.get(effect);
        if (clip == null) {
            return;
        }
        try {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } catch (RuntimeException ignored) {
            
        }
    }

    
    public boolean toggleMuted() {
        muted = !muted;
        return muted;
    }

    public boolean isMuted() {
        return muted;
    }

    
    private static byte[] synthesize(double[][] notes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (double[] note : notes) {
            int count = (int) (SAMPLE_RATE * note[1] / 1000.0);
            for (int i = 0; i < count; i++) {
                double t = i / SAMPLE_RATE;
                double attack = Math.min(1.0, i / (SAMPLE_RATE * 0.004)); 
                double decay = Math.exp(-3.5 * i / count);                 
                double value = Math.sin(2 * Math.PI * note[0] * t) * attack * decay * note[2];
                short sample = (short) (value * Short.MAX_VALUE);
                out.write(sample & 0xFF);
                out.write((sample >> 8) & 0xFF);
            }
        }
        return out.toByteArray();
    }
}
