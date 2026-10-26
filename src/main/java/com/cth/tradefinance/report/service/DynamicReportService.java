package com.cth.tradefinance.report.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DynamicReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public Map<String, Object> executeDynamicReportQuery(String entityName, List<String> fields, String filterField, String filterValue) {
        EntityType<?> targetEntity = null;
        for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
            if (entity.getName().equalsIgnoreCase(entityName)) {
                targetEntity = entity;
                break;
            }
        }

        if (targetEntity == null) {
            throw new IllegalArgumentException("Invalid or unauthorized entity name: " + entityName);
        }

        Set<String> validAttributes = new HashSet<>();
        targetEntity.getAttributes().forEach(attr -> validAttributes.add(attr.getName()));

        List<String> safeFields = new ArrayList<>();
        if (fields != null) {
            for (String f : fields) {
                if (validAttributes.contains(f)) {
                    safeFields.add(f);
                }
            }
        }
        if (safeFields.isEmpty()) {
            safeFields.add(validAttributes.iterator().next());
        }

        StringBuilder jpql = new StringBuilder("SELECT ");
        for (int i = 0; i < safeFields.size(); i++) {
            jpql.append("e.").append(safeFields.get(i));
            if (i < safeFields.size() - 1) {
                jpql.append(", ");
            }
        }
        jpql.append(" FROM ").append(targetEntity.getName()).append(" e");

        boolean hasFilter = filterField != null && validAttributes.contains(filterField) && filterValue != null && !filterValue.trim().isEmpty();
        if (hasFilter) {
            jpql.append(" WHERE LOWER(CAST(e.").append(filterField).append(" AS string)) LIKE LOWER(:filterVal)");
        }

        Query query = entityManager.createQuery(jpql.toString());
        if (hasFilter) {
            query.setParameter("filterVal", "%" + filterValue.trim() + "%");
        }

        List<?> resultList = query.getResultList();
        List<Map<String, Object>> rows = new ArrayList<>();

        for (Object item : resultList) {
            Map<String, Object> row = new LinkedHashMap<>();
            if (safeFields.size() == 1) {
                row.put(safeFields.get(0), item);
            } else {
                Object[] objArr = (Object[]) item;
                for (int i = 0; i < safeFields.size(); i++) {
                    row.put(safeFields.get(i), objArr[i]);
                }
            }
            rows.add(row);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("headers", safeFields);
        response.put("data", rows);
        response.put("totalRows", rows.size());

        return response;
    }
}
