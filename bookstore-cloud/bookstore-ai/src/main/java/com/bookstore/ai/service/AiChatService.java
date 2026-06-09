package com.bookstore.ai.service;

import com.bookstore.ai.feign.BookFeignClient;
import com.bookstore.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiChatService {

    private final ChatModel chatModel;
    private final BookFeignClient bookFeignClient;

    /** 每个用户的对话历史: userId -> messages */
    private final ConcurrentHashMap<Long, List<Message>> chatHistory = new ConcurrentHashMap<>();

    /** 每个用户保留的最大消息数（user + assistant 各算一条） */
    private static final int MAX_HISTORY = 10;

    private static final String SYSTEM_PROMPT = """
            你是「网上书店」的AI智能导购助手，名叫"书小助"。你的职责是：
            1. 根据用户的兴趣和需求，推荐合适的图书
            2. 介绍图书的分类、作者、内容等信息
            3. 回答关于书店购物流程的问题（如下单、支付、退换货等）
            4. 用友好、专业、热情的语气与用户交流
            5. 回答要简洁明了，不要太长
            6. 如果用户问的不是图书相关的问题，礼貌地引导回图书话题
            7. 你可以结合上下文继续对话，记住用户之前说过的内容

            当前书店在售图书信息：
            %s
            """;

    public AiChatService(ChatModel chatModel, BookFeignClient bookFeignClient) {
        this.chatModel = chatModel;
        this.bookFeignClient = bookFeignClient;
    }

    public String chat(Long userId, String userMessage) {
        try {
            // 获取全部书籍作为上下文
            String bookContext = getBookContext();
            String systemPrompt = String.format(SYSTEM_PROMPT, bookContext);

            // 获取或创建该用户的对话历史
            List<Message> history = chatHistory.computeIfAbsent(userId, k -> new ArrayList<>());

            // 构建完整消息列表：system + 历史 + 当前用户消息
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemPrompt));
            messages.addAll(history);
            messages.add(new UserMessage(userMessage));

            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);
            String reply = response.getResult().getOutput().getText();

            // 保存到对话历史
            history.add(new UserMessage(userMessage));
            history.add(new AssistantMessage(reply));

            // 裁剪历史，防止 token 过多
            while (history.size() > MAX_HISTORY) {
                history.remove(0);
                history.remove(0); // 成对移除
            }

            log.info("AI对话 userId={} - 用户: {} | 回复: {}", userId, userMessage, reply);
            return reply;
        } catch (Exception e) {
            log.error("AI对话异常 userId={}", userId, e);
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }

    public void clearHistory(Long userId) {
        chatHistory.remove(userId);
    }

    private String getBookContext() {
        try {
            List<Book> books = bookFeignClient.getAllBooks();
            if (books == null || books.isEmpty()) {
                return "暂无图书数据";
            }
            return books.stream()
                    .map(b -> String.format("《%s》 作者:%s 价格:¥%s 简介:%s",
                            b.getTitle(),
                            b.getAuthor(),
                            b.getPrice(),
                            b.getDescription() != null && b.getDescription().length() > 80
                                    ? b.getDescription().substring(0, 80) + "..."
                                    : b.getDescription()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.warn("获取图书上下文失败", e);
            return "暂无图书数据";
        }
    }
}
