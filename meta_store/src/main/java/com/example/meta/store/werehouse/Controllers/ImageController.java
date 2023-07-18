package com.example.meta.store.werehouse.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.werehouse.Services.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/image")
@RequiredArgsConstructor
public class ImageController {
private final ImageService imageService;

	
	@GetMapping(path = "/{lien}/{service}/{name}")
	public byte[] getImage( @PathVariable String lien, @PathVariable String service, @PathVariable String name)throws Exception {
		System.out.println(name+" udvkjh");
		return imageService.getImage( lien,service,name);
				}
}
