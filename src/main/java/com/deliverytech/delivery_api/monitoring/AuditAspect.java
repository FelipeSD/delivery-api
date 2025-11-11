package com.deliverytech.delivery_api.monitoring;

import java.util.Map;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.deliverytech.delivery_api.monitoring.audit.AuditService;
import com.deliverytech.delivery_api.security.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    // Intercepta qualquer método em um serviço que comece com "cadastrar"
    @AfterReturning(pointcut = "execution(* com.deliverytech.delivery_api.services.*.cadastrar*(..))", returning = "result")
    public void auditarCriacao(Object result) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String entityName = result != null ? result.getClass().getSimpleName() : "Desconhecida";

            log.info("🔎 Auditoria automática - usuário: {}, entidade: {}", userId, entityName);

            auditService.logUserAction(
                userId,
                "CRIAR",
                entityName,
                Map.of("resultado", result != null ? result.toString() : "null")
            );

        } catch (Exception e) {
            log.warn("⚠️ Falha ao auditar ação: {}", e.getMessage());
        }
    }
}
