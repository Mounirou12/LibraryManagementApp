package com.projet.page.design;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DatePickerField extends JSpinner {

    public DatePickerField() {
        this(LocalDate.now());
    }

    public DatePickerField(LocalDate initialDate) {
        super(new SpinnerDateModel(
                toDate(initialDate),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH));

        setEditor(new JSpinner.DateEditor(this, "dd/MM/yyyy"));
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setPreferredSize(new Dimension(150, 35));

        // Style du champ interne
        JFormattedTextField textField = ((JSpinner.DefaultEditor) getEditor()).getTextField();
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1, 0xD5, 0xDB), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        textField.setBackground(Color.WHITE);
    }

    /** Retourne la date choisie sous forme de LocalDate */
    public LocalDate getLocalDate() {
        Date date = (Date) getValue();
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /** Définit la date affichée */
    public void setLocalDate(LocalDate date) {
        setValue(toDate(date));
    }

    private static Date toDate(LocalDate localDate) {
        if (localDate == null) return new Date();
        return java.sql.Date.valueOf(localDate);
    }
}