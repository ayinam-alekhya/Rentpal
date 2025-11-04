import com.rentpal.service.AuthService;

public class TestTenantAuth {
    public static void main(String[] args) {
        try {
            AuthService authService = new AuthService();
            
            // Test with a tenant that has an email
            AuthService.AuthResult result = authService.authenticate("test.tenant@example.com", "test123", "tenant");
            
            if (result.isSuccess()) {
                System.out.println("Authentication successful!");
                System.out.println("User type: " + result.getUserType());
                if (result.getTenant() != null) {
                    System.out.println("Tenant name: " + result.getTenant().getName());
                    System.out.println("Tenant email: " + result.getTenant().getEmail());
                }
            } else {
                System.out.println("Authentication failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}