package org.keliu.common.reply;

import org.keliu.common.domain.Money;

public class BeginReviseOrderReply {
    private Money revisedOrderTotal;

    public BeginReviseOrderReply(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }

    public BeginReviseOrderReply() {
    }

    public Money getRevisedOrderTotal() {
        return revisedOrderTotal;
    }

    public void setRevisedOrderTotal(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }
}
