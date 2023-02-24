package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;


    @BeforeEach
    public void init() {
        System.out.println("호출");
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void finsh() {
        stockRepository.deleteAll();
    }


    @Test
    @DisplayName("한개씩 재고 감소되는지 확인")
    public void stock_decrease() {
        stockService.decrease(1L,1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(99);
    }

    @Test
    @DisplayName("동시에 100개 요청 - 레이스컨디션 발생 시키기")
    public void AtOnce100() throws InterruptedException {
        int threadcnt = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); //비동기를 실행시켜주는 자바의 API
        CountDownLatch latch = new CountDownLatch(threadcnt);//100개의 요청을 기다려야함 다른 스레드에서 완성될때까지 대기하는 클래스

        for (int i = 0; i < threadcnt; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L); //하나의 요청
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(0);
    }
}