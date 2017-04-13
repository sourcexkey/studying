package com.xkey.webcam;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class WebCamTest {
    public static void main(String[] args) throws IOException {
        WebCamTest webCamTest = new WebCamTest();
        //webCamTest.takePicture();
        //webCamTest.record();
        webCamTest.streamVideo();
    }

    public void streamVideo() throws MalformedURLException {
        Webcam.setDriver(new IpCamDriver());


        IpCamDeviceRegistry.register("Lignano", "http://localhost:8083/mjpg/video.mjpg", IpCamMode.PUSH);

        WebcamPanel panel = new WebcamPanel(Webcam.getWebcams().get(0));
        panel.setFPSLimit(1);

        JFrame f = new JFrame("Live Views From Lignano Beach");
        f.add(panel);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void record() throws IOException {

        File file = new File("output.ts");

        IMediaWriter writer = ToolFactory.makeWriter(file.getName());
        Dimension size = WebcamResolution.QVGA.getSize();

        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, size.width, size.height);

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.open(true);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 330; i++) {

            System.out.println("Capture frame " + i);

            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
            frame.setKeyFrame(i == 0);
            frame.setQuality(0);

            writer.encodeVideo(0, frame);

            // 10 FPS
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        writer.close();

        System.out.println("Video recorded in file: " + file.getAbsolutePath());
    }

    public void takePicture() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));
    }
}
