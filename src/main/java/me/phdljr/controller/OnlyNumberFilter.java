package me.phdljr.controller;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class OnlyNumberFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
        throws BadLocationException {
        if (str.matches("\\d+")) {     // 숫자만 허용
            super.insertString(fb, offs, str, a);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
        throws BadLocationException {
        if (str.matches("\\d+")) {     // 숫자만 허용
            super.replace(fb, offs, length, str, a);
        }
    }
}
