package io.github.vitorfranca089.libmanager.dto;

import io.github.vitorfranca089.libmanager.model.enums.LoanStatus;

import java.time.LocalDateTime;

public record LoanDTO(
        int id,
        BookDTO book,
        UserDTO user,
        LocalDateTime loanDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        LoanStatus loanStatus
) {
}
