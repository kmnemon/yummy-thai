package org.keliu.orderservice.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.keliu.orderservice.common.Address;

import javax.persistence.*;
import java.time.LocalDateTime;

@Access(AccessType.FIELD)
@Embeddable
public class DeliveryInformation {
    private LocalDateTime deliveryTime;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="state", column=@Column(name="delivery_state"))
    })
    private Address deliveryAddress;

    public DeliveryInformation() {
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public DeliveryInformation(LocalDateTime deliveryTime, Address deliveryAddress) {
        this.deliveryTime = deliveryTime;
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
