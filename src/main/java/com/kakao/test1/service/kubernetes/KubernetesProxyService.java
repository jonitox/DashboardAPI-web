package com.kakao.test1.service.kubernetes;

import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.kraken.vault.VaultAuthenticatorFactory;
import com.kakao.test1.config.Dkosv3ContextSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KubernetesProxyService {
    private static final Map<String, Dkosv3ContextSecret> k8sContextMap;
    static {
        k8sContextMap = new HashMap<>();
    }

    public KubeAPI getContext(String contextName) throws InvalidK8sContextException {
        final String secretPathFormat = "secret/media/kraken-dkosv3/%s";

        if (!k8sContextMap.containsKey(contextName)) {
            // fsis 이용하여 vault에서 context 조회
            Dkosv3ContextSecret dkosv3ContextSecret = VaultAuthenticatorFactory.appRole("media")
                    .read(String.format(secretPathFormat, contextName), Dkosv3ContextSecret.class)
                    .orElse(null);
            if (dkosv3ContextSecret == null) {
                // 데이터가 없을 경우 에러 발생
                throw new InvalidK8sContextException(contextName);
            } else {
                k8sContextMap.put(contextName, dkosv3ContextSecret);
            }
        }

        return new KubeAPI(k8sContextMap.get(contextName));
    }

}
