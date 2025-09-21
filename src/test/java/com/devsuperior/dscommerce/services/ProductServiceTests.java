package com.devsuperior.dscommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingProductId, nonExistingProductId, dependentProductId;
	private String productName;
	private Product product;
	private ProductDTO productDTO;
	private PageImpl<Product> page;
	
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingProductId = 1L;
		nonExistingProductId = 2L;
		dependentProductId = 3L;
		productName = "Play 5";
		
		product = ProductFactory.createProduct(productName);
		productDTO = new ProductDTO(product);
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findById(existingProductId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingProductId)).thenReturn(Optional.empty());
		Mockito.when(repository.searchByName(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.existsById(existingProductId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingProductId)).thenReturn(false);
		Mockito.when(repository.existsById(dependentProductId)).thenReturn(true);
		
		Mockito.doNothing().when(repository).deleteById(existingProductId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductId);

		
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.findById(existingProductId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingProductId);
		Assertions.assertEquals(result.getName(), product.getName());
	}
	
	@Test
	public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingProductId);
		});
	}
	
	@Test
	public void findAllShouldReturnPagedProductMinDTO() {
		
		Pageable pageable = PageRequest.of(0, 12);
		String name = "Play 5";
		
		Page<ProductMinDTO> result = service.findAll(name, pageable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.iterator().next().getName(), productName);
	}
	
	@Test
	public void insertShouldReturnProductDTO() {
		
		ProductDTO result = service.insert(productDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), product.getName());
		Assertions.assertEquals(result.getPrice(), product.getPrice());
		Assertions.assertFalse(result.getCategories().isEmpty());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.update(existingProductId, productDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingProductId, result.getId());
	    Assertions.assertEquals(productDTO.getName(), result.getName());
	    Assertions.assertEquals(productDTO.getDescription(), result.getDescription());
	    Assertions.assertFalse(result.getCategories().isEmpty());
	    Assertions.assertEquals(productDTO.getCategories().size(), result.getCategories().size());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingProductId, productDTO);
		});
		
		Mockito.verify(repository).getReferenceById(nonExistingProductId);
		Mockito.verify(repository, Mockito.never()).save(Mockito.any());
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingProductId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingProductId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingProductId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentProductId);
		});
	}
}
