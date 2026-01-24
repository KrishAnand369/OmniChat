package com.krish.chatApp.config;

import com.krish.chatApp.model.postgres.Tenant;
import com.krish.chatApp.repository.postgres.TenantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;

    public DataSeeder(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the "demo" tenant exists. If not, create it.
        if (tenantRepository.findById("demo").isEmpty()) {
            Tenant demoTenant = new Tenant();
            demoTenant.setId("demo");
            demoTenant.setDisplayName("Demo Corp");
            demoTenant.setApiSecret("12345");
            demoTenant.setActive(true);

            tenantRepository.save(demoTenant);
            System.out.println("✅ SUCCESS: Demo Tenant created!");
        }
    }
}