package com.example.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;



@RestController
@RequestMapping("/product")
public class ProductController {
	
		
	private Logger LOG=Logger.getLogger(ProductController.class.getName());
	private RestTemplate restTemplate;
	
	@Autowired
	public ProductController(RestTemplate restTemplate) {
		this.restTemplate=restTemplate;
	}
	
	
	@GetMapping("/{id}/similar")
	public ResponseEntity<Object>getSimilar(@PathVariable(value="id") String productId){
		logs();
		String url="";
		try {
			url="http://localhost:3001/product/"+productId+"/similarids";
		int[] response = restTemplate.getForObject(url, int[].class);
		
		ArrayList<Product> similarProduct=new ArrayList<Product>();	
		
		for (int i=0;i<response.length;i++) {
			
			Product produto=restTemplate.getForObject("http://localhost:3001/product/"+response[i], Product.class);
			
			similarProduct.add(produto);
			
		}				
		return new ResponseEntity<Object>(similarProduct, HttpStatus.OK);
		
		}catch(HttpServerErrorException e) {
			LOG.severe(e.getMessage()+"\tError URL: "+url);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			
		}catch (HttpClientErrorException e) {
			LOG.warning(e.getMessage()+"\tError URL: "+url);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	private void logs() {
		FileHandler ficherolog;

		try {

			ficherolog = new FileHandler("log.txt");
			
			LOG.addHandler(ficherolog);


		} catch (SecurityException | IOException e) {

			LOG.severe("Se ha producido un error en la creación del fichero");

			e.printStackTrace();

		}
		

	}
}
