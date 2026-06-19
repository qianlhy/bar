package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.entity.User;
import com.flowerstore.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    /**
     * 获取大师分排行榜
     */
    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(defaultValue = "month") String type) {
        try {
            return Result.success(rankService.getRankList(type));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
