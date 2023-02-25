package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NameLockService {
    private StockRepository stockRepository;

    public NameLockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    //부모의 트랜잭션과는 별도로 실행되어야 한다.
    // 안그러면 Synchronized와 같은 문제가 발생한다.
    // 핵심은 lock 을 해제하기전에 Database 에 commit 이 되도록 하는것
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void propagation_decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);

    }
}
