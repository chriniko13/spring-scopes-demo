package com.web.request.scope.example.demo.service;

import com.web.request.scope.example.demo.annotation.Hello;
import com.web.request.scope.example.demo.annotation.World;
import com.web.request.scope.example.demo.dto.BindingResponseDto;
import com.web.request.scope.example.demo.dto.DbResponseDto;
import com.web.request.scope.example.demo.dto.DemoRequestDto;
import com.web.request.scope.example.demo.dto.DemoResponseDto;
import com.web.request.scope.example.demo.repository.DemoRepository;
import com.web.request.scope.example.demo.transaction.TransactioIdThreadLocal;
import com.web.request.scope.example.demo.transaction.TransactionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class DemoService {

    @Autowired
    private TransactioIdThreadLocal transactioIdThreadLocal;

    @Autowired
    private TransactionId transactionId;

    @Autowired
    private DemoRepository demoRepository;

    @Hello
    @World
    public DemoResponseDto testServiceAnnotation(DemoRequestDto demoRequestDto) {
        String txId = transactionId.get();
        demoRepository.demoServiceAnnotation(txId);
        return new DemoResponseDto("all-ok");
    }

    public DemoResponseDto testServiceThreadLocal(DemoRequestDto demoRequestDto) {
        String txId = transactioIdThreadLocal.get();
        demoRepository.demoServiceThreadLocal(txId);
        return new DemoResponseDto("all-ok");
    }

    public DbResponseDto getDbStatus() {
        return new DbResponseDto(demoRepository.getDb());
    }

    public boolean ensureHealthyDb() {

        Map<String, List<String>> db = demoRepository.getDb();
        if (db.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, List<String>> entry : db.entrySet()) {

            List<String> value = entry.getValue();

            int before = value.size();
            int after = new HashSet<>(value).size();

            boolean allOk = after == (before / 2);
            if (!allOk) return false;

        }

        return true;
    }

    public void clearDb() {
        demoRepository.clearDb();
    }

    public List<BindingResponseDto> getThreadTransactionIdBindings() {
        return demoRepository.getThreadTransactionIdBindings();
    }
}
