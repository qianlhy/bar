package com.flowerstore.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.flowerstore.entity.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 商品规格校验与计价。价格只从后端商品配置中读取，避免信任前端传价。
 */
@Service
public class ProductSpecService {

    public BigDecimal resolvePrice(Product product, String specText) {
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        String normalized = normalize(specText);
        if (normalized.isEmpty()) {
            return product.getPrice();
        }
        if (product.getSpecs() == null || product.getSpecs().trim().isEmpty()) {
            throw new RuntimeException("该商品不支持规格选择");
        }

        try {
            JSONArray groups = JSONArray.parseArray(product.getSpecs());
            String[] labels = normalized.split("\\s*/\\s*");
            if (groups == null || groups.size() != labels.length) {
                throw new RuntimeException("商品规格选择不完整");
            }

            BigDecimal price = product.getPrice();
            for (int i = 0; i < groups.size(); i++) {
                JSONObject group = groups.getJSONObject(i);
                JSONArray options = group == null ? null : group.getJSONArray("options");
                JSONObject selected = null;
                if (options != null) {
                    for (int j = 0; j < options.size(); j++) {
                        JSONObject option = options.getJSONObject(j);
                        if (option != null && labels[i].equals(option.getString("label"))) {
                            selected = option;
                            break;
                        }
                    }
                }
                if (selected == null) {
                    throw new RuntimeException("商品规格已变更，请重新选择");
                }
                if (selected.get("price") != null) {
                    price = selected.getBigDecimal("price");
                }
            }
            return price;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("商品规格配置错误");
        }
    }

    public String normalize(String specText) {
        return specText == null ? "" : specText.trim();
    }
}
