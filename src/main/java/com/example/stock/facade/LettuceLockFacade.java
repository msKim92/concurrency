package com.example.stock.facade;

import com.example.stock.repository.RedisLockRespository;
import com.example.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockFacade {
    private RedisLockRespository redisLockRespository;
    private StockService stockService;

    public LettuceLockFacade(RedisLockRespository redisLockRespository, StockService stockService) {
        this.redisLockRespository = redisLockRespository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while (!redisLockRespository.lock(key)) { //락획득 실패시
            Thread.sleep(100);
        }

        try{
            stockService.propagation_decrease(key, quantity);
        }finally {
            redisLockRespository.unLock(key);
        }


    }
}
