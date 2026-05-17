
<p align="center">
  <strong>HOTBYTE-FOOD DELIVERY PLATFORM</strong>
</p>

<div align="center">

![HotByte Banner](https://images.unsplash.com/photo-1513104890138-7c749659a591?w=1200&h=400&fit=crop&q=80)

<p align="center">
  <strong>Hot food. On time. Every time.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/React-18.x-61DAFB?style=for-the-badge&logo=react&logoColor=black" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Razorpay-Payment-0C2451?style=for-the-badge&logo=razorpay&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-api-docs">API Docs</a> •
  <a href="#-database-schema">Database</a>
</p>

</div>

---

## 📖 About

**HotByte** is a full-stack food delivery web application that connects customers with local restaurants. Built with React.js and Spring Boot, it supports real-time ordering, Razorpay payment processing, Google OAuth login, and role-based access for three user types — Customer, Restaurant Owner, and Admin.

> 🎓 Built as a full-stack capstone project demonstrating end-to-end web application development.

---

## ✨ Features

### 👤 Customer
- 🔍 Browse restaurants and menu items by category
- 🛒 Add to cart, manage quantities, apply coupon codes
- 💳 Pay via UPI, Card, Net Banking, Wallets (Razorpay) or Cash on Delivery
- 📍 Auto-detect delivery address using GPS
- 📦 View order history and track order status
- 🔐 Login with Email/Password or Google OAuth
- 💰 Wallet management and notifications

### 🍽️ Restaurant Owner
- 📊 Dashboard with revenue and order analytics
- ➕ Add, edit, delete menu items with images
- 🗂️ Manage food categories with custom images
- 📋 View and update order status in real-time
- 🔄 Toggle item availability on/off

### 🛡️ Admin
- 👥 Manage all users and restaurants
- 📦 View all orders across the platform
- ✅ Activate / deactivate restaurant accounts
- 📈 Platform-wide analytics and monitoring

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | React.js 18, Bootstrap 5, React Router v6, Context API |
| **Backend** | Spring Boot 3, Spring Security, JPA / Hibernate |
| **Database** | MySQL 8 |
| **Authentication** | JWT Tokens, Google OAuth 2.0 |
| **Payments** | Razorpay (UPI, Cards, Net Banking, Wallets) |
| **Geolocation** | Nominatim OpenStreetMap API |
| **Icons** | React Icons (Font Awesome) |
| **Notifications** | React Hot Toast |

---

## 🗂️ Project Structure

```
HOTBYTE/
│
├── hotbyte-frontend/          # React.js Frontend
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/        # Navbar, Footer, FoodCard
│   │   ├── context/           # AuthContext, CartContext
│   │   ├── pages/             # All page components
│   │   │   ├── admin/
│   │   │   ├── restaurant/
│   │   │   └── *.js
│   │   ├── services/          # API service functions
│   │   ├── App.js
│   │   └── index.js
│   ├── .env
│   └── package.json
│
└── hotbyte-backend/           # Spring Boot Backend
    ├── src/main/java/com/hotbyte/
    │   ├── controller/        # REST Controllers
    │   ├── service/           # Business Logic
    │   ├── repository/        # JPA Repositories
    │   ├── entity/            # Database Entities
    │   ├── dto/               # Request/Response DTOs
    │   ├── security/          # JWT, Spring Security
    │   ├── config/            # CORS, Security Config
    │   └── exception/         # Global Exception Handler
    ├── src/main/resources/
    │   └── application.properties
    └── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- [Node.js](https://nodejs.org/) (v18+)
- [Java JDK](https://adoptium.net/) (v17+)
- [Maven](https://maven.apache.org/) (v3.8+)
- [MySQL](https://dev.mysql.com/downloads/) (v8+)
- A [Razorpay account](https://razorpay.com) (for payments)
- A [Google Cloud Console](https://console.cloud.google.com) project (for OAuth)

---

### 🗄️ Database Setup

```sql
CREATE DATABASE hotbyte;
```

Update `application.properties` with your MySQL credentials (see Backend Setup below).

---

### ⚙️ Backend Setup

**1. Clone the repository**

```bash
git clone https://github.com/yourusername/hotbyte.git
cd hotbyte/hotbyte-backend
```

**2. Configure `src/main/resources/application.properties`**

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/hotbyte
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your_jwt_secret_key_here
jwt.expiration=86400000

# Razorpay
razorpay.key.id=rzp_test_your_key_id
razorpay.key.secret=your_key_secret

# Google OAuth
google.client.id=your_google_client_id
```

**3. Run the backend**

```bash
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`

---

### 💻 Frontend Setup

**1. Navigate to frontend directory**

```bash
cd hotbyte/hotbyte-frontend
```

**2. Install dependencies**

```bash
npm install
```

**3. Create `.env` file in the root of the frontend folder**

```env
REACT_APP_RAZORPAY_KEY_ID=rzp_test_your_key_id
```

**4. Add Razorpay script to `public/index.html`** (before closing `</body>`)

```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

**5. Run the frontend**

```bash
npm start
```

Frontend runs at `http://localhost:3000`

---

## 🔌 API Docs

Base URL: `http://localhost:8080/api/v1`

### Auth Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/register` | Register new user | ❌ |
| POST | `/auth/login` | Login with email & password | ❌ |
| POST | `/auth/google` | Login with Google OAuth token | ❌ |

### Menu Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/menu` | Get all menu items | ❌ |
| GET | `/menu/:id` | Get single menu item | ❌ |
| GET | `/categories` | Get all categories | ❌ |

### Order Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/orders/place` | Place a new order | ✅ USER |
| GET | `/orders/my` | Get my orders | ✅ USER |
| PUT | `/orders/:id/cancel` | Cancel an order | ✅ USER |

### Payment Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/payment/create-order` | Create Razorpay order | ✅ USER |
| POST | `/payment/verify` | Verify payment signature | ✅ USER |

### Restaurant Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/restaurant/dashboard` | Get dashboard stats | ✅ RESTAURANT |
| GET | `/restaurant/menu` | Get my menu items | ✅ RESTAURANT |
| POST | `/restaurant/menu/add` | Add menu item | ✅ RESTAURANT |
| PUT | `/restaurant/menu/:id` | Update menu item | ✅ RESTAURANT |
| DELETE | `/restaurant/menu/:id` | Delete menu item | ✅ RESTAURANT |
| GET | `/restaurant/categories` | Get my categories | ✅ RESTAURANT |
| POST | `/restaurant/categories/add` | Add category | ✅ RESTAURANT |
| GET | `/restaurant/orders` | Get active orders | ✅ RESTAURANT |
| PUT | `/restaurant/orders/:id/status` | Update order status | ✅ RESTAURANT |

---

## 🗃️ Database Schema

```
users              restaurants         menu_items
─────────────      ───────────────     ──────────────────
id (PK)            id (PK)             id (PK)
name               name                name
email              address             price
password           cuisine_type        discount_price
role               is_open             food_type
phone              user_id (FK)        image_url
wallet_balance     rating              category_id (FK)
is_active          image_url           restaurant_id (FK)
                                       is_available

orders             cart_items          categories
──────────         ──────────          ──────────────
id (PK)            id (PK)             id (PK)
user_id (FK)       user_id (FK)        name
restaurant_id(FK)  menu_item_id (FK)   image_url
total              quantity            restaurant_id (FK)
status             unit_price
payment_method
delivery_address
coupon_code
```

---

## 🔐 Environment Variables

### Frontend (`.env`)

| Variable | Description |
|----------|-------------|
| `REACT_APP_RAZORPAY_KEY_ID` | Your Razorpay public key ID |

### Backend (`application.properties`)

| Variable | Description |
|----------|-------------|
| `spring.datasource.url` | MySQL connection URL |
| `spring.datasource.username` | MySQL username |
| `spring.datasource.password` | MySQL password |
| `jwt.secret` | Secret key for signing JWT tokens |
| `jwt.expiration` | Token expiry in milliseconds |
| `razorpay.key.id` | Razorpay Key ID |
| `razorpay.key.secret` | Razorpay Key Secret |
| `google.client.id` | Google OAuth Client ID |

---

## 💳 Test Payments (Razorpay Test Mode)

Use these test credentials to simulate payments without real money:

| Payment Method | Test Credentials |
|---------------|-----------------|
| **Card** | `4111 1111 1111 1111` • Any future expiry • Any CVV |
| **UPI (Success)** | `success@razorpay` |
| **UPI (Failure)** | `failure@razorpay` |
| **Net Banking** | Select any bank → Use test credentials shown |

> ⚠️ Switch to `rzp_live_` keys when deploying to production.

---

## 🎭 Default User Roles

To test all three portals, create accounts with these roles:

| Role | How to Access |
|------|--------------|
| **Customer** | Register normally via `/register` |
| **Restaurant** | Register → Update role to `RESTAURANT` in DB |
| **Admin** | Register → Update role to `ADMIN` in DB |

```sql
-- Make a user an Admin
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';

-- Make a user a Restaurant owner
UPDATE users SET role = 'RESTAURANT' WHERE email = 'restaurant@example.com';
```

---

## 📁 Key Dependencies

### Frontend (`package.json`)

```json
{
  "react": "^18.x",
  "react-router-dom": "^6.x",
  "axios": "^1.x",
  "bootstrap": "^5.x",
  "react-icons": "^4.x",
  "react-hot-toast": "^2.x",
  "@react-oauth/google": "^0.x"
}
```

### Backend (`pom.xml`)

```xml
<!-- Spring Boot Starter Web       -->
<!-- Spring Boot Starter Security  -->
<!-- Spring Boot Starter Data JPA  -->
<!-- MySQL Connector               -->
<!-- JJWT (JWT)                    -->
<!-- Razorpay Java SDK             -->
<!-- Google API Client (OAuth)     -->
<!-- Lombok                        -->
<!-- SpringDoc OpenAPI (Swagger)   -->
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Thirksha Baskar**
- GitHub: [Thirksha Baskar](https://github.com/THIRKSHABASKAR)
- LinkedIn: [Thirksha Baskar](www.linkedin.com/in/thirksha-baskar-464694254)

---

<div align="center">

Made with ❤️ and <img src="https://img.shields.io/badge/🔥-E23744?style=flat-square&labelColor=E23744&color=E23744" alt="fire" /> by the HotByte Team

⭐ **Star this repo if you found it helpful!** ⭐

</div>
