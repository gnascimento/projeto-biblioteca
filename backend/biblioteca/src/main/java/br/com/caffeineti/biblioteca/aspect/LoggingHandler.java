package br.com.caffeineti.biblioteca.aspect;

import br.com.caffeineti.biblioteca.repository.AssuntoRepository;
import br.com.caffeineti.biblioteca.repository.AutorRepository;
import br.com.caffeineti.biblioteca.repository.LivroRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingHandler {

    @Pointcut("execution(* br.com.caffeineti.biblioteca.repository.*.*(..))")
    public void repository() {
    }


    @Before("repository()")
    public void logBefore(JoinPoint joinPoint) {
        if (joinPoint.getSignature() instanceof MethodSignature methodSignature) {
            log.info("Entering in Method : {} at {}", methodSignature.getName(), defineRepositoryName(joinPoint));
        }
    }

    private String defineRepositoryName(JoinPoint joinPoint) {
        if(joinPoint.getTarget() instanceof AssuntoRepository) {
            return AssuntoRepository.class.getSimpleName();
        } else if (joinPoint.getTarget() instanceof LivroRepository) {
            return LivroRepository.class.getSimpleName();
        } else if (joinPoint.getTarget() instanceof AutorRepository) {
            return AutorRepository.class.getSimpleName();
        } else {
            return "Unknown";
        }
    }

}
