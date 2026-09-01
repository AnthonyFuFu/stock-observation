package com.anthonyfu.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 統一紀錄 service 方法的執行時間與例外，取代原本散落各處的手動 log/println。
 *
 * WebFlux 的方法多半回傳 Mono/Flux，proceed() 當下並不會真正執行方法內容，
 * 因此耗時必須掛在回傳的 Publisher 完成／出錯的時間點量測，而非 proceed() 呼叫本身。
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("within(com.anthonyfu.service..*)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        long start = System.nanoTime();
        Object result = joinPoint.proceed();

        if (result instanceof Mono<?> mono) {
            return mono
                    .doOnSuccess(v -> logSuccess(signature, start))
                    .doOnError(e -> logError(signature, start, e));
        }
        if (result instanceof Flux<?> flux) {
            return flux
                    .doOnComplete(() -> logSuccess(signature, start))
                    .doOnError(e -> logError(signature, start, e));
        }

        // 非 reactive 回傳型別，直接量測同步呼叫耗時
        logSuccess(signature, start);
        return result;
    }

    private void logSuccess(String signature, long startNanos) {
        log.debug("{} 執行完成，耗時 {} ms", signature, elapsedMs(startNanos));
    }

    private void logError(String signature, long startNanos, Throwable e) {
        log.error("{} 執行失敗，耗時 {} ms", signature, elapsedMs(startNanos), e);
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
