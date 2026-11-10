package org.delcom.ifs25034pbo2026springbootcashflow.domain.exception;

/** Dilempar saat validasi aturan bisnis atau input gagal. */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
