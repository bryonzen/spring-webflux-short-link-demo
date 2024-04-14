package tech.flycat.shortlink.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import tech.flycat.shortlink.entity.VisitRecord;
import tech.flycat.shortlink.entity.VisitRecordQueueData;
import tech.flycat.shortlink.repository.RedirectLinkRelationRepository;
import tech.flycat.shortlink.repository.VisitRecordRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Slf4j
@Service
public class VisitRecordHandler {

    @Autowired
    private RedirectLinkRelationRepository redirectLinkRelationRepository;
    @Autowired
    private VisitRecordRepository visitRecordRepository;

    public static final LinkedBlockingQueue<VisitRecordQueueData> visitRecordQueue = new LinkedBlockingQueue<>(Short.MAX_VALUE);

    public void push(String code, ServerRequest request) {
        Map<String, String> headers = request.headers().asHttpHeaders()
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x -> String.join("", x.getValue())));
        String remoteAddress = request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse(null);

        VisitRecordQueueData visitRecordQueueData = VisitRecordQueueData.builder()
                .code(code)
                .remoteAddress(remoteAddress)
                .headers(headers)
                .createTime(LocalDateTime.now())
                .build();

        if (log.isDebugEnabled()) {
            log.debug("保存访问记录: {}", visitRecordQueueData);
        }
        visitRecordQueue.offer(visitRecordQueueData);
    }

    @Scheduled(cron = "*/6 * * * * ?")
    public void scheduleSave() {
        if (visitRecordQueue.isEmpty()) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("保存访问数据，数量: {}", visitRecordQueue.size());
        }

        while (!visitRecordQueue.isEmpty()) {
            List<VisitRecordQueueData> visitRecordQueueDatas = new ArrayList<>();
            visitRecordQueue.drainTo(visitRecordQueueDatas, 1000);
            saveVisitRecords(visitRecordQueueDatas);
        }
    }

    private void saveVisitRecords(List<VisitRecordQueueData> visitRecordQueueDatas) {
        Map<String, AtomicInteger> code2Count = new HashMap<>();

        List<VisitRecord> visitRecords = visitRecordQueueDatas.stream().map(d -> {
            VisitRecord visitRecord = new VisitRecord();
            visitRecord.setCode(d.getCode());
            Map<String, String> headers = d.getHeaders();
            visitRecord.setHost(headers.get("Host"));
            visitRecord.setUserAgent(headers.get("User-Agent"));
            visitRecord.setIpAddr(getIpAddr(headers, d.getRemoteAddress()));
            visitRecord.setReferer(headers.get("Referer"));
            visitRecord.setCreatedTime(d.getCreateTime());

            code2Count.computeIfAbsent(d.getCode(), (code) -> new AtomicInteger(0)).incrementAndGet();
            return visitRecord;
        }).collect(Collectors.toList());

        visitRecordRepository.saveAll(visitRecords).subscribe();

        redirectLinkRelationRepository.findByCodeIn(code2Count.keySet())
                .map(r -> {
                    r.setVisitTimes(r.getVisitTimes() + code2Count.get(r.getCode()).intValue());
                    r.setUpdateTime(LocalDateTime.now());
                    return r;
                }).collectList()
                .flatMap(rs -> redirectLinkRelationRepository.saveAll(rs).collectList())
                .subscribe();
    }

    private String getIpAddr(Map<String, String> headers, String remoteAddress) {
        String ipAddress;
        try {
            ipAddress = headers.get("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = headers.get("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = headers.get("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = remoteAddress;
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }

        return ipAddress;
    }

}
