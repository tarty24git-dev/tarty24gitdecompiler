package com.cth.tradefinance.report.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportSchemaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, List<String>> getAvailableTablesAndFields() {
        Map<String, List<String>> schemaMap = new LinkedHashMap<>();
        Metamodel metamodel = entityManager.getMetamodel();

        for (EntityType<?> entityType : metamodel.getEntities()) {
            String entityName = entityType.getName();
            List<String> fields = new ArrayList<>();
            entityType.getAttributes().forEach(attr -> fields.add(attr.getName()));
            Collections.sort(fields);
            schemaMap.put(entityName, fields);
        }

        return schemaMap;
    }
}
