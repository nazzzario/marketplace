package com.teamchallenge.marketplace.common.image;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    private final String CLOUD_NAME = "teamchallenge-marketplace";
    private final String API_KEY = "519845717787162";
    private final String API_SECRET = "tmE7PuZ2PuSrYtxin_cIZVPMgAA";

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
