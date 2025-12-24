"""
Locust Load Testing Configuration for Online Banking System

This file defines HTTP load tests for the Spring Boot microservices system.
All traffic goes through the API Gateway (default: http://host.docker.internal:8080).

User Scenarios:
--------------
1. Browse User (60%): login → view accounts → logout
2. Active User (30%): login → view accounts → transfer money → logout
3. History User (10%): login → view transaction history → logout

How to Run Locust:
------------------
1. Using Docker Compose (recommended):
   docker compose -f docker-compose.locust.yml up

2. Using Docker directly:
   docker build -f Dockerfile.locust -t bank-locust .
   docker run -p 8089:8089 bank-locust

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

Configuration:
--------------
Update TEST_CREDENTIALS below with valid test user credentials.
The system will automatically authenticate and reuse JWT tokens.
"""

from locust import HttpUser, task, between
import json
import random
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Test credentials - UPDATE THESE with valid test user credentials
TEST_CREDENTIALS = [
    {"email": "user1@example.com", "password": "password123"},
    {"email": "user2@example.com", "password": "password123"},
    {"email": "user3@example.com", "password": "password123"},
]

# Test account numbers - UPDATE THESE with valid account numbers from your test data
TEST_ACCOUNTS = [1000001, 1000002, 1000003]


class BaseUser(HttpUser):
    """
    Base user class with common authentication and helper methods.
    All user scenarios inherit from this class.
    """
    
    # Realistic think time between tasks: 1-3 seconds
    wait_time = between(1, 3)
    
    def on_start(self):
        """
        Called when a simulated user starts.
        Authenticates and stores JWT token for subsequent requests.
        """
        self.token = None
        self.user_id = None
        self.accounts = []
        
        # Select random credentials for this user
        creds = random.choice(TEST_CREDENTIALS)
        self.email = creds["email"]
        self.password = creds["password"]
        
        # Authenticate
        self.authenticate()
    
    def authenticate(self):
        """
        Authenticate user and store JWT token.
        Token will be sent as Bearer header in subsequent requests.
        """
        try:
            login_data = {
                "email": self.email,
                "password": self.password
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
                        self.token = response_data.get("jwtToken")
                        user_data = response_data.get("user", {})
                        self.user_id = user_data.get("userId")
                        
                        if self.token and self.user_id:
                            logger.info(f"Authentication successful for {self.email}")
                            response.success()
                        else:
                            logger.warning("Login successful but missing token or userId")
                            response.failure("Missing token or userId in response")
                    except (json.JSONDecodeError, AttributeError) as e:
                        logger.error(f"Failed to parse login response: {e}")
                        response.failure(f"Invalid JSON response: {str(e)}")
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
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        return headers
    
    def validate_response(self, response, success_field=None):
        """
        Validate response by checking status code and optional success field.
        
        Args:
            response: Locust response object
            success_field: Optional field name in JSON to check for success (e.g., "transactionStatus")
        
        Returns:
            bool: True if response is valid
        """
        if response.status_code == 200:
            try:
                data = response.json()
                if success_field:
                    # Check for success field (e.g., "SUCCESS" status)
                    field_value = data.get(success_field)
                    if field_value and "SUCCESS" in str(field_value).upper():
                        return True
                    elif field_value:
                        return False
                # If no success field specified, 200 is good enough
                return True
            except (json.JSONDecodeError, AttributeError):
                # Valid JSON but no success field to check
                return True
        return False
    
    def get_user_accounts(self):
        """
        Helper method to get user accounts and cache them.
        """
        if not self.user_id:
            return []
        
        if self.accounts:
            return self.accounts
        
        try:
            with self.client.get(
                f"/api/v1/account/user/{self.user_id}",
                headers=self.get_headers(),
                name="Account - Get User Accounts",
                catch_response=True
            ) as response:
                if response.status_code == 200:
                    try:
                        self.accounts = response.json()
                        response.success()
                        return self.accounts
                    except json.JSONDecodeError:
                        response.failure("Invalid JSON response")
                elif response.status_code == 401:
                    response.failure("Unauthorized - token may be invalid")
                elif response.status_code == 404:
                    response.failure("User not found")
                else:
                    response.failure(f"Unexpected status: {response.status_code}")
        except Exception as e:
            logger.error(f"Error getting user accounts: {e}")
        
        return []


class BrowseUser(BaseUser):
    """
    Browse User Scenario (60% of traffic)
    Most common user: login → view accounts → logout
    """
    
    weight = 6  # 60% of traffic (6 out of 10)
    
    @task
    def browse_accounts(self):
        """
        Browse user's accounts.
        """
        accounts = self.get_user_accounts()
        
        if accounts and len(accounts) > 0:
            # View a random account
            account = random.choice(accounts)
            account_no = account.get("accountno")
            
            if account_no:
                with self.client.get(
                    f"/api/v1/account/{account_no}",
                    headers=self.get_headers(),
                    name="Account - View Account Details",
                    catch_response=True
                ) as response:
                    if response.status_code == 200:
                        if self.validate_response(response):
                            response.success()
                        else:
                            response.failure("Invalid response data")
                    elif response.status_code == 401:
                        response.failure("Unauthorized")
                    elif response.status_code == 404:
                        response.failure("Account not found")
                    else:
                        response.failure(f"Unexpected status: {response.status_code}")
        else:
            # If no accounts, just view user accounts endpoint
            self.get_user_accounts()


class ActiveUser(BaseUser):
    """
    Active User Scenario (30% of traffic)
    Active user: login → view accounts → transfer money → logout
    """
    
    weight = 3  # 30% of traffic (3 out of 10)
    
    @task
    def view_and_transfer(self):
        """
        View accounts and then transfer money.
        """
        accounts = self.get_user_accounts()
        
        if accounts and len(accounts) >= 2:
            # Select two different accounts for transfer
            from_account = random.choice(accounts)
            to_account = random.choice([acc for acc in accounts if acc.get("accountno") != from_account.get("accountno")])
            
            from_account_no = from_account.get("accountno")
            to_account_no = to_account.get("accountno")
            
            if from_account_no and to_account_no:
                # Transfer a small random amount (1-100)
                transfer_amount = round(random.uniform(1.0, 100.0), 2)
                
                transfer_data = {
                    "fromAccount": from_account_no,
                    "toAccount": to_account_no,
                    "amount": transfer_amount,
                    "description": f"Test transfer from load test"
                }
                
                with self.client.post(
                    "/api/v1/account/transfer",
                    json=transfer_data,
                    headers=self.get_headers(),
                    name="Account - Transfer Money",
                    catch_response=True
                ) as response:
                    if response.status_code == 200:
                        if self.validate_response(response, "transactionStatus"):
                            response.success()
                        else:
                            response.failure("Transfer did not succeed")
                    elif response.status_code == 400:
                        # Insufficient balance is a valid business error
                        try:
                            error_data = response.json()
                            if "insufficient" in str(error_data).lower() or "balance" in str(error_data).lower():
                                response.success()  # Expected business error
                            else:
                                response.failure(f"Bad request: {error_data}")
                        except:
                            response.failure("Bad request")
                    elif response.status_code == 401:
                        response.failure("Unauthorized")
                    else:
                        response.failure(f"Unexpected status: {response.status_code}")
        else:
            # If not enough accounts, just view accounts
            self.get_user_accounts()


class HistoryUser(BaseUser):
    """
    History User Scenario (10% of traffic)
    History user: login → view transaction history → logout
    """
    
    weight = 1  # 10% of traffic (1 out of 10)
    
    @task
    def view_transaction_history(self):
        """
        View transaction history for an account.
        """
        accounts = self.get_user_accounts()
        
        if accounts and len(accounts) > 0:
            # Select a random account
            account = random.choice(accounts)
            account_no = account.get("accountno")
            
            if account_no:
                with self.client.get(
                    f"/api/v1/account/transactions/{account_no}",
                    headers=self.get_headers(),
                    name="Account - View Transaction History",
                    catch_response=True
                ) as response:
                    if response.status_code == 200:
                        try:
                            transactions = response.json()
                            # Validate it's a list (transaction history)
                            if isinstance(transactions, list):
                                response.success()
                            else:
                                response.failure("Invalid response format - expected list")
                        except json.JSONDecodeError:
                            response.failure("Invalid JSON response")
                    elif response.status_code == 401:
                        response.failure("Unauthorized")
                    elif response.status_code == 404:
                        response.failure("Account not found")
                    else:
                        response.failure(f"Unexpected status: {response.status_code}")
        else:
            # If no accounts, try with a test account number
            test_account = random.choice(TEST_ACCOUNTS)
            with self.client.get(
                f"/api/v1/account/transactions/{test_account}",
                headers=self.get_headers(),
                name="Account - View Transaction History",
                catch_response=True
            ) as response:
                if response.status_code in [200, 404]:  # 404 is acceptable if account doesn't exist
                    response.success()
                else:
                    response.failure(f"Unexpected status: {response.status_code}")
