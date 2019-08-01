package com.sam.bullhorn;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.Transformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CloudinaryConfig {
    private Cloudinary cloudinary;

    @Autowired
    public CloudinaryConfig(@Value("${cloud.key}") String key,
                            @Value("${cloud.secret}") String secret,
                            @Value("${cloud.name}") String cloud) {
        cloudinary = Singleton.getCloudinary();
        cloudinary.config.cloudName=cloud;
        cloudinary.config.apiSecret=secret;
        cloudinary.config.apiKey=key;
    }

    public Map upload(Object file, Map options) {
        try{
            return cloudinary.uploader().upload(file, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createUrl(String name, int width, int height, String action) {
        return cloudinary.url().transformation(new Transformation().width(width)
                .height(height).crop(action)).imageTag(name);
    }

    public String sepia(String name) {
        String info = cloudinary.url().transformation(new Transformation().effect("sepia")).imageTag(name);
        return info.substring(info.indexOf("'") + 1, info.indexOf("'", info.indexOf("'") + 1));
    }

    public String sepiaThumb(String name, int width, int height, String action) {
        String info = cloudinary.url().transformation(new Transformation().width(width)
                .height(height).crop(action).chain().effect("sepia")).imageTag(name);
        return info.substring(info.indexOf("'") + 1, info.indexOf("'", info.indexOf("'") + 1));
    }
}
