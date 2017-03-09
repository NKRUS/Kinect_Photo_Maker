package ru.kit.Kinect;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mikha on 12.01.2017.
 */
public class Kinect extends J4KSDK {

    private VideoPanel viewer = null;

    private boolean isFroze = false;
    private byte[] frozenFrame;
    private byte[] currentColorFrame;


    public void freezeFrame(){
        isFroze = true;
        frozenFrame = currentColorFrame;
    }

    public void continueFrame(){
        isFroze = false;
    }

    public void makeContour(){
        isFroze = true;

        //BufferedImage img = createImageFromBytes(currentColorFrame);
        try {
//            chromakeyImage = new ChromakeyImage(VideoPanel.getLast_screen());
//            frozenFrame = createByteArrayFromImage(chromakeyImage.getProcessedImg());
        } catch (Exception ex) {
            System.err.print("First make screenshot");
            isFroze = false;
        }
        //frozenFrame = getBorderCanny(currentColorFrame);
    }

    // byte[] -> BufferedImage
    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] createByteArrayFromImage(BufferedImage img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public void setViewer(VideoPanel viewer) {
        this.viewer = viewer;
    }


    /*The following method will run every time a new depth frame is
      received from the Kinect sensor. The data of the depth frame is
      converted into a DepthMap object, with U,V texture mapping if
      available.*/
    @Override
    public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {

        DepthMap map=new DepthMap(getDepthWidth(),getDepthHeight(),XYZ);
        if(UV!=null) map.setUV(UV);
    }

    /*The following method will run every time a new skeleton frame
      is received from the Kinect sensor. The skeletons are converted
      into Skeleton objects.*/
    @Override
    public void onSkeletonFrameEvent(boolean[] skeleton_tracked, float[] joint_position, float[] joint_orientation, byte[] joint_status) {

        Skeleton skeletons[]=new Skeleton[getMaxNumberOfSkeletons()];
        for(int i=0;i<getMaxNumberOfSkeletons();i++)
        {
            skeletons[i]=Skeleton.getSkeleton(i, skeleton_tracked, joint_position, joint_orientation, joint_status, this);
            System.out.println(skeletons[i].toString());
        }
    }

    //color_frame - BGRA format
    @Override
    public void onColorFrameEvent(byte[] color_frame) {
        if (viewer == null || viewer.videoTexture == null) return;
        currentColorFrame = color_frame;
        if(!isFroze)
            viewer.videoTexture.update(getColorWidth(), getColorHeight(), color_frame);
        else
            viewer.videoTexture.update(getColorWidth(), getColorHeight(), frozenFrame);
    }
}
