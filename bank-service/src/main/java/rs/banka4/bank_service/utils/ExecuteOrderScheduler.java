package rs.banka4.bank_service.utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.banka4.bank_service.service.abstraction.OrderService;

@Component
@RequiredArgsConstructor
public class ExecuteOrderScheduler {

    private final OrderService orderService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void executeOrders() {
        orderService.executeOrders();
    }

}
