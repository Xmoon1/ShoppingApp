package com.example.waikan.controllers;

import com.example.waikan.models.Product;
import com.example.waikan.models.User;
import com.example.waikan.services.ProductService;
import com.example.waikan.validation.ResponseErrorValidation;
import org.springframework.mail.MailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
public class ProductController {
    private final MailSender mailSender;
    private final ResponseErrorValidation responseErrorValidation;
    private final ProductService productService;

    public ProductController(MailSender mailSender, ResponseErrorValidation responseErrorValidation,
                             ProductService productService) {
        this.mailSender = mailSender;
        this.responseErrorValidation = responseErrorValidation;
        this.productService = productService;
    }

    @GetMapping("/")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @PostMapping("/product/create")
    public String saveProduct(@AuthenticationPrincipal User user,
                              @RequestParam("file1") MultipartFile file1,
                              @RequestParam("file2") MultipartFile file2,
                              @RequestParam("file3") MultipartFile file3,
                              @Valid Product product, BindingResult result, Model model)
            throws IOException {
        Map<String, String> errors = responseErrorValidation.mapValidationService(result);
        if (errors != null) {
            model.mergeAttributes(errors);
            return "products";
        } else {
            productService.saveProduct(user, product, file1, file2, file3);
            return "redirect:/";
        }
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable("id") Product product, Model model) {
        model.addAttribute("product", product);
        model.addAttribute("reviews", product.getReviews());
        model.addAttribute("images", product.getImages());
        return "product-info";
    }

    @GetMapping("/my/products")
    public String userProduct(@AuthenticationPrincipal User user, Model model) {
        List<Product> userProducts = user.getProducts();
        model.addAttribute("products", userProducts);
        model.addAttribute("productsCount", userProducts.size());
        return "my-products";
    }
}
