# HomeLens REST API Specification

This document details all REST endpoints provided by the HomeLens backend service, including required authentication headers, request bodies, query parameters, and response structures.

- **Base URL:** `http://localhost:8080`
- **Interactive OpenAPI Documentation:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Spec:** `http://localhost:8080/v3/api-docs`

---

## 1. Authentication Endpoints (`/api/auth`)

### 1.1 Register New User
- **Method:** `POST`
- **Path:** `/api/auth/register`
- **Auth Required:** None (Public)
- **Request Body (`application/json`):**
  ```json
  {
    "email": "owner@homelens.com",
    "password": "Password123!",
    "fullName": "Priya Sharma",
    "phone": "+91-9876543210",
    "role": "ROLE_OWNER"
  }
  ```
- **Responses:**
  - `201 Created`:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "type": "Bearer",
      "id": 1,
      "email": "owner@homelens.com",
      "fullName": "Priya Sharma",
      "role": "ROLE_OWNER"
    }
    ```
  - `400 Bad Request`: Validation failure or duplicate email.

### 1.2 User Login
- **Method:** `POST`
- **Path:** `/api/auth/login`
- **Auth Required:** None (Public)
- **Request Body (`application/json`):**
  ```json
  {
    "email": "owner@homelens.com",
    "password": "Password123!"
  }
  ```
- **Responses:**
  - `200 OK`: Returns JWT `token`, `id`, `email`, `fullName`, and `role`.
  - `401 Unauthorized`: Bad credentials.

### 1.3 Get Current User Profile
- **Method:** `GET`
- **Path:** `/api/auth/me`
- **Auth Required:** `Bearer <JWT>` (Any authenticated user)
- **Responses:**
  - `200 OK`:
    ```json
    {
      "id": 1,
      "email": "owner@homelens.com",
      "fullName": "Priya Sharma",
      "phone": "+91-9876543210",
      "role": "ROLE_OWNER",
      "createdAt": "2026-11-12T10:00:00Z"
    }
    ```

---

## 2. Property Endpoints (`/api/properties`)

### 2.1 Search & Filter Properties
- **Method:** `GET`
- **Path:** `/api/properties`
- **Auth Required:** None (Public)
- **Query Parameters:**
  | Parameter | Type | Default | Description |
  | :--- | :--- | :--- | :--- |
  | `query` | string | `null` | Full-text keyword search in title and description |
  | `city` | string | `null` | Filter by city name (case-insensitive) |
  | `bhkType` | string | `null` | Filter by BHK (`1 RK`, `1 BHK`, `2 BHK`, `3 BHK`, `4 BHK`, `4+ BHK`) |
  | `minRent` | decimal | `null` | Minimum base monthly rent in INR (₹) |
  | `maxRent` | decimal | `null` | Maximum base monthly rent in INR (₹) |
  | `propertyType` | string | `null` | `GATED_COMMUNITY` or `INDIVIDUAL_HOUSE` |
  | `familyAllowed` | boolean | `null` | Filter properties allowing family occupancy |
  | `singleAllowed` | boolean | `null` | Filter properties allowing single/bachelor occupancy |
  | `waterAvailability` | boolean | `null` | Guaranteed continuous water availability |
  | `status` | string | `AVAILABLE` | `AVAILABLE` or `RENTED` |
  | `page` | integer | `0` | Zero-based page index |
  | `size` | integer | `12` | Items per page |
  | `sortBy` | string | `createdAt` | Field to sort by (`monthlyRent`, `createdAt`, `title`) |
  | `sortDir` | string | `desc` | Direction (`asc` or `desc`) |

- **Responses:**
  - `200 OK`: Returns paginated `PageResponse<PropertyResponse>`.

### 2.2 Get Property by ID
- **Method:** `GET`
- **Path:** `/api/properties/{id}`
- **Auth Required:** None (Public)
- **Responses:**
  - `200 OK`:
    ```json
    {
      "id": 10,
      "title": "Green Valley 3 BHK Gated Flat",
      "description": "Scenic 3 BHK flat with modern amenities.",
      "monthlyRent": 45000.00,
      "electricityCharges": 2500.00,
      "waterCharges": 800.00,
      "totalMonthlyCharges": 48300.00,
      "bhkType": "3 BHK",
      "propertyType": "GATED_COMMUNITY",
      "listingType": "FOR_RENT",
      "status": "AVAILABLE",
      "bedrooms": 3,
      "bathrooms": 3,
      "areaSqft": 1750.00,
      "waterAvailability": true,
      "familyAllowed": true,
      "singleAllowed": true,
      "distanceHospitalKm": 1.2,
      "distanceCollegeKm": 2.5,
      "distanceSchoolKm": 0.8,
      "distanceMetroKm": 0.5,
      "distanceBusStopKm": 0.2,
      "address": "402 Green Valley Park",
      "city": "Bengaluru",
      "state": "Karnataka",
      "postalCode": "560001",
      "images": [
        {
          "id": 1,
          "imageUrl": "/uploads/properties/e27df0b9-hall.jpg",
          "isPrimary": true,
          "displayOrder": 0
        }
      ],
      "owner": {
        "id": 1,
        "fullName": "Priya Sharma",
        "email": "owner@homelens.com",
        "phone": "+91-9876543210"
      }
    }
    ```

### 2.3 Register New Property
- **Method:** `POST`
- **Path:** `/api/properties`
- **Auth Required:** `Bearer <JWT>` (`ROLE_OWNER`, `ROLE_AGENT`, or `ROLE_ADMIN`)
- **Request Body (`application/json`):** Matches `CreatePropertyRequest`.
- **Responses:**
  - `201 Created`: Returns created `PropertyResponse`.
  - `400 Bad Request`: Validation failure.
  - `403 Forbidden`: Unauthorized role.

### 2.4 Update Property
- **Method:** `PUT`
- **Path:** `/api/properties/{id}`
- **Auth Required:** `Bearer <JWT>` (Listing Owner or Admin)
- **Request Body (`application/json`):** Matches `UpdatePropertyRequest`.
- **Responses:**
  - `200 OK`: Returns updated `PropertyResponse`.
  - `403 Forbidden`: Authenticated user does not own this property.

### 2.5 Change Property Availability Status
- **Method:** `PATCH`
- **Path:** `/api/properties/{id}/status`
- **Auth Required:** `Bearer <JWT>` (Listing Owner or Admin)
- **Request Body (`application/json`):**
  ```json
  {
    "status": "RENTED"
  }
  ```
- **Responses:**
  - `200 OK`: Returns updated `PropertyResponse` with new status.

### 2.6 Delete Property
- **Method:** `DELETE`
- **Path:** `/api/properties/{id}`
- **Auth Required:** `Bearer <JWT>` (Listing Owner or Admin)
- **Responses:**
  - `204 No Content`: Property and media deleted.

### 2.7 Get Owner's Registered Properties
- **Method:** `GET`
- **Path:** `/api/properties/my`
- **Auth Required:** `Bearer <JWT>` (`ROLE_OWNER`, `ROLE_AGENT`, `ROLE_ADMIN`)
- **Responses:**
  - `200 OK`: Paginated list of properties registered by authenticated user.

---

## 3. Media & Photograph Endpoints (`/api/properties/{id}/images`)

### 3.1 Upload Photograph
- **Method:** `POST`
- **Path:** `/api/properties/{id}/images`
- **Auth Required:** `Bearer <JWT>` (Listing Owner or Admin)
- **Content-Type:** `multipart/form-data`
- **Form Parameters:**
  - `file`: Image binary (`image/jpeg`, `image/png`, `image/webp`)
  - `isPrimary`: boolean (optional, default `false`)
- **Responses:**
  - `200 OK`: Returns updated `PropertyResponse` including new image in `images` array.

### 3.2 Delete Photograph
- **Method:** `DELETE`
- **Path:** `/api/properties/{id}/images/{imageId}`
- **Auth Required:** `Bearer <JWT>` (Listing Owner or Admin)
- **Responses:**
  - `200 OK`: Returns updated `PropertyResponse`.

---

## 4. Inquiries & Lead Management (`/api/inquiries`)

### 4.1 Submit Inquiry / Tour Request
- **Method:** `POST`
- **Path:** `/api/properties/{id}/inquiries`
- **Auth Required:** None (Public)
- **Request Body (`application/json`):**
  ```json
  {
    "name": "Rahul Verma",
    "email": "rahul.verma@example.com",
    "phone": "+91-9988776655",
    "message": "Hi, I am interested in renting this property. Can I visit this Saturday?"
  }
  ```
- **Responses:**
  - `201 Created`: Returns created `InquiryResponse` with status `NEW`.

### 4.2 Get Inquiries for Authenticated Owner
- **Method:** `GET`
- **Path:** `/api/inquiries/my`
- **Auth Required:** `Bearer <JWT>` (`ROLE_OWNER`, `ROLE_AGENT`, `ROLE_ADMIN`)
- **Responses:**
  - `200 OK`: Paginated list of inquiries received across all properties owned by user.

### 4.3 Update Inquiry Status
- **Method:** `PATCH`
- **Path:** `/api/inquiries/{id}/status`
- **Auth Required:** `Bearer <JWT>` (Property Owner or Admin)
- **Request Body (`application/json`):**
  ```json
  {
    "status": "CONTACTED"
  }
  ```
- **Valid Statuses:** `NEW`, `CONTACTED`, `RESOLVED`, `CLOSED`.
- **Responses:**
  - `200 OK`: Returns updated `InquiryResponse`.
  - `403 Forbidden`: Authenticated user is not the owner of the associated property.

### 4.4 Dismiss / Delete Inquiry
- **Method:** `DELETE`
- **Path:** `/api/inquiries/{id}`
- **Auth Required:** `Bearer <JWT>` (Property Owner or Admin)
- **Responses:**
  - `204 No Content`: Inquiry deleted.
