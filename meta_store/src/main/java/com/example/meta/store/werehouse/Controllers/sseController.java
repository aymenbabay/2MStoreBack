package com.example.meta.store.werehouse.Controllers;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("werehouse/sse/")
public class sseController {

	private ExecutorService executor = Executors.newCachedThreadPool();
	
	
	@GetMapping("steam-flux")
	public Flux<String> streamFlux(){
		System.out.println(" stream web flux in sse controller stream flux method");
		return Flux.interval(Duration.ofSeconds(1))
				.map(sequence -> "flux - " + LocalTime.now().toString());
	}
	
	
}
