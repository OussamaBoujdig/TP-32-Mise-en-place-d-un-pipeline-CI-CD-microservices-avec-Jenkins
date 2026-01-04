package com.example.car.models;

import com.example.car.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    private Long id;
    private String marque;
    private String cylindree;
    private String immatriculation;
    private Client proprietaire;
}