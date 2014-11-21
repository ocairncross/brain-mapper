/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Oliver Cairncross
 */
public class Face
{

    public static Vector3f UP = new Vector3f(0, 1, 0);
    public static Vector3f DOWN = new Vector3f(0, -1, 0);
    public static Vector3f LEFT = new Vector3f(-1, 0, 0);
    public static Vector3f RIGHT = new Vector3f(1, 0, 0);
    public static Vector3f FRONT = new Vector3f(0, 0, 1);
    public static Vector3f BACK = new Vector3f(0, 0, -1);

    public Point3f a;
    public Point3f b;
    public Point3f c;
    public Point3f d;
    public Vector3f normal;
    
}