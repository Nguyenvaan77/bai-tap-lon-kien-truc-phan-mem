"""
Locust Load Testing Configuration for Online Banking System

This file defines HTTP load tests for the Spring Boot microservices system.
All traffic should go through the API Gateway (default: http://host.docker.internal:8080).

How to Run Locust:
------------------
1. Using Docker (recommended):
   docker build -f Dockerfile.locust -t bank-locust .
   docker run -p 8089:8089 bank-locust

2. Using Docker Compose:
   docker compose -f docker-compose.locust.yml up

3. Using Locust directly (requires Python 3.11+):
   pip install locust
   locust -f locustfile.py --host http://localhost:8080

4. Access Locust UI:
   Open browser to http://localhost:8089

Locust UI provides latency statistics:
- Median (50th percentile)
- 95th percentile
- 99th percentile
- Failure rate
- Requests per second

Best Practices:
---------------
- Always target the API Gateway, not internal services directly
- Use realistic wait_time between requests (1-3 seconds)
- Avoid hardcoding single IDs - use dynamic data when possible
- Override host when deploying to cloud environments
- Monitor failure rates and adjust load accordingly
"""

from locust import HttpUser, task, between
import json
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class ApiUser(HttpUser):
    """
    Locust user class that simulates API requests to the banking system.
    
    This class represents a virtual user that will:
    - Optionally authenticate and store JWT token
    - Make requests to various endpoints
    - Wait 1-3 seconds between tasks (realistic user behavior)
    """
    
    # Wait time between tasks: 1-3 seconds (random)
    wait_time = between(1, 3)
    
    def on_start(self):
        """
        Called when a simulated user starts.
        Optionally authenticate and store token for subsequent requests.
        """
        # Set to True to enable authentication
        self.use_auth = False
        
        # Credentials for authentication (update with valid test credentials)
        self.auth_email = "test@example.com"
        self.auth_password = "password123"
        
        # JWT token storage
        self.token = None
        
        # Authenticate if enabled
        if self.use_auth:
            self.authenticate()
    
    def authenticate(self):
        """
        Authenticate user and store JWT token.
        Token will be sent as Bearer header in subsequent requests.
        """
        try:
            login_data = {
                "email": self.auth_email,
                "password": self.auth_password
            }
            
            with self.client.post(
                "/api/v1/login",
                json=login_data,
                name="Auth - Login",
                catch_response=True
            ) as response:
                if response.status_code == 200:
                    try:
                        response_data = response.json()
                        self.token = response_data.get("token") or response_data.get("accessToken")
                        if self.token:
                            logger.info(f"Authentication successful, token stored")
                            response.success()
                        else:
                            logger.warning("Login successful but no token in response")
                            response.failure("No token in response")
                    except json.JSONDecodeError:
                        response.failure("Invalid JSON response")
                elif response.status_code == 401:
                    response.failure("Invalid credentials")
                else:
                    response.failure(f"Login failed with status {response.status_code}")
        except Exception as e:
            logger.error(f"Authentication error: {str(e)}")
    
    def get_headers(self):
        """
        Get headers for authenticated requests.
        Returns headers with Bearer token if authentication is enabled.
        """
        headers = {"Content-Type": "application/json"}
        if self.use_auth and self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        return headers
    
    @task(3)
    def get_accounts(self):
        """
        GET /api/v1/account/** - Get account information.
        Weight: 3 (executed 3x more frequently than other tasks)
        """
        # Example: Get account details
        # Note: In production, use dynamic account numbers from previous responses
        account_no = 1000001  # Replace with dynamic value in production
        
        with self.client.get(
            f"/api/v1/account/{account_no}",
            headers=self.get_headers(),
            name="Account - Get Account Details",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 401:
                response.failure("Unauthorized - token may be invalid")
            elif response.status_code == 404:
                response.failure("Account not found")
            else:
                response.failure(f"Unexpected status: {response.status_code}")
    
    @task(2)
    def get_user_accounts(self):
        """
        GET /api/v1/account/user/{userId} - Get all accounts for a user.
        Weight: 2
        """
        # Example: Get user accounts
        # Note: In production, use dynamic user ID from authentication
        user_id = "test-user-id"  # Replace with dynamic value in production
        
        with self.client.get(
            f"/api/v1/account/user/{user_id}",
            headers=self.get_headers(),
            name="Account - Get User Accounts",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 401:
                response.failure("Unauthorized")
            elif response.status_code == 404:
                response.failure("User not found")
            else:
                response.failure(f"Unexpected status: {response.status_code}")
    
    @task(2)
    def get_customer_profile(self):
        """
        GET /api/v1/user/{id} - Get customer profile.
        Weight: 2
        """
        # Example: Get customer profile
        # Note: In production, use dynamic user ID from authentication
        user_id = "test-user-id"  # Replace with dynamic value in production
        
        with self.client.get(
            f"/api/v1/user/{user_id}",
            headers=self.get_headers(),
            name="Customer - Get Profile",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 401:
                response.failure("Unauthorized")
            elif response.status_code == 404:
                response.failure("User not found")
            else:
                response.failure(f"Unexpected status: {response.status_code}")
    
    @task(1)
    def get_beneficiaries(self):
        """
        GET /api/v1/beneficiary/{id} - Get beneficiary information.
        Weight: 1 (executed less frequently)
        """
        # Example: Get beneficiary
        # Note: In production, use dynamic beneficiary ID
        beneficiary_id = "beneficiary-123"  # Replace with dynamic value in production
        
        with self.client.get(
            f"/api/v1/beneficiary/{beneficiary_id}",
            headers=self.get_headers(),
            name="Customer - Get Beneficiary",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 401:
                response.failure("Unauthorized")
            elif response.status_code == 404:
                response.failure("Beneficiary not found")
            else:
                response.failure(f"Unexpected status: {response.status_code}")
    
    @task(1)
    def health_check(self):
        """
        Optional: Health check endpoint (if available).
        Weight: 1
        """
        with self.client.get(
            "/actuator/health",
            name="Health Check",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:  # 404 is OK if endpoint doesn't exist
                response.success()
            else:
                response.failure(f"Health check failed: {response.status_code}")


# Additional configuration
# ------------------------
# You can add more user classes for different user types:
# 
# class AdminUser(HttpUser):
#     wait_time = between(2, 5)
#     @task
#     def admin_task(self):
#         ...
#
# This allows testing different user behaviors simultaneously.

