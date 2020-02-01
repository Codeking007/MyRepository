package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("search")
public class SkuController {
    @Autowired
    private SkuFeign skuFeign;

    @GetMapping("list")
    public String search(@RequestParam(required = false) Map<String,String> searchMap, Model model) {
        //替换特殊字符
        handlerSearchMap(searchMap);
        //排序优化
        Set<String> sort = searchMap.keySet().stream().filter(key -> key.contains("SORT")).collect(Collectors.toSet());
        for (String key : sort) {
            searchMap.remove(key);
        }

        Map result = skuFeign.search(searchMap);
        model.addAttribute("result", result);
        model.addAttribute("searchMap", searchMap);
        String url = getUrl(searchMap);
        model.addAttribute("url", url);
        Page page = new Page(
                new Long(result.get("total").toString()),
                new Integer(result.get("pageNum").toString()),
                new Integer(result.get("pageSize").toString())
        );
        model.addAttribute("page",page);
        return "search";
    }

    private String getUrl(Map<String, String> searchMap) {
        String url = "/search/list";
        if (searchMap != null) {
            url += "?";
            for (String key : searchMap.keySet()) {
                if (/*key.indexOf("sort") > -1||*/"pageNum".equals(key)) {
                    continue;
                }
                url += key + "=" + searchMap.get(key) + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
    private void handlerSearchMap(Map<String,String> searchMap){
        if (searchMap!=null) {
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                if (entry.getKey().startsWith("spec_")) {
                    entry.setValue(entry.getValue().replace("+","%2B"));
                }
            }
        }
    }
}
