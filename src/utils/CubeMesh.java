package utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.washington.biostr.sig.volume.VolumeArray;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.TriangleMesh;
import javax.vecmath.Point3f;

/**
 *
 * @author Oliver Cairncross
 */
public class CubeMesh
{
    
    TriangleMesh triangleMesh;
    List<Face> faces = new ArrayList<Face>();

    public CubeMesh(VolumeArray va)
    {
        createFaces(va);
        createMesh();
    }
    
    public TriangleMesh getTriangleMesh()
    {
        return triangleMesh;
    }
    
    private void createMesh()
    {
        float[] vertices = new float[faces.size() * 4 * 3];
        float[] textureCoords = new float[1];        
        int[] faceIndices = new int[faces.size() * 12];
        
        textureCoords[0] = 0.0f;
        
        int vertexIndex = 0;
        int faceCount = 0;
        
        for (Face f : faces)
        {            
            faceIndices[faceCount * 12 + 0] = vertexIndex / 3;
            faceIndices[faceCount * 12 + 1] = 0;            
            faceIndices[faceCount * 12 + 10] = vertexIndex / 3;
            faceIndices[faceCount * 12 + 11] = 0;            
            vertices[vertexIndex++] = f.a.x;
            vertices[vertexIndex++] = f.a.y;
            vertices[vertexIndex++] = f.a.z;
            
            faceIndices[faceCount * 12 + 2] = vertexIndex / 3;
            faceIndices[faceCount * 12 + 3] = 0;
            vertices[vertexIndex++] = f.b.x;
            vertices[vertexIndex++] = f.b.y;
            vertices[vertexIndex++] = f.b.z;
            
            faceIndices[faceCount * 12 + 4] = vertexIndex / 3;
            faceIndices[faceCount * 12 + 5] = 0;
            faceIndices[faceCount * 12 + 6] = vertexIndex / 3;
            faceIndices[faceCount * 12 + 7] = 0;
            vertices[vertexIndex++] = f.c.x;
            vertices[vertexIndex++] = f.c.y;
            vertices[vertexIndex++] = f.c.z;
            
            faceIndices[faceCount * 12 + 8] = vertexIndex / 3;
            faceIndices[faceCount * 12 + 9] = 0;
            vertices[vertexIndex++] = f.d.x;
            vertices[vertexIndex++] = f.d.y;
            vertices[vertexIndex++] = f.d.z;
            
            faceCount++;
        }
        
        triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().setAll(vertices);
        triangleMesh.getTexCoords().setAll(textureCoords);
        triangleMesh.getFaces().setAll(faceIndices);
        
    }

    private void createFaces(VolumeArray matrix)
    {
        
        // This algo does not collapse points; so we have coincident points...
                
        float xScale = 1.0f;
        float yScale = 1.0f;
        float zScale = 1.0f;

        for (int i = 0; i < matrix.getMaxX(); i++)
        {
            for (int j = 0; j < matrix.getMaxY(); j++)
            {
                for (int k = 0; k < matrix.getMaxZ(); k++)
                {

                    double intensity = matrix.getDouble(i, j, k, 0, 0);

                    float x = i * xScale;
                    float y = j * yScale;
                    float z = k * zScale;

                    if (intensity > 0.0)
                    {
                        System.out.printf("[%3d,%3d,%3d] = %f\n", i, j, k, intensity);

                        if (i == 0 || matrix.getDouble(i - 1, j, k, 0, 0) != 0) // left
                        {
                            Face f = new Face();
                            f.a = new Point3f(x - xScale, y, z - zScale);
                            f.b = new Point3f(x - xScale, y - yScale, z - zScale);
                            f.c = new Point3f(x - xScale, y - yScale, z);
                            f.d = new Point3f(x - xScale, y, z);
                            f.normal = Face.LEFT;
                            faces.add(f);
                        }

                        if (i == matrix.getMaxX() - 1 || matrix.getDouble(i + 1, j, k, 0, 0) != 0) // right
                        {
                            Face f = new Face();
                            f.a = new Point3f(x, y, z);
                            f.b = new Point3f(x, y - yScale, z);
                            f.c = new Point3f(x, y - yScale, z - zScale);
                            f.d = new Point3f(x, y, z - zScale);
                            f.normal = Face.RIGHT;
                            faces.add(f);
                        }

                        if (j == 0 || matrix.getDouble(i, j - 1, k, 0, 0) != 0) // down
                        {
                            Face f = new Face();
                            f.a = new Point3f(x, y - yScale, z);
                            f.b = new Point3f(x - xScale, y - yScale, z);
                            f.c = new Point3f(x - xScale, y - yScale, z - zScale);
                            f.d = new Point3f(x, y - yScale, z - zScale);
                            f.normal = Face.DOWN;
                            faces.add(f);
                        }

                        if (j == matrix.getMaxY() - 1 || matrix.getDouble(i, j + 1, k, 0, 0) != 0) // up
                        {
                            Face f = new Face();
                            f.a = new Point3f(x - xScale, y, z - zScale);
                            f.b = new Point3f(x - xScale, y, z);
                            f.c = new Point3f(x, y, z);
                            f.d = new Point3f(x, y, z - zScale);
                            f.normal = Face.UP;
                            faces.add(f);
                        }

                        if (k == 0 || matrix.getDouble(i, j, k - 1, 0, 0) != 0) // back
                        {
                            Face f = new Face();
                            f.a = new Point3f(x, y - yScale, z - zScale);
                            f.b = new Point3f(x - xScale, y - yScale, z - zScale);
                            f.c = new Point3f(x - xScale, y, z - zScale);
                            f.d = new Point3f(x, y, z - zScale);
                            f.normal = Face.BACK;
                            faces.add(f);
                        }

                        if (k == matrix.getMaxZ() - 1 || matrix.getDouble(i, j, k + 1, 0, 0 ) != 0) // front
                        {
                            Face f = new Face();
                            f.a = new Point3f(x, y, z);
                            f.b = new Point3f(x - xScale, y, z);
                            f.c = new Point3f(x - xScale, y - yScale, z);
                            f.d = new Point3f(x, y - yScale, z);
                            f.normal = Face.FRONT;
                            faces.add(f);
                        }
                    }
                }
            }
        }
    }

}
