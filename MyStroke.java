package laba6;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class MyStroke implements Stroke {
    private BasicStroke stroke;

    MyStroke(float width) {
        this.stroke = new BasicStroke(width);
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        GeneralPath shapePath = new GeneralPath();

        float[] xy = new float[2];
        float[] pre_xy = new float[2];
        double t = -5;

        for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
            int type = i.currentSegment(xy);

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    shapePath.moveTo(xy[0], xy[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    double x1 = pre_xy[0];
                    double y1 = pre_xy[1];
                    double x2 = xy[0];
                    double y2 = xy[1];

                    double dx = x2 - x1;
                    double dy = y2 - y1;

                    double length = Math.sqrt(dx * dx + dy * dy);
                    dx /= length;
                    dy /= length;
                    x1 += dx * t;
                    y1 += dy * t;
                    length -= t;
                    t = 0;

                    double step = 7;
                    if (!Double.isInfinite(length)) {
                        while (t <= length) {
                            x1 += dx * step + dy * step;
                            y1 += -dy * step + dx * step;
                            shapePath.lineTo(x1, y1);

                            x1 += -dy * step;
                            y1 += +dx * step;
                            shapePath.lineTo(x1, y1);

                            x1 += dx * step;
                            y1 += dy * step;
                            shapePath.lineTo(x1, y1);

                            x1 += +dy * step;
                            y1 += -dx * step;
                            shapePath.lineTo(x1, y1);

                            t += 3 * step;
                        }
                        t -= length;
                    }
                    break;

            }

            pre_xy[0] = xy[0];
            pre_xy[1] = xy[1];
        }

        return stroke.createStrokedShape(shapePath);
    }
}
