package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.RealAccountAdapterDbo;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RealAccountRepository implements PanacheMongoRepositoryBase<RealAccountAdapterDbo, String> {

  
}
