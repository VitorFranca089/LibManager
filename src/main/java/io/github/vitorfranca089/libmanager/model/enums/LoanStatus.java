package io.github.vitorfranca089.libmanager.model.enums;

public enum LoanStatus {
    BORROWED("Emprestado"),
    RETURNED("Devolvido"),
    OVERDUE("Atrasado");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
