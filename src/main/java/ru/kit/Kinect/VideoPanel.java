package ru.kit.Kinect;

import com.sun.awt.AWTUtilities;
import edu.ufl.digitalworlds.j4k.VideoFrame;
import edu.ufl.digitalworlds.opengl.OpenGLPanel;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by mikha on 12.01.2017.
 */
public class VideoPanel extends OpenGLPanel {

    private static String path;
    public static final String IMAGE_NAME_BACK = "back_photo";
    public static final String IMAGE_NAME_SAGITTAL = "sagittal_photo";

    VideoFrame videoTexture;
    public static boolean isBack;

    public void setup() {

        //OPENGL SPECIFIC INITIALIZATION (OPTIONAL)
        GL2 gl = getGL2();
        gl.glEnable(GL2.GL_CULL_FACE);
        float light_model_ambient[] = {0.3f, 0.3f, 0.3f, 1.0f};
        float light0_diffuse[] = {0.9f, 0.9f, 0.9f, 0.9f};
        float light0_direction[] = {0.0f, -0.4f, 1.0f, 0.0f};
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_FALSE);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, light_model_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_direction, 0);
        gl.glEnable(GL2.GL_LIGHT0);

        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glColor3f(0.9f, 0.9f, 0.9f);


        videoTexture = new VideoFrame();

        background(0, 0, 0);
    }

    public void draw() {

        GL2 gl = getGL2();
        pushMatrix();


        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glColor3f(1f, 1f, 1f);
        videoTexture.use(gl);
        translate(0, 0, -2.2);
        //rotateZ(180);
        rotateZ(90);//Angle or KINECT Position
        //image(3.9, 2);
        image(2.0, 1.2);

        if (!flag132) {
            drawShape();
        }

        popMatrix();

//		if(count++ == 50)
        if (flag132) {
            saveScreenshot(gl);
            flag132 = false;
        }
    }

    private void drawShape() {
        float y = 0.40f;
        float x = 0.65f;
        float dx = 0.29f;
        GL2 gl = getGL2();
        gl.glColor3f(5,0,0);
        gl.glLineWidth(3);
        //edge1
        gl.glBegin( GL2.GL_LINES );
        gl.glVertex3f(x - dx, y, 0);
        gl.glVertex3f(-x - dx, y, 0);
        gl.glEnd();
        //edge2
        gl.glBegin( GL2.GL_LINES );
        gl.glVertex3f( -x - dx, -y, 0 );
        gl.glVertex3f( x - dx, -y, 0 );
        gl.glEnd();
        //edge3
        gl.glBegin( GL2.GL_LINES );
        gl.glVertex3f( -x - dx, -y, 0 );
        gl.glVertex3f( -x - dx, y, 0 );
        gl.glEnd();
        //edge4
        gl.glBegin( GL2.GL_LINES );
        gl.glVertex3f( x - dx, -y, 0 );
        gl.glVertex3f( x - dx, y, 0 );
        gl.glEnd();


        gl.glFlush();
    }

    public static boolean flag132 = false;

    int count = 0;

    private void saveScreenshot(GL2 gl) {
        // read current buffer
        FloatBuffer imageData = FloatBuffer.allocate(this.getWidth() * this.getHeight() * 3);
        gl.glReadPixels(0, 0, this.getWidth(), this.getHeight(), GL2.GL_RGB, GL2.GL_FLOAT, imageData);
        imageData.rewind();

        // fill rgbArray for BufferedImage
        int[] rgbArray = new int[this.getWidth() * this.getHeight()];
        for (int y = 0; y < this.getHeight(); ++y) {
            for (int x = 0; x < this.getWidth(); ++x) {
                int r = (int) (imageData.get() * 255) << 16;
                int g = (int) (imageData.get() * 255) << 8;
                int b = (int) (imageData.get() * 255);
                int i = ((this.getHeight() - 1) - y) * this.getWidth() + x;
                rgbArray[i] = r + g + b;
            }
        }

        // create and save image
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, this.getWidth(), this.getHeight(), rgbArray, 0, this.getWidth());
//        File dir = new File("screenshots_kinect");
//        dir.mkdir();
        File outputfile = getNextScreenFile();
        try {
            ImageIO.write(image, "png", outputfile);
            last_screen = image;
            System.out.println(outputfile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can not save screenshot!");
        }
    }

    private static BufferedImage last_screen;

    public static BufferedImage getLast_screen() {
        return last_screen;
    }

    private File getNextScreenFile() {
        // create image name
        //String fileName = System.getProperty("user.dir") + "\\screenshots_kinect\\screenshot_" + getSystemTime(false);
        String fileName = path + (isBack ? IMAGE_NAME_BACK : IMAGE_NAME_SAGITTAL);
        File imageToSave = new File(fileName + ".png");

        // check for duplicates
//        int duplicate = 0;
//        while (imageToSave.exists()) {
//            imageToSave = new File(fileName + "_" + ++duplicate + ".png");
//        }

        return imageToSave;
    }

    private static String getSystemTime(boolean getTimeOnly) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getTimeOnly ? "HH-mm-ss" : "yyyy-MM-dd'T'HH-mm-ss"
        );
        return dateFormat.format(new Date());
    }

    public static void setPath(String path) {
        if (path == null) {
            VideoPanel.path = System.getProperty("user.dir") + "\\screenshots_kinect\\";
        } else {
            VideoPanel.path = path;
        }

    }
}
