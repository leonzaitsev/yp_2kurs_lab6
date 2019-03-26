package laba6;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;

public class Cissoid implements Shape, Serializable, Cloneable, Transferable{
    private int centerX;
    private int centerY;
    int leftX;
    int leftY;
    int width;
    int height;

    private int a;

    public Cissoid(int centerX, int centerY, int leftX, int leftY,int width, int height, int a) {
        super();
        this.centerX = centerX;
        this.centerY = centerY;
        this.leftX = leftX;
        this.leftY = leftY;
        this.width = width;
        this.height = height;
        this.a = a;
    }


@Override
public Rectangle getBounds()
{
    return new Rectangle(leftX, leftY, width, height);
}

    @Override
    public Rectangle2D getBounds2D()
    {
        return new Rectangle2D.Float(leftX, leftY, width, height);
    }

    public int getCenterX()
    {
        return leftX + width / 2;
    }

    public int getCenterY()
    {
        return leftY + height / 2;
    }

    public double getR(double alpha)
    {
        double s = Math.sin(alpha);
        double c = Math.cos(alpha);
        return 3 * a * s * c / (s * s * s + c * c * c);
    }

    @Override
    public boolean contains(double x, double y)
    {
        if (!getBounds().contains(x, y))
            return false;
        double x0 = x - getCenterX();
        double y0 = getCenterY() - y;
        if (x0 < 0 || y0 < 0)
            return false;
        double alpha = Math.atan(y0 / x0);
        double r = getR(alpha);
        return x0 * x0 + y0 * y0 < r * r;
    }

    @Override
    public boolean contains(Point2D p)
    {
        return contains(p.getX(), p.getY());
    }

    @Override
    public boolean intersects(double x, double y, double w, double h)
    {
        return getBounds().intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r)
    {
        return getBounds().intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h)
    {
        return contains(x, y) && contains(x + w, y) && contains(x, y + h) && contains(x + w, y + h);
    }

    @Override
    public boolean contains(Rectangle2D r)
    {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at)
    {
        return new ShapeIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness)
    {
        return getPathIterator(at);
    }


    class ShapeIterator implements PathIterator {
        AffineTransform at;
        boolean done = false;
        double h = Math.PI / 400;
        boolean start = true;
        double t = -Math.PI/2+h;

        ShapeIterator(AffineTransform at) {
            this.at = at;
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public void next() {
            t += h;
        }

        @Override
        public int currentSegment(float[] coordinate) {
            if (start) {
                coordinate[0] = (float) (2 * a * Math.pow(Math.tan(t), 2)/(1+Math.pow(Math.tan(t), 2))) + centerX;
                coordinate[1] = (float) (2 * a * Math.pow(Math.tan(t), 3)/Math.pow(Math.tan(t), 2)) + centerY;

                start = false;
                if (at != null)
                    at.transform(coordinate, 0, coordinate, 0, 1);

                return SEG_MOVETO;

            }
            if (t >= Math.PI/2-h) {
                done = true;
                return SEG_CLOSE;
            }


            coordinate[0] = (float) (2 * a * Math.pow(Math.tan(t), 2)/(1+Math.pow(Math.tan(t), 2))) + centerX;
            coordinate[1] = (float) (2 * a * Math.pow(Math.tan(t), 3)/Math.pow(Math.tan(t), 2)) + centerY;
            return SEG_LINETO;
        }

        @Override
        public int currentSegment(double[] coordinate) {
            if (start) {

                coordinate[0] =  (2 * a * Math.pow(Math.tan(t), 2)/(1+Math.pow(Math.tan(t), 2))) + centerX;
                coordinate[1] =  (2 * a * Math.pow(Math.tan(t), 3)/Math.pow(Math.tan(t), 2)) + centerY;
                start = false;

                return SEG_MOVETO;

            }
            if (t >= Math.PI/2-h) {
                done = true;
                return SEG_CLOSE;
            }


            coordinate[0] =  (2 * a * Math.pow(Math.tan(t), 2)/(1+Math.pow(Math.tan(t), 2))) + centerX;
            coordinate[1] =  (2 * a * Math.pow(Math.tan(t), 3)/Math.pow(Math.tan(t), 2)) + centerY;
            return SEG_LINETO;
        }

    }

    public Object clone()
    {
        try
        {
            Cissoid s = (Cissoid) super.clone(); // make a copy of all
            return s;
        } catch (CloneNotSupportedException e)
        { // This should never happen
            return this;
        }
    }

    public static DataFlavor decDataFlavor = new DataFlavor(Cissoid.class, "Cissoid");

    // This is a list of the flavors we know how to work with
    public static DataFlavor[] supportedFlavors = { decDataFlavor, DataFlavor.stringFlavor };

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return (DataFlavor[]) supportedFlavors.clone();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return (flavor.equals(decDataFlavor) || flavor.equals(DataFlavor.stringFlavor));
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (flavor.equals(decDataFlavor))
        {
            return this;
        } else if (flavor.equals(DataFlavor.stringFlavor))
        {
            return toString();
        } else
            throw new UnsupportedFlavorException(flavor);
    }


    @Override
    public String toString() {
        return leftX +
                " " + leftY +
                " " + width +
                " " + height +
                " " + a;
    }

    public static Cissoid getFromString(String s)
    {
        String[] arr = s.split(" ");
        return new Cissoid(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),Integer.parseInt(arr[4]), Integer.parseInt(arr[5]), Integer.parseInt(arr[6]));
    }

    public void translate(double x, double y)
    {
       centerX += x;
       centerY += y;
    }

}
