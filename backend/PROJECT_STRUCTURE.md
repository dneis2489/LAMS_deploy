# LAMS Project Structure

## Overview
This project consists of a backend API server and a React-based frontend application for monitoring and analyzing API logs.

## Backend

The backend provides REST API endpoints for:
- Retrieving log lists with pagination and filtering
- Getting detailed log information
- Statistics on request counts, status codes, and response durations

### Backend Endpoints

#### Logs API
- `GET /lams/loglist?p={page}&s={size}` - Get paginated list of logs
- `GET /lams/loglist/filter?p={page}&s={size}&method={method}&status={status}` - Get filtered logs
- `GET /lams/loginfo?id={id}` - Get detailed log information by ID

#### Statistics API - Request Counts
- `GET /lams/getCountRequestForMethodsWithHour` - Request counts by method (hourly)
- `GET /lams/getCountRequestForMethodsWithDay` - Request counts by method (daily)
- `GET /lams/getCountRequestForMethodsWithMonth` - Request counts by method (monthly)
- `GET /lams/getCountRequestWithHour` - Total request counts (hourly)
- `GET /lams/getCountRequestWithDay` - Total request counts (daily)
- `GET /lams/getCountRequestWithMounth` - Total request counts (monthly)

#### Statistics API - Status Codes
- `GET /lams/getCountRequestStatusForMethodsWithHour` - Status counts by method (hourly)
- `GET /lams/getCountRequestStatusForMethodsWithDay` - Status counts by method (daily)
- `GET /lams/getCountRequestStatusForMethodsMonth` - Status counts by method (monthly)
- `GET /lams/getCountRequestStatusWithHour` - Total status counts (hourly)
- `GET /lams/getCountRequestStatusWithDay` - Total status counts (daily)
- `GET /lams/getCountRequestStatusMonth` - Total status counts (monthly)

#### Statistics API - Duration
- `GET /lams/getDurationForMethodsWithHour` - Average duration by method (hourly)
- `GET /lams/getDurationForMethodsWithDay` - Average duration by method (daily)
- `GET /lams/getDurationForMethodsWithMonth` - Average duration by method (monthly)
- `GET /lams/getDurationWithHour` - Average duration (hourly)
- `GET /lams/getDurationWithDay` - Average duration (daily)
- `GET /lams/getDurationWithMonth` - Average duration (monthly)

## Frontend

### File Listing

```
frontend/
├── public/
│   └── index.html              # HTML template
├── src/
│   ├── components/
│   │   ├── Dashboard.js        # Main dashboard with stats overview
│   │   ├── LogsList.js         # Paginated logs table with filters
│   │   ├── LogDetail.js        # Detailed log view
│   │   └── Statistics.js       # Charts and analytics with Recharts
│   ├── api.js                  # API client configuration (axios)
│   ├── App.js                  # Main application component with routing
│   ├── index.js                # Application entry point
│   └── index.css               # Global styles with responsive design
├── package.json                # Dependencies and scripts
├── webpack.config.js           # Webpack configuration
└── README.md                   # Frontend documentation
```

### Frontend Files Description

1. **public/index.html**
   - Base HTML template with Google Fonts (Inter)
   - Viewport meta tag for responsive design

2. **src/index.js**
   - React application entry point
   - Renders the App component

3. **src/App.js**
   - Main application component
   - React Router configuration with routes:
     - `/` - Dashboard
     - `/logs` - Logs List
     - `/logs/:id` - Log Detail
     - `/statistics` - Statistics

4. **src/api.js**
   - Axios instance configuration
   - API methods organized by category:
     - `logApi` - Log-related endpoints
     - `countRequestApi` - Request count statistics
     - `requestStatusApi` - Status code statistics
     - `durationApi` - Response duration statistics

5. **src/components/Dashboard.js**
   - Key metrics cards (Total Requests, Success Rate, Avg Duration)
   - Top methods table
   - Quick links to other sections

6. **src/components/LogsList.js**
   - Filterable logs table (by method and status)
   - Pagination controls
   - Links to detailed log views

7. **src/components/LogDetail.js**
   - Detailed log information display
   - Request/Response headers and bodies
   - Timing information

8. **src/components/Statistics.js**
   - Interactive charts using Recharts:
     - Area chart for requests over time
     - Line chart for response duration
     - Pie chart for status distribution
     - Bar charts for methods and status codes
   - Time range selector (Hour/Day/Month)
   - Chart type selector

9. **src/index.css**
   - Modern CSS with custom properties (CSS variables)
   - Responsive design with media queries
   - Styled components:
     - Navbar with gradient background
     - Stat cards with hover effects
     - Tables with styled headers
     - Method and status badges
     - Form controls
     - Charts containers

### Technologies Used

- **React 18** - UI library
- **React Router DOM 6** - Client-side routing
- **Recharts 2** - Data visualization
- **Axios** - HTTP client
- **Webpack 5** - Module bundler
- **Babel** - JavaScript transpiler

### Features

- ✅ Modern, clean design with gradient accents
- ✅ Fully responsive layout (mobile, tablet, desktop)
- ✅ Interactive charts for data visualization
- ✅ Real-time data fetching from backend API
- ✅ Filtering and pagination for logs
- ✅ Detailed log inspection
- ✅ Smooth animations and transitions
- ✅ Color-coded method and status badges

## Running the Application

### Backend
```bash
# Start the backend server (typically on port 8080)
```

### Frontend
```bash
cd frontend
npm install
npm start  # Starts dev server on http://localhost:3000
```

The frontend development server is configured to proxy `/lams` requests to the backend at `http://localhost:8080`.
