package io.github.vitorfranca089.libmanager.dto;

public record BookDTO(
        int id,
        String title,
        String author,
        int yearPub,
        String genre,
        boolean isAvailable
) {
}
