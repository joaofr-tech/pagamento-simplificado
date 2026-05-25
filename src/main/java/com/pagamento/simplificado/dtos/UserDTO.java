package com.pagamento.simplificado.dtos;

import com.pagamento.simplificado.domain.UserType;

import java.math.BigDecimal;

public record UserDTO(String firstName, String lastName, String document, BigDecimal balance, String email, String password, UserType userType) {
}
