package org.example.productservice.service;

import org.example.productservice.entity.Product;

import java.util.List;

public interface ProductService {
    public List<Product> findAll();
    public Product findById(Long id);
    public void save(Product product);
    public void update(Long id, Product product);
    public void deleteById(Long id);
}
