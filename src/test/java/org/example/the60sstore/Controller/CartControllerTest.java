package org.example.the60sstore.Controller;
import jakarta.servlet.http.HttpServletRequest;
import org.example.the60sstore.Entity.Product;
import org.example.the60sstore.Entity.ProductPrice;
import org.example.the60sstore.Service.LanguageService;
import org.example.the60sstore.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private LanguageService languageService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testToCart() throws Exception {

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1);
        product.setQuantity(2);

        List<ProductPrice> prices = new ArrayList<>();
        ProductPrice price = new ProductPrice();
        price.setPrice(100);
        prices.add(price);
        product.setProductPrices(prices);
        products.add(product);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", products);

        doNothing().when(languageService).addLanguagle(any(HttpServletRequest.class), any(Model.class));

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("store-cart"))
                .andExpect(model().attribute("products", products))
                .andExpect(model().attribute("total", 200));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testRemoveOutCart() throws Exception {
        // Giả lập session với danh sách Product
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1);
        product.setQuantity(2);
        products.add(product);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("cart", products);
        session.setAttribute("cartSize", 2);

        doNothing().when(languageService).addLanguagle(any(HttpServletRequest.class), any(Model.class));

        mockMvc.perform(get("/removeOutCart").param("productId", "1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:cart"))
                .andExpect(model().attributeExists("products"));

        List<Product> updatedCart = (List<Product>) session.getAttribute("cart");
        assert updatedCart != null;
        assert updatedCart.isEmpty();
        assert session.getAttribute("cartSize").equals(0);
    }
}
