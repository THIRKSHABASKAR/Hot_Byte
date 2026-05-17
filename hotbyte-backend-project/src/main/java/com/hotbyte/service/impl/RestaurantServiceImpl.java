package com.hotbyte.service.impl;

import com.hotbyte.dto.request.*;
import com.hotbyte.dto.response.*;
import com.hotbyte.entity.*;
import com.hotbyte.exception.*;
import com.hotbyte.repository.*;
import com.hotbyte.service.NotificationService;
import com.hotbyte.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl
        implements RestaurantService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    RestaurantServiceImpl.class);

    private final RestaurantRepository
            restaurantRepository;
    private final MenuItemRepository
            menuItemRepository;
    private final MenuCategoryRepository
            categoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final NotificationService
            notificationService;

    // ── Get restaurant of logged in owner ──
    private Restaurant getRestaurantByUserId(
            Long userId) {
        return restaurantRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurant not found."
                                + " Please contact admin."));
    }

    @Override
    public RestaurantResponse getMyRestaurant(
            Long userId) {
        return mapToRestaurantResponse(
                getRestaurantByUserId(userId));
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(
            Long userId,
            CreateRestaurantRequest request) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        if (request.getName() != null)
            restaurant.setName(request.getName());
        if (request.getDescription() != null)
            restaurant.setDescription(
                    request.getDescription());
        if (request.getLocation() != null)
            restaurant.setLocation(
                    request.getLocation());
        if (request.getCity() != null)
            restaurant.setCity(request.getCity());
        if (request.getPhone() != null)
            restaurant.setPhone(request.getPhone());
        if (request.getImageUrl() != null)
            restaurant.setImageUrl(
                    request.getImageUrl());
        restaurantRepository.save(restaurant);
        return mapToRestaurantResponse(restaurant);
    }

    @Override
    public RestaurantDashboardResponse getDashboard(
            Long userId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);

        List<Order> allOrders = orderRepository
                .findByRestaurantIdOrderByPlacedAtDesc(
                        restaurant.getId());

        int total = allOrders.size();
        int pending = (int) allOrders.stream()
                .filter(o ->
                        o.getStatus() ==
                        Order.OrderStatus.PLACED ||
                        o.getStatus() ==
                        Order.OrderStatus.CONFIRMED ||
                        o.getStatus() ==
                        Order.OrderStatus.PROCESSING)
                .count();
        int completed = (int) allOrders.stream()
                .filter(o ->
                        o.getStatus() ==
                        Order.OrderStatus.DELIVERED)
                .count();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o ->
                        o.getStatus() ==
                        Order.OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);

        // Today's revenue
        LocalDateTime startOfDay =
                LocalDate.now().atStartOfDay();
        BigDecimal todayRevenue = allOrders.stream()
                .filter(o ->
                        o.getStatus() ==
                        Order.OrderStatus.DELIVERED &&
                        o.getPlacedAt() != null &&
                        o.getPlacedAt().isAfter(
                                startOfDay))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);

        int menuCount = menuItemRepository
                .findByRestaurantId(restaurant.getId())
                .size();
        int catCount = categoryRepository
                .findByRestaurantId(restaurant.getId())
                .size();

        RestaurantDashboardResponse response =
                new RestaurantDashboardResponse();
        response.setRestaurantId(restaurant.getId());
        response.setRestaurantName(restaurant.getName());
        response.setImageUrl(restaurant.getImageUrl());
        response.setTotalMenuItems(menuCount);
        response.setTotalOrders(total);
        response.setPendingOrders(pending);
        response.setCompletedOrders(completed);
        response.setTotalRevenue(totalRevenue);
        response.setTodayRevenue(todayRevenue);
        response.setTotalCategories(catCount);
        return response;
    }

    @Override
    @Transactional
    public MenuItemResponse addMenuItem(
            Long userId,
            AddMenuItemRequest request) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);

        MenuCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"));

        // Make sure category belongs to restaurant
        if (!category.getRestaurant().getId()
                .equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Category does not belong"
                    + " to your restaurant");
        }

        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIngredients(request.getIngredients());
        item.setPrice(request.getPrice());
        item.setDiscountPrice(
                request.getDiscountPrice());
        item.setImageUrl(request.getImageUrl());
        item.setCalories(request.getCalories());
        item.setFats(request.getFats());
        item.setProteins(request.getProteins());
        item.setCarbohydrates(
                request.getCarbohydrates());
        item.setCookingTime(request.getCookingTime());
        item.setIsAvailable(true);

        // Set enums safely
        if (request.getFoodType() != null) {
            try {
                item.setFoodType(
                        MenuItem.FoodType.valueOf(
                                request.getFoodType()
                                .toUpperCase()));
            } catch (Exception e) {
                item.setFoodType(MenuItem.FoodType.VEG);
            }
        }
        if (request.getAvailability() != null) {
            try {
                item.setAvailability(
                        MenuItem.Availability.valueOf(
                                request.getAvailability()
                                .toUpperCase()));
            } catch (Exception e) {
                item.setAvailability(
                        MenuItem.Availability.ALL_DAY);
            }
        }
        if (request.getTasteInfo() != null) {
            try {
                item.setTasteInfo(
                        MenuItem.TasteInfo.valueOf(
                                request.getTasteInfo()
                                .toUpperCase()));
            } catch (Exception ignored) {}
        }

        MenuItem saved =
                menuItemRepository.save(item);
        logger.info("Menu item added: {} by restaurant: {}",
                saved.getName(), restaurant.getName());
        return mapToMenuItemResponse(saved);
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(
            Long userId, Long itemId,
            AddMenuItemRequest request) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        MenuItem item = menuItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found"));

        if (!item.getRestaurant().getId()
                .equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Item does not belong"
                    + " to your restaurant");
        }

        if (request.getName() != null)
            item.setName(request.getName());
        if (request.getDescription() != null)
            item.setDescription(
                    request.getDescription());
        if (request.getIngredients() != null)
            item.setIngredients(
                    request.getIngredients());
        if (request.getPrice() != null)
            item.setPrice(request.getPrice());
        if (request.getDiscountPrice() != null)
            item.setDiscountPrice(
                    request.getDiscountPrice());
        if (request.getImageUrl() != null)
            item.setImageUrl(request.getImageUrl());
        if (request.getCalories() != null)
            item.setCalories(request.getCalories());
        if (request.getFats() != null)
            item.setFats(request.getFats());
        if (request.getProteins() != null)
            item.setProteins(request.getProteins());
        if (request.getCarbohydrates() != null)
            item.setCarbohydrates(
                    request.getCarbohydrates());
        if (request.getCookingTime() != null)
            item.setCookingTime(
                    request.getCookingTime());
        if (request.getCategoryId() != null) {
            MenuCategory cat = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Category not found"));
            item.setCategory(cat);
        }
        if (request.getFoodType() != null) {
            try {
                item.setFoodType(
                        MenuItem.FoodType.valueOf(
                                request.getFoodType()
                                .toUpperCase()));
            } catch (Exception ignored) {}
        }

        menuItemRepository.save(item);
        return mapToMenuItemResponse(item);
    }

    @Override
    @Transactional
    public void deleteMenuItem(
            Long userId, Long itemId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        MenuItem item = menuItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found"));
        if (!item.getRestaurant().getId()
                .equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Not authorized");
        }
        menuItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void toggleItemAvailability(
            Long userId, Long itemId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        MenuItem item = menuItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found"));
        if (!item.getRestaurant().getId()
                .equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Not authorized");
        }
        item.setIsAvailable(!item.getIsAvailable());
        menuItemRepository.save(item);
    }

    @Override
    public List<MenuItemResponse> getMyMenuItems(
            Long userId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        return menuItemRepository
                .findByRestaurantId(restaurant.getId())
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse addCategory(
            Long userId,
            AddCategoryRequest request) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);

        if (categoryRepository
                .existsByNameAndRestaurantId(
                        request.getName(),
                        restaurant.getId())) {
            throw new BadRequestException(
                    "Category already exists");
        }

        MenuCategory cat = new MenuCategory();
        cat.setRestaurant(restaurant);
        cat.setName(request.getName());
        cat.setDescription(request.getDescription());
        cat.setImageUrl(request.getImageUrl());
        cat.setIsActive(true);

        MenuCategory saved =
                categoryRepository.save(cat);
        return mapToCategoryResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(
            Long userId, Long categoryId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        MenuCategory cat = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"));
        if (!cat.getRestaurant().getId()
                .equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Not authorized");
        }
        categoryRepository.delete(cat);
    }

    @Override
    public List<CategoryResponse> getMyCategories(
            Long userId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        return categoryRepository
                .findByRestaurantId(restaurant.getId())
                .stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getMyOrders(
            Long userId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        return orderRepository
                .findByRestaurantIdOrderByPlacedAtDesc(
                        restaurant.getId())
                .stream()
                .filter(o ->
                        o.getStatus() !=
                        Order.OrderStatus.DELIVERED &&
                        o.getStatus() !=
                        Order.OrderStatus.CANCELLED)
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long userId, Long orderId,
            UpdateOrderStatusRequest request) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found"));

        if (!order.getRestaurant().getId()
                .equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Not authorized");
        }

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(
                    request.getStatus().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException(
                    "Invalid status: "
                    + request.getStatus());
        }

        order.setStatus(newStatus);

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case CONFIRMED ->
                order.setConfirmedAt(now);
            case DISPATCHED ->
                order.setDispatchedAt(now);
            case DELIVERED ->
                order.setDeliveredAt(now);
            case CANCELLED ->
                order.setCancelledAt(now);
            default -> {}
        }

        orderRepository.save(order);

        // Notify customer
        String msg = switch (newStatus) {
            case CONFIRMED ->
                "Your order #" + orderId
                + " has been confirmed! 🎉";
            case PROCESSING ->
                "Your order #" + orderId
                + " is being prepared 👨‍🍳";
            case DISPATCHED ->
                "Your order #" + orderId
                + " is on the way! 🛵";
            case DELIVERED ->
                "Your order #" + orderId
                + " has been delivered! Enjoy 😋";
            case CANCELLED ->
                "Your order #" + orderId
                + " has been cancelled.";
            default -> "Order status updated.";
        };

        Notification.NotificationType notifType =
                switch (newStatus) {
            case CONFIRMED ->
                Notification.NotificationType
                        .ORDER_CONFIRMED;
            case DISPATCHED ->
                Notification.NotificationType
                        .ORDER_DISPATCHED;
            case DELIVERED ->
                Notification.NotificationType
                        .ORDER_DELIVERED;
            case CANCELLED ->
                Notification.NotificationType
                        .ORDER_CANCELLED;
            default ->
                Notification.NotificationType.GENERAL;
        };

        notificationService.createNotification(
                order.getUser().getId(),
                "Order Update",
                msg,
                notifType);

        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrderHistory(
            Long userId) {
        Restaurant restaurant =
                getRestaurantByUserId(userId);
        return orderRepository
                .findByRestaurantIdOrderByPlacedAtDesc(
                        restaurant.getId())
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // ── Mappers ──────────────────────────────────

    private RestaurantResponse mapToRestaurantResponse(
            Restaurant r) {
        RestaurantResponse res =
                new RestaurantResponse();
        res.setId(r.getId());
        res.setName(r.getName());
        res.setDescription(r.getDescription());
        res.setLocation(r.getLocation());
        res.setCity(r.getCity());
        res.setPhone(r.getPhone());
        res.setImageUrl(r.getImageUrl());
        res.setAvgRating(r.getAvgRating());
        res.setTotalRatings(r.getTotalRatings());
        res.setIsActive(r.getIsActive());
        if (r.getOpeningTime() != null)
            res.setOpeningTime(
                    r.getOpeningTime().toString());
        if (r.getClosingTime() != null)
            res.setClosingTime(
                    r.getClosingTime().toString());
        if (r.getUser() != null)
            res.setUserId(r.getUser().getId());
        return res;
    }

    private MenuItemResponse mapToMenuItemResponse(
            MenuItem item) {
        MenuItemResponse res = new MenuItemResponse();
        res.setId(item.getId());
        res.setName(item.getName());
        res.setDescription(item.getDescription());
        res.setIngredients(item.getIngredients());
        res.setPrice(item.getPrice());
        res.setDiscountPrice(item.getDiscountPrice());
        res.setImageUrl(item.getImageUrl());
        res.setIsAvailable(item.getIsAvailable());
        res.setAvgRating(item.getAvgRating());
        res.setTotalRatings(item.getTotalRatings());
        res.setCookingTime(item.getCookingTime());
        res.setCalories(item.getCalories());
        res.setFats(item.getFats());
        res.setProteins(item.getProteins());
        res.setCarbohydrates(item.getCarbohydrates());
        if (item.getFoodType() != null)
            res.setFoodType(
                    item.getFoodType().name());
        if (item.getAvailability() != null)
            res.setAvailability(
                    item.getAvailability().name());
        if (item.getTasteInfo() != null)
            res.setTasteInfo(
                    item.getTasteInfo().name());
        if (item.getRestaurant() != null) {
            res.setRestaurantId(
                    item.getRestaurant().getId());
            res.setRestaurantName(
                    item.getRestaurant().getName());
        }
        if (item.getCategory() != null) {
            res.setCategoryId(
                    item.getCategory().getId());
            res.setCategoryName(
                    item.getCategory().getName());
        }
        return res;
    }

    private CategoryResponse mapToCategoryResponse(
            MenuCategory cat) {
        CategoryResponse res = new CategoryResponse();
        res.setId(cat.getId());
        res.setName(cat.getName());
        res.setDescription(cat.getDescription());
        res.setImageUrl(cat.getImageUrl());
        if (cat.getRestaurant() != null) {
            res.setRestaurantId(
                    cat.getRestaurant().getId());
            res.setRestaurantName(
                    cat.getRestaurant().getName());
        }
        return res;
    }

    private OrderResponse mapToOrderResponse(
            Order order) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getId());
        res.setDeliveryAddress(
                order.getDeliveryAddress());
        res.setDeliveryPhone(order.getDeliveryPhone());
        res.setSubtotal(order.getSubtotal());
        res.setDeliveryFee(order.getDeliveryFee());
        res.setCouponDiscount(
                order.getCouponDiscount());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus().name());
        res.setPlacedAt(order.getPlacedAt());
        res.setEstimatedDelivery(
                order.getEstimatedDelivery());
        res.setDeliveredAt(order.getDeliveredAt());
        if (order.getPaymentMethod() != null)
            res.setPaymentMethod(
                    order.getPaymentMethod().name());
        if (order.getPaymentStatus() != null)
            res.setPaymentStatus(
                    order.getPaymentStatus().name());
        if (order.getRestaurant() != null) {
            res.setRestaurantName(
                    order.getRestaurant().getName());
            res.setRestaurantImage(
                    order.getRestaurant().getImageUrl());
        }
        if (order.getOrderItems() != null) {
            res.setItems(order.getOrderItems()
                    .stream()
                    .map(item -> {
                        OrderItemResponse oir =
                                new OrderItemResponse();
                        oir.setMenuItemId(
                                item.getMenuItem()
                                .getId());
                        oir.setItemName(
                                item.getItemName());
                        oir.setUnitPrice(
                                item.getUnitPrice());
                        oir.setQuantity(
                                item.getQuantity());
                        oir.setTotalPrice(
                                item.getTotalPrice());
                        return oir;
                    })
                    .collect(Collectors.toList()));
        }
        return res;
    }
}