package edonymyeon.backend.global.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setTaskDecorator(new CopyTaskDecorator());
        return taskExecutor;
    }

    private static class CopyTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(final Runnable runnable) {
            final Map<String, String> mdc = MDC.getCopyOfContextMap();
            return () -> {
                try{
                    if(mdc != null){
                        MDC.setContextMap(mdc);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }
}
