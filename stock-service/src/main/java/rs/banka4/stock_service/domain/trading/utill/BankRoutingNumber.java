package rs.banka4.stock_service.domain.trading.utill;

public enum BankRoutingNumber {
    BANK4(444);

    private final int routingNumber;

    BankRoutingNumber(int routingNumber) {
        this.routingNumber = routingNumber;
    }

    public int getRoutingNumber() {
        return routingNumber;
    }
}
