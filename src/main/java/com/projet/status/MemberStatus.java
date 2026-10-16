package com.projet;

import java.text.Normalizer;

public enum MemberStatus {
    SUSPENDU("Suspendu"),
    ACTIF("Actif");

    private final String label;

    public String getLabel() {
        return label;
    }

    MemberStatus(String label) {
        this.label = label;
    }

    public static MemberStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIF;
        }
        String normalized = Normalizer
                .normalize(value, Normalizer.Form.NFD)
                .replace("\\p{M}", "")
                .toUpperCase()
                .trim();

        try {
            return MemberStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            System.out.println("Statut inconnu: '" + value + "'");
            return ACTIF;
        }
    }
}
