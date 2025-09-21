package com.devsuperior.dscommerce.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderStatus;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.OrderFactory;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {
	
	@InjectMocks
	private OrderService service;
	
	@Mock
	private OrderRepository	repository;
	
	@Mock
	private AuthService authService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private OrderItemRepository orderItemRepository;
	
	private Long existingOrderId, nonExistingOrderId;
	private long existingProductId, nonExistingProductId;
	private Order order;
	private OrderDTO dto;
	private User admin, client;
	private Product product;
	
	@BeforeEach
	private void setUp() throws Exception {
		
		existingOrderId = 1L;
		nonExistingOrderId = 2L;
		existingProductId = 1L;
		nonExistingProductId = 2L;
		
		admin = UserFactory.createCustomAdminUser(1L, "Jef");
		client = UserFactory.createCustomClientUser(2L, "Bob");	
		
		order = OrderFactory.createOrder(client);
		dto = new OrderDTO(order);
		
		product = ProductFactory.createProduct();
		
		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());
		
		Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.save(any())).thenReturn(order);
		
		Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
	}
	
	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {
		
		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
		
		OrderDTO result = service.findById(existingOrderId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}
	
	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {
		
		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
		
		OrderDTO result = service.findById(existingOrderId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}
	
	@Test
	public void findByIdShouldThrowsForbiddenExceptionWhenIdExistsAndOtherClientLogged() {
		
		Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			OrderDTO result = service.findById(existingOrderId);
		});
	}
	
	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			OrderDTO result = service.findById(nonExistingOrderId);
		});
	}
	
	@Test 
	public void insertShouldReturnOrderDTOWhenAdminLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(admin);
		
		OrderDTO result = service.insert(dto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
		Assertions.assertEquals(result.getStatus(), OrderStatus.WAITING_PAYMENT);
	}

	@Test 
	public void insertShouldReturnOrderDTOWhenClientLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(client);
		
		OrderDTO result = service.insert(dto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}

	@Test
	public void insertShouldThrowsUsernameNotFoundExceptionWhenUserNotLogged() {
		
		Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();
		
		order.setClient(new User());
		dto = new OrderDTO(order);
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			OrderDTO result = service.insert(dto);
		});
	}
	
	@Test
	public void insertShouldThrowsEntityNotFoundExceptionWhenOrderProductIdDoesNotExist() {
		
		Mockito.when(userService.authenticated()).thenReturn(client);
		
		product.setId(nonExistingOrderId);
		OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
		order.getItems().add(orderItem);
		
		dto = new OrderDTO(order);
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			OrderDTO result = service.insert(dto);
		});
	}
}
