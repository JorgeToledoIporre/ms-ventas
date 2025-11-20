package com.tienda.carrito.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    
    @NotNull(message = "Los datos del cliente son obligatorios")
    @Valid
    private ClienteDTO cliente;
    
    private String codigoDescuento;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ClienteDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50)
    private String nombre;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50)
    private String apellidos;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La calle es obligatoria")
    private String calle;
    
    private String departamento;
    
    @NotBlank(message = "La región es obligatoria")
    private String region;
    
    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;
    
    private String indicaciones;
}