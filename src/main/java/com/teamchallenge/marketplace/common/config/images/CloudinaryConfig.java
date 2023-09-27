package com.teamchallenge.marketplace.common.config.images;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinaryConfig(){
        return new Cloudinary(ObjectUtils.asMap(""));
    }

}
