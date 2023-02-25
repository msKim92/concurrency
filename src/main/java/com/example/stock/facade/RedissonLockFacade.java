package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockFacade {
    private RedissonClient redissonClient;
    private StockService stockService;

    public RedissonLockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) {
        RLock lock = redissonClient.getLock(key.toString());

        try{
            boolean possible = lock.tryLock(5, 1, TimeUnit.SECONDS); //기다리는 시간 5초

            if (!possible) {
                System.out.println("Lock 획득 실패");
                return;
            }

            stockService.decrease(key,quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();//받아온 락을 해제
        }


    }
}

