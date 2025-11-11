package com.lucasleitz.medialibrary.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class AuthorDto {
    private UUID id;
    private String name;
}
