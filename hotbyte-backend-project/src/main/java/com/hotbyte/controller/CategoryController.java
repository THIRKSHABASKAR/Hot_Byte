package com.hotbyte.controller;

import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.CategoryResponse;
import com.hotbyte.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories",
     description = "Menu category APIs")
public class CategoryController {

    private final MenuService menuService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>>
    getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(
                "Categories fetched successfully",
                menuService.getAllCategories()));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary =
            "Get categories by restaurant")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>>
    getCategoriesByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Categories fetched successfully",
                menuService.getCategoriesByRestaurant(
                        restaurantId)));
    }
}