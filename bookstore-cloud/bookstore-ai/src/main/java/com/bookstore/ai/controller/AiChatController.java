package com.bookstore.ai.controller;

import com.bookstore.ai.service.AiChatService;
import com.bookstore.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return Result.error(400, "消息不能为空");
        }
        String userIdStr = request.getHeader("X-User-Id");
        Long userId = userIdStr != null ? Long.parseLong(userIdStr) : 0L;
        String reply = aiChatService.chat(userId, message);
        return Result.success(Map.of("reply", reply));
    }

    @PostMapping("/clear")
    public Result<Void> clearHistory(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        Long userId = userIdStr != null ? Long.parseLong(userIdStr) : 0L;
        aiChatService.clearHistory(userId);
        return Result.success();
    }
}
