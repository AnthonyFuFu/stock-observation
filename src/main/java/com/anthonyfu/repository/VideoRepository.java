package com.anthonyfu.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.anthonyfu.model.entity.Video;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VideoRepository extends ReactiveCrudRepository<Video, Long> {

    Mono<Video> findByFilename(String filename);

    Flux<Video> findAllByOrderByCategoryAscTitleAsc();
}
