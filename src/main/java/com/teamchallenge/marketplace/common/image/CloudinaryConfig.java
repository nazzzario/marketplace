package com.teamchallenge.marketplace.common.image;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.name}")
    private String CLOUD_NAME;

    @Value("${cloudinary.key}")
    private String API_KEY;

    @Value("${cloudinary.secret}")
    private String API_SECRET;

    @Bean
    public Cloudinary getCloudinary(){
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name",CLOUD_NAME);
        config.put("api_key",API_KEY);
        config.put("api_secret",API_SECRET);
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
