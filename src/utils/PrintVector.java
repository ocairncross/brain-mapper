/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author oliver
 */
public class PrintVector
{

    public static String toString(float[] m)
    {
        return IntStream.range(0, m.length)
                .mapToDouble(i -> m[i])
                .mapToObj(Double::toString)
                .collect(Collectors.joining(",", "[", "]"));        
    }
}
