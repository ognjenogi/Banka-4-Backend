package rs.banka4.bank_service.exceptions;

public class ExchangeAcronymNotFound extends RuntimeException {
    public ExchangeAcronymNotFound(String message) {
        super(message);
    }
}
// Interal error for seeding / api interaction
