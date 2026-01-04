package org.example.productservice.service;

import jakarta.transaction.Transactional;
import org.example.productservice.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public void save(Product product) {
            productRepository.save(product);
    }

    @Transactional
    @Override
    public void update(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
if(product.getName() != null)
    existingProduct.setName(product.getName());
if(product.getDescription() != null)
    existingProduct.setDescription(product.getDescription());
if (product.getPrice() != null)
        existingProduct.setPrice(product.getPrice());
if (product.getQuantity() != 0)
        existingProduct.setQuantity(product.getQuantity());

    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
