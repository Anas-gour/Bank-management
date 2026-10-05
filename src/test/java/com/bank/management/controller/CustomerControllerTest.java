package com.bank.management.controller;

import com.bank.management.entity.Customer;
import com.bank.management.service.CustomerService;
import com.bank.management.service.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.bank.management.controller.CustomerController;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldGetAllCustomers() throws Exception {

        when(customerService.getAllCustomers())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldGetCustomerById() throws Exception {

        Customer customer = new Customer();

        customer.setName("Anas");
        customer.setEmail("anas@gmail.com");
        customer.setAddress("Bangalore");

        when(customerService.getCustomerById(21L))
                .thenReturn(customer);

        mockMvc.perform(get("/api/customers/21"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                {
                    "name": "Anas",
                    "email": "anas@gmail.com",
                    "address": "Bangalore"
                }
                """));
    }

    @Test
    void shouldCreateCustomer() throws Exception {

        Customer customer = new Customer();

        customer.setName("Anas");
        customer.setEmail("anas@gmail.com");
        customer.setPhone("9876543210");
        customer.setAddress("Bangalore");

        when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(customer);

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "name": "Anas",
                                        "email": "anas@gmail.com",
                                        "phone": "9876543210",
                                        "address": "Bangalore"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "name": "Anas",
                        "email": "anas@gmail.com",
                        "phone": "9876543210",
                        "address": "Bangalore"
                    }
                    """));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {

        Customer customer = new Customer();

        customer.setName("Anas Updated");
        customer.setEmail("anasupdated@gmail.com");
        customer.setPhone("9876543210");
        customer.setAddress("Bangalore");

        when(customerService.updateCustomer(
                eq(21L),
                any(Customer.class)
        )).thenReturn(customer);

        mockMvc.perform(
                        put("/api/customers/21")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "name": "Anas Updated",
                                        "email": "anasupdated@gmail.com",
                                        "phone": "9876543210",
                                        "address": "Bangalore"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "name": "Anas Updated",
                        "email": "anasupdated@gmail.com",
                        "phone": "9876543210",
                        "address": "Bangalore"
                    }
                    """));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {

        mockMvc.perform(
                        delete("/api/customers/21")
                )
                .andExpect(status().isOk());
    }
}
