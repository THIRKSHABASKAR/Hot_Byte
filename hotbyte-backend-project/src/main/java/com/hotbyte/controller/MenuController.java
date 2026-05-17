package com.hotbyte.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.MenuItemResponse;
import com.hotbyte.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu browsing APIs")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @Operation(summary = "Get all available menu items")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAllMenuItems() {
        return ResponseEntity.ok(ApiResponse.success(
                "Menu items fetched successfully",
                menuService.getAllMenuItems()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Menu item fetched successfully",
                menuService.getMenuItemById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter menu items")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> searchMenuItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String foodType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Search results fetched",
                menuService.searchMenuItems(name, foodType, categoryId, restaurantId)));
    }
}