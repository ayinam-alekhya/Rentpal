import requests
import json

# Base URL for the API
BASE_URL = "http://localhost:8088/api"

def register_user(name, email, password, role):
    """Register a new user"""
    url = f"{BASE_URL}/auth/register"
    payload = {
        "name": name,
        "email": email,
        "password": password,
        "role": role
    }
    headers = {
        "Content-Type": "application/json"
    }
    
    print(f"Sending POST request to {url}")
    print(f"Payload: {json.dumps(payload, indent=2)}")
    
    try:
        response = requests.post(url, json=payload, headers=headers)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        return response
    except Exception as e:
        print(f"Error: {e}")
        return None

def login_user(email, password, role):
    """Login a user"""
    url = f"{BASE_URL}/auth/login"
    payload = {
        "email": email,
        "password": password,
        "role": role
    }
    headers = {
        "Content-Type": "application/json"
    }
    
    print(f"Sending POST request to {url}")
    print(f"Payload: {json.dumps(payload, indent=2)}")
    
    try:
        response = requests.post(url, json=payload, headers=headers)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        return response
    except Exception as e:
        print(f"Error: {e}")
        return None

if __name__ == "__main__":
    print("Testing RentPal API with SQLite backend")
    print("=" * 50)
    
    # Register a new owner
    print("\n1. Registering a new owner...")
    register_response = register_user("John Doe", "john@example.com", "password123", "owner")
    
    if register_response and register_response.status_code == 200:
        print("Owner registration successful!")
        
        # Try to login the owner
        print("\n2. Logging in the owner...")
        login_response = login_user("john@example.com", "password123", "owner")
        
        if login_response and login_response.status_code == 200:
            print("Owner login successful!")
            data = login_response.json()
            if data.get("success"):
                print(f"Welcome, {data['user']['name']}!")
            else:
                print("Login failed:", data.get("message"))
        else:
            print("Login failed!")
    else:
        print("Owner registration failed!")