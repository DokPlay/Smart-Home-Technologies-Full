package com.smarthome.commerce.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(Long id, String name, String description, String category, BigDecimal price, Availability availability, State state, List<String> photos) {
}
