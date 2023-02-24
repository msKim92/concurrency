package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long productId;
    private long quantity;

    public Stock() {
    }

    public Stock(long id, long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public long getQuantity() {
        return quantity;
    }

    public void decrease(Long quantity) {
        if(this.quantity - quantity < 0){
            throw new RuntimeException("재고가 마이너스 일순 없습니다.");
        }
        this.quantity = this.quantity - quantity;
    }
}
