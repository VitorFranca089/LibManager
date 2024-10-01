package io.github.vitorfranca089.libmanager.dto;

import java.time.LocalDateTime;

public record LoanDTO(
        int id,
        BookDTO book,
        UserDTO user,
        LocalDateTime loanDate,
        LocalDateTime returnDate
) {
}
