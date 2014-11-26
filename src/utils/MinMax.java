/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.vecmath.Vector3f;


/**
 *
 * @author o.cairncross
 */
public class MinMax implements Serializable
{

    private double min = 0;
    private double max = 0;
    private boolean initialised;
    private final List<Vector3f> colourVectors = new ArrayList<>();
    private double floor = Double.NEGATIVE_INFINITY;
    private double ceiling = Double.POSITIVE_INFINITY;
    
    public MinMax()
    {
        initialised = false;
        colourVectors.add(new Vector3f(0.847f, 0.057f, 0.057f)); // red
        colourVectors.add(new Vector3f(0.527f, 0.527f, 0.0f)); // yellow
        colourVectors.add(new Vector3f(0.0f, 0.592f, 0.0f)); // green
        colourVectors.add(new Vector3f(0.0f, 0.559f, 0.559f)); // cyan
        colourVectors.add(new Vector3f(0.316f, 0.316f, 0.991f)); // blue
        colourVectors.add(new Vector3f(0.718f, 0.0f, 0.718f)); // magenta
    }
    
    public MinMax(double floor, double ceiling)
    {
        this.floor = floor;
        this.ceiling = ceiling;
    }

    public boolean isInitialised()
    {
        return initialised;
    }

    public final boolean setVal(double newVal)
    {
        boolean change = false;
        if (initialised)
        {
            if (newVal < min)
            {
                setMin(newVal);
                change = true;
            }
            if (newVal > max)
            {
                setMax(newVal);
                change = true;
            }
        }
        else
        {
            setMin(newVal);
            setMax(newVal);            
            change = true;
            initialised = true;
        }
        return change;
    }
    
    private void setMin(double i)
    {
        if (i > floor)
        {
            min = i;
        }
        else
        {
            min = floor;
        }
    }
    
    private void setMax(double i)
    {
        if (i < ceiling)
        {
            max = i;
        }
        else
        {
            max = ceiling;
        }
    }

    public boolean setVal(MinMax newVal)
    {
        // Don't short circuit this with ||
        return setVal(newVal.getMin()) | setVal(newVal.getMax());
    }

    public void reset()
    {
        initialised = false;
    }

    public void shrinkMin(double s)
    {
        min = min - (Math.abs(min) * s);
    }

    public void growMax(double s)
    {
        max = max + (Math.abs(max) * s);
    }

    public void decMin(double s)
    {
        min -= s;
    }

    public void incMax(double s)
    {
        max += s;
    }

    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }

    public double getRange()
    {
        return max - min;
    }

    public double getMidPoint()
    {
        return (getRange() / 2.0) + min;
    }

    @Override
    public String toString()
    {
        return String.format("[%,.3f - %,.3f]", min, max);
    }

    public Vector3f getColourVector(double x, boolean intepolate)
    {
        return getColourVector(x, getMin(), getMax(), intepolate);
    }

    public Vector3f getColourVector(double x)
    {
        return getColourVector(x, getMin(), getMax(), true);
    }
    
    public Color getColour(double x)
    {
        Vector3f colourVector = getColourVector(x);
        return new Color(colourVector.x, colourVector.y, colourVector.z);
    }

    public Vector3f getColourVector(double x, double clampMin, double clampMax)
    {
        return getColourVector(x, clampMin, clampMax, true);
    }

    public Vector3f getColourVector(double x, double clampMin, double clampMax, boolean interpolate)
    {
        Vector3f colorVector = new Vector3f(0.0f, 0.0f, 0.0f);

        if (x < clampMin || x > clampMax)
        {
            return colorVector;
        }

        int n = colourVectors.size();
        double r = clampMax - clampMin;
        double s = (n - 1) / r;
        int b = (int) (s * x);

        if (b == n - 1)
        {
            return colourVectors.get(n - 1);
        }

        double f = (s * x) - b;
        if (interpolate)
        {
            colorVector.interpolate(colourVectors.get(b), colourVectors.get(b + 1), (float) f);
        }
        else
        {
            colorVector = colourVectors.get(b);
        }
        return colorVector;
    }

    public BufferedImage getDiscreteKey()
    {
        Random r = new Random();
        int w = 50;
        int h = 50;
        int values = (int) getRange();
        BufferedImage key = new BufferedImage(w, h * values, BufferedImage.TYPE_INT_ARGB);

        Graphics g = key.getGraphics();
        g.setFont(new Font("Arial", Font.BOLD, 24));
        int p = 0;
        for (int i = values; i > 0; i--)
        {
            int gen = i + (int) getMin();
            Double d = new Double(gen);            
            g.fillRect(0, p * h, w, (p * h) + h);
            g.setColor(Color.WHITE);
            g.drawString(String.format("%d", gen), 10, (p * h) + 30);
            p++;
        }
        return key;
    }

}
