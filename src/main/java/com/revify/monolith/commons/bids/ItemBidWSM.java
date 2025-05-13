package com.revify.monolith.commons.bids;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static com.revify.monolith.commons.bids.BidType.NEW;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  ItemBidWSM implements Iterable<ItemBidWSM> {
    private Long userId;
    private String itemId;
    private String currency;
    private BigDecimal amount;

    private BidType bidType = NEW;

    @Override
    public Iterator<ItemBidWSM> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super ItemBidWSM> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<ItemBidWSM> spliterator() {
        return Iterable.super.spliterator();
    }
}
