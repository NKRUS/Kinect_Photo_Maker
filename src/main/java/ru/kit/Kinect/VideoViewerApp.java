package ru.kit.Kinect;

import edu.ufl.digitalworlds.gui.DWApp;
import edu.ufl.digitalworlds.j4k.J4KSDK;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;

/**
 * Created by mikha on 12.01.2017.
 */
public class VideoViewerApp extends DWApp {

    Kinect myKinect;
    VideoPanel main_panel;

    JButton btnPhotoSagittal;
    JButton btnContour;
    JButton btnContinue;
    JButton btnPhotoBack;

    static String path;



    public void GUIsetup(JPanel p_root) {

        if (System.getProperty("os.arch").toLowerCase().indexOf("64") < 0) {
            if (DWApp.showConfirmDialog("Performance Warning", "<html><center><br>WARNING: You are running a 32bit version of Java.<br>This may reduce significantly the performance of this application.<br>It is strongly adviced to exit this program and install a 64bit version of Java.<br><br>Do you want to exit now?</center>"))
                System.exit(0);
        }

        setLoadingProgress("Intitializing Kinect...", 20);
        myKinect = new Kinect();
        if (!myKinect.start(J4KSDK.COLOR)) {
            DWApp.showErrorDialog("ERROR", "<html><center><br>ERROR: The Kinect device could not be initialized.<br><br>1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.<br> 2. Check if the Kinect is plugged into a power outlet.<br>3. Check if the Kinect is connected to a USB port of this computer.</center>");
            //System.exit(0);
        }

        JPanel controls = new JPanel(new GridLayout(0, 4));
        btnPhotoBack = new JButton("Фото спины");
        btnPhotoBack.addActionListener(this);
        controls.add(btnPhotoBack);

        btnPhotoSagittal = new JButton("Фото сбоку");
        btnPhotoSagittal.addActionListener(this);
        controls.add(btnPhotoSagittal);

//        btnContour = new JButton("Обвести");
//        btnContour.addActionListener(this);
//        controls.add(btnContour);

        btnContinue = new JButton("Продолжить");
        btnContinue.addActionListener(this);
        controls.add(btnContinue);


        setLoadingProgress("Intitializing OpenGL...", 60);
        main_panel = new VideoPanel();
        myKinect.setViewer(main_panel);
        p_root.add(main_panel, BorderLayout.CENTER);
        p_root.add(controls, BorderLayout.SOUTH);
    }

    public void GUIclosing() {
        myKinect.stop();
    }

    public static int STREAM_WIDTH = 1920;
    public static int STREAM_HEIGHT = 1080;

    public static void main(String args[]) {
        //System.out.println("\u041f\u043e\u0434\u0445\u043e\u0434\u044f\u0449\u0438\u0439 \u043c\u0430\u0440\u0448\u0440\u0443\u0442 \u0434\u043b\u044f URL \u0438 \u043c\u0435\u0442\u043e\u0434\u0430 \u0437\u0430\u043f\u0440\u043e\u0441\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
        startKinect();

        if (args.length > 0) {
            System.out.println(args[0]);
            VideoPanel.setPath(args[0]);

        }


    }

    public static void startKinect() {
        createMainFrame("Video Viewer App");
        app = new VideoViewerApp();
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = sSize.width - 300;
        int height = width * STREAM_HEIGHT / STREAM_WIDTH;

        //setFrameSize(width, height, null);
        setFrameSize(568, 1000, null);
    }


    @Override
    public void GUIactionPerformed(ActionEvent e) {
        if (e.getSource() == btnPhotoSagittal || e.getSource() == btnPhotoBack) {
            VideoPanel.isBack = e.getSource() == btnPhotoBack;
            VideoPanel.flag132 = true;
            myKinect.freezeFrame();
        } else if (e.getSource() == btnContinue) {
            myKinect.continueFrame();
        } else {
            myKinect.makeContour();
        }
    }

}
