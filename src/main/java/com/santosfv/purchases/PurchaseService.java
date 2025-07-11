package com.santosfv.purchases;

import com.santosfv.purchases.controllers.PurchaseRequest;
import com.santosfv.purchases.repository.PurchaseModel;
import com.santosfv.purchases.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public PurchaseModel createPurchase(PurchaseRequest purchaseRequest) {
        PurchaseModel model = PurchaseModel.from(purchaseRequest);
        return purchaseRepository.save(model);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseModel> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id);
    }
}