package com.projet;

import java.text.Normalizer;

public enum BorrowingStatus {
    EN_COURS("En_cours"),
    EN_RETARD("En_retard"),
    RETOURNE("Retourné");

    private final String label;

    public String getLabel() {
        return label;
    }

    BorrowingStatus(String label) {
        this.label = label;
    }

    public static BorrowingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return EN_COURS;
        }

        String normalized = Normalizer
                .normalize(value, Normalizer.Form.NFD)
                .replace("\\p{M}", "")
                .toUpperCase()
                .trim();

        try {
            return BorrowingStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            System.out.println("Statut inconnu: '" + value + "'");
            return EN_COURS;
        }
    }

}
