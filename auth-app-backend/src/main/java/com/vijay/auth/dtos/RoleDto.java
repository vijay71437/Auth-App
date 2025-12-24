package com.vijay.auth.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {

    private UUID id;
    private String name;
}

