package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.VirtualAccountAdapterDbo;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VirtualAccountRepository implements PanacheMongoRepositoryBase<VirtualAccountAdapterDbo, String> {
}
