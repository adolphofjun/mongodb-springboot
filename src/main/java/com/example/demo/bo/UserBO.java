package com.example.demo.bo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class UserBO {
    private String name;
    private String description;
}
