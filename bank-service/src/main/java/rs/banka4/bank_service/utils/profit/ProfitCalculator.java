package rs.banka4.bank_service.utils.profit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.options.db.OptionType;
import rs.banka4.bank_service.domain.orders.db.Direction;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.options.db.Option;
import rs.banka4.bank_service.domain.orders.db.Order;
import rs.banka4.bank_service.domain.security.future.db.Future;
import rs.banka4.bank_service.domain.security.forex.db.ForexPair;
import rs.banka4.bank_service.domain.security.stock.db.Stock;
import rs.banka4.bank_service.repositories.OrderRepository;

/**
 * A unified calculator that, for any asset type (Stock, Future, ForexPair, Option),
 * determines profit using the same overall steps:
 * 1) Gather buy orders -> compute weighted-average cost basis
 * 3) Subtract to find unsold (open) quantity
 * 4) Retrieve current market price from Listing
 * 5) Multiply by a "multiplier" if needed (futures contract size, option contract size)
 * 6) Returns unrealized profit
 */
@Service
@RequiredArgsConstructor
public class ProfitCalculator {
    private final OrderRepository orderRepository;
    /**
     * Calculates total profit (unrealized) for the given user and asset.
     */
    public MonetaryAmount calculateProfit(UUID userId, Asset asset,MonetaryAmount currentPrice) {
        var buyOrders = orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(userId, asset, Direction.BUY,true);
        var sellOrders = orderRepository.findByUserIdAndAssetAndDirectionAndIsDone(userId, asset, Direction.SELL,true);

        var totalBuyQty = BigDecimal.ZERO;
        var totalBuyCost = BigDecimal.ZERO;

        if(asset instanceof Option option) {
            return calculateOptionProfit(currentPrice,option);
        }
        for (var buy : buyOrders) {
            var qty = BigDecimal.valueOf(buy.getQuantity());
            var cost = buy.getPricePerUnit().getAmount().multiply(qty);
            BigDecimal fee = computeFee(buy, cost);
            totalBuyQty = totalBuyQty.add(qty);
            totalBuyCost = totalBuyCost.add(cost).add(fee);
        }
        var avgBuyPrice = totalBuyQty.compareTo(BigDecimal.ZERO) > 0
            ? totalBuyCost.divide(totalBuyQty, 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        var totalSellQty = sellOrders.stream()
            .map(o -> BigDecimal.valueOf(o.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        var unsoldQty = totalBuyQty.subtract(totalSellQty);

        var multiplier = getMultiplier(asset);
        System.out.println("Multiplier: " + multiplier+" totalByQunt "+totalBuyQty+" totalByPric "+totalBuyCost+" totalSelQunt "+totalSellQty+" currentPrice "+currentPrice.getAmount());
        var unrealizedProfit = currentPrice.getAmount().subtract(avgBuyPrice)
            .multiply(unsoldQty)
            .multiply(multiplier);

        return new MonetaryAmount(unrealizedProfit,currentPrice.getCurrency());
    }
    private BigDecimal computeFee(Order order, BigDecimal baseCost) {
        return switch (order.getOrderType()) {
            case MARKET -> {
                BigDecimal mFee = baseCost.multiply(BigDecimal.valueOf(0.14));
                yield mFee.min(BigDecimal.valueOf(7));
            }
            case LIMIT -> {
                BigDecimal lFee = baseCost.multiply(BigDecimal.valueOf(0.24));
                yield lFee.min(BigDecimal.valueOf(12));
            }
            default -> BigDecimal.ZERO;
        };
    }
    private MonetaryAmount calculateOptionProfit(MonetaryAmount monetaryCurrPrice, Option option) {
        final var contractMultiplier = BigDecimal.valueOf(100);
        var currentStockPrice = monetaryCurrPrice.getAmount();
        var strikePrice = option.getStrikePrice().getAmount();
        var premium = option.getPremium().getAmount();

        var intrinsic = getIntrinsic(option, currentStockPrice, strikePrice);

        var totalIntrinsicValue = intrinsic.multiply(contractMultiplier);
        var totalPremium = premium.multiply(contractMultiplier);

        var profit = totalIntrinsicValue.subtract(totalPremium);
        profit = profit.setScale(2, RoundingMode.HALF_UP);
        return new MonetaryAmount(profit, monetaryCurrPrice.getCurrency());
    }

    private static BigDecimal getIntrinsic(Option option, BigDecimal currentStockPrice, BigDecimal strikePrice) {
        BigDecimal intrinsic;
        if (option.getOptionType() == OptionType.CALL) {
            intrinsic = currentStockPrice.subtract(strikePrice);
        } else if (option.getOptionType() == OptionType.PUT) {
            intrinsic = strikePrice.subtract(currentStockPrice);
        } else {
            throw new IllegalArgumentException("Unsupported option type: " + option.getOptionType());
        }
        return intrinsic.max(BigDecimal.ZERO);
    }


    /**
     * Distinguishes each asset type by returning a multiplier for the final profit.
     *  - Stock -> 1
     *  - Future -> future.getContractSize()
     *  - Forex -> 1
     *  - Option -> 100 (as specification says) or use an option-specific contractSize if stored
     */
    private BigDecimal getMultiplier(Asset asset) {
        if (asset instanceof Stock) {
            return BigDecimal.ONE;
        } else if (asset instanceof Future fut) {
            return BigDecimal.valueOf(fut.getContractSize());
        } else if (asset instanceof ForexPair) {
            return BigDecimal.ONE;
        }
        return BigDecimal.ONE;
    }
}
