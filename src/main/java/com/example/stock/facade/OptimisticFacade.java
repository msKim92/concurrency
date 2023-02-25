package com.example.stock.facade;

import com.example.stock.service.OptimisticService;
import org.springframework.stereotype.Service;

/*
 * 실패시 재요청을 해야하므로
 */
//@Service
public class OptimisticFacade {
    private OptimisticService optimisticService;

    public OptimisticFacade(OptimisticService optimisticService) {
        this.optimisticService = optimisticService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
    while (true) {
        try {
            optimisticService.decrease(id, quantity);
            break;
        } catch (Exception e) {
            Thread.sleep(50);

            }
        }
    }

}
