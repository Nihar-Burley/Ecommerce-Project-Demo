PROJECT STRUCTURE (CART SERVICE)

com.company.cart
│
├── CartServiceApplication.java
│
├── controller
│     └── CartController.java
│
├── service
│     ├── CartService.java
│     └── impl
│           └── CartServiceImpl.java
│
├── repository
│     ├── CartRepository.java
│     └── CartItemRepository.java
│
├── model
│     ├── Cart.java
│     └── CartItem.java
│
├── dto
│     ├── request
│     │     ├── AddToCartRequest.java
│     │     ├── UpdateCartRequest.java
│     │     └── RemoveCartItemRequest.java (optional)
│     │
│     └── response
│           ├── CartResponse.java
│           └── CartItemResponse.java
│
├── client
│     └── ProductClient.java
│
├── exception
│     ├── GlobalExceptionHandler.java
│     ├── ProductNotFoundException.java
│     ├── CartNotFoundException.java
│     ├── InsufficientStockException.java
│     └── BadRequestException.java
│
├── config
│     ├── WebClientConfig.java
│     ├── RouterConfig.java (optional for functional style)
│     └── OpenApiConfig.java (optional)
│
├── mapper
│     └── CartMapper.java
│
├── util
│     ├── Constants.java
│     └── ValidationUtil.java (optional)
│
├── security
│     └── JwtAuthenticationFilter.java (we add later)
│
├── enums
│     └── ErrorCode.java (optional but good practice)
│
└── test
      ├── unit
      │     ├── service
      │     │     └── CartServiceTest.java
      │     └── controller
      │           └── CartControllerTest.java
      │
      └── cucumber
            ├── features
            │     └── cart.feature
            │
            ├── stepdefinitions
            │     └── CartStepDefinition.java
            │
            └── runner
                  └── CucumberTestRunner.java







                  Endpoint Alignment (VERY IMPORTANT)

                  Your Product Service must have:

                  GET    /products/{id}
                  PUT    /products/{id}/reduce/{qty}
                  PUT    /products/{id}/increase/{qty}