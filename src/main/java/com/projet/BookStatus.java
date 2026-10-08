package com.projet;

public enum BookStatus {
    DISPONIBLE,
    EMPRUNTE,
    RESERVE;

    public static BookStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return DISPONIBLE;
        }

        // Normaliser : enlève accents, met en majuscules, trim
        String normalized = java.text.Normalizer
                .normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .trim();

        try {
            return BookStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            System.out.println("Statut inconnu : '" + value + "'");
            return DISPONIBLE;
        }
    }

}
