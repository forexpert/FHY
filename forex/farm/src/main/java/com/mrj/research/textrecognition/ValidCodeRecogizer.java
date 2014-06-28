package com.mrj.research.textrecognition;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by I074995 on 6/24/2014.
 */
public interface ValidCodeRecogizer {

    String recognize(byte[] image) throws IOException;
    public String recognize(BufferedImage inputImage) throws IOException;
}
