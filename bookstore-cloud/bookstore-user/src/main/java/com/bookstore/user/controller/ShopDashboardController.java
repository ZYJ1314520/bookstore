package com.bookstore.user.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.bookstore.common.Result;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.Shop;
import com.bookstore.user.feign.BookFeignClient;
import com.bookstore.user.feign.OrderFeignClient;
import com.bookstore.user.service.ShopService;
import com.bookstore.utils.UserContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shop/dashboard")
public class ShopDashboardController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private BookFeignClient bookFeignClient;

    @GetMapping
    public Result<?> getDashboard() {
        Shop shop = shopService.getShopInfo(UserContext.getUserId());
        if (shop == null) {
            return Result.error("店铺不存在");
        }

        Map<String, Object> stats = new HashMap<>();
        List<Order> shopOrders = orderFeignClient.getOrdersByShopId(shop.getId());

        if (shopOrders.isEmpty()) {
            stats.put("todaySales", 0);
            stats.put("todayOrders", 0);
            stats.put("pendingOrders", 0);
            stats.put("totalBooks", bookFeignClient.countByShopId(shop.getId()));
            return Result.success(stats);
        }

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        List<Long> shopOrderIds = shopOrders.stream().map(Order::getId).collect(Collectors.toList());

        List<Order> todayOrders = shopOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(todayStart))
                .collect(Collectors.toList());

        double todaySales = todayOrders.stream()
                .filter(o -> o.getStatus() >= 1 && o.getStatus() <= 3)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();

        long pendingOrders = shopOrders.stream()
                .filter(o -> o.getStatus() == 1)
                .count();

        long totalBooks = bookFeignClient.countByShopId(shop.getId());

        stats.put("todaySales", todaySales);
        stats.put("todayOrders", todayOrders.size());
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalBooks", totalBooks);

        return Result.success(stats);
    }

    @GetMapping("/chart")
    public Result<?> getSalesTrend(@RequestParam(defaultValue = "7") Integer days) {
        Shop shop = shopService.getShopInfo(UserContext.getUserId());
        if (shop == null) {
            return Result.error("店铺不存在");
        }

        List<Order> shopOrders = orderFeignClient.getOrdersByShopId(shop.getId());
        List<Map<String, Object>> trend = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            List<Order> dayOrders = shopOrders.stream()
                    .filter(o -> o.getPayTime() != null
                            && o.getPayTime().isAfter(start)
                            && o.getPayTime().isBefore(end)
                            && o.getStatus() >= 1 && o.getStatus() <= 3)
                    .collect(Collectors.toList());

            double sales = dayOrders.stream()
                    .mapToDouble(o -> o.getTotalAmount().doubleValue())
                    .sum();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("sales", sales);
            item.put("orders", dayOrders.size());
            trend.add(item);
        }

        return Result.success(trend);
    }

    @GetMapping("/export")
    public void exportReport(@RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             HttpServletResponse response) throws Exception {
        Shop shop = shopService.getShopInfo(UserContext.getUserId());
        if (shop == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"message\":\"店铺不存在\"}");
            return;
        }

        // 默认最近30天
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : end.minusDays(29);
        LocalDateTime startDt = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endDt = LocalDateTime.of(end, LocalTime.MAX);

        // 获取订单并按日期筛选
        List<Order> allOrders = orderFeignClient.getOrdersByShopId(shop.getId());
        List<Order> orders = allOrders.stream()
                .filter(o -> o.getPayTime() != null
                        && o.getPayTime().isAfter(startDt)
                        && o.getPayTime().isBefore(endDt)
                        && o.getStatus() >= 1 && o.getStatus() <= 3)
                .sorted(Comparator.comparing(Order::getPayTime).reversed())
                .collect(Collectors.toList());

        double totalRevenue = orders.stream().mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        int totalOrders = orders.size();
        double avgPrice = totalOrders > 0 ? totalRevenue / totalOrders : 0;
        long shippedCount = orders.stream().filter(o -> o.getStatus() == 2).count();
        long completedCount = orders.stream().filter(o -> o.getStatus() == 3).count();
        long paidCount = orders.stream().filter(o -> o.getStatus() == 1).count();

        // 设置响应头
        String fileName = URLEncoder.encode(shop.getShopName() + "_经营报表_" + start + "_" + end, StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // Sheet1: 经营汇总
        List<List<Object>> summaryData = new ArrayList<>();
        summaryData.add(List.of("报表周期", start + " 至 " + end));
        summaryData.add(List.of("总营业额（元）", String.format("%.2f", totalRevenue)));
        summaryData.add(List.of("总订单数", totalOrders));
        summaryData.add(List.of("平均客单价（元）", String.format("%.2f", avgPrice)));
        summaryData.add(List.of("待发货订单", paidCount));
        summaryData.add(List.of("已发货订单", shippedCount));
        summaryData.add(List.of("已完成订单", completedCount));

        // Sheet2: 订单明细
        List<List<Object>> detailData = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String[] statusText = {"待付款", "待发货", "已发货", "已完成", "已取消"};
        for (Order o : orders) {
            detailData.add(List.of(
                    o.getOrderNo(),
                    String.format("%.2f", o.getTotalAmount()),
                    o.getStatus() >= 0 && o.getStatus() < statusText.length ? statusText[o.getStatus()] : "未知",
                    o.getReceiverName() != null ? o.getReceiverName() : "",
                    o.getPayTime() != null ? o.getPayTime().format(dtf) : ""
            ));
        }

        try (ExcelWriter writer = EasyExcel.write(response.getOutputStream()).build()) {
            // Sheet1: 经营汇总
            WriteSheet sheet1 = EasyExcel.writerSheet(0, "经营汇总")
                    .head(List.of(List.of("项目", "数值")))
                    .registerWriteHandler(new com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy(22))
                    .build();
            writer.write(summaryData, sheet1);

            // Sheet2: 订单明细
            WriteSheet sheet2 = EasyExcel.writerSheet(1, "订单明细")
                    .head(List.of(List.of("订单号", "金额（元）", "状态", "收货人", "支付时间")))
                    .registerWriteHandler(new com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy(22))
                    .build();
            writer.write(detailData, sheet2);
        }
    }
}
