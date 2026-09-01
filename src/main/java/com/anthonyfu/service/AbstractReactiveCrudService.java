package com.anthonyfu.service;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

/**
 * 封裝標準的「以主鍵查找 → 覆寫主鍵 → 儲存 / 刪除」流程，
 * 讓各 CRUD service 不必重複實作幾乎一樣的 update/delete 邏輯。
 *
 * 子類別只需要提供 repository、如何讀寫 entity 的主鍵，
 * 其餘特殊查詢方法仍維持在子類別自行撰寫。
 */
public abstract class AbstractReactiveCrudService<T, ID, R extends ReactiveCrudRepository<T, ID>> {

    protected final R repository;

    protected AbstractReactiveCrudService(R repository) {
        this.repository = repository;
    }

    /** 取得 entity 目前的主鍵值 */
    protected abstract ID extractId(T entity);

    /** 將主鍵寫回 entity（各 entity 的 setId 方法簽章不同，無法泛型化，需子類別實作） */
    protected abstract void assignId(T entity, ID id);

    /** 標準更新流程：先確認資料存在，再覆寫主鍵並儲存 */
    protected Mono<T> updateById(ID id, T entity) {
        return repository.findById(id).flatMap(existing -> {
            assignId(entity, id);
            return repository.save(entity);
        });
    }

    /** 適用於以非主鍵欄位查找既有資料（例如以 symbol 查找）後再覆寫主鍵並儲存 */
    protected Mono<T> updateByLookup(Mono<T> lookup, T entity) {
        return lookup.flatMap(existing -> {
            assignId(entity, extractId(existing));
            return repository.save(entity);
        });
    }

    /** 依自訂查詢取得既有資料後刪除 */
    protected Mono<Void> deleteByLookup(Mono<T> lookup) {
        return lookup.flatMap(repository::delete);
    }
}
