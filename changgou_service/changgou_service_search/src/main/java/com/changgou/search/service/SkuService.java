package com.changgou.search.service;

import java.util.Map;

public interface SkuService {
    void importSku();

    Map search(Map<String, String> searchMap);
}
