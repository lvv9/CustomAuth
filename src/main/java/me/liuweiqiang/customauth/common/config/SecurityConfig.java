package me.liuweiqiang.customauth.common.config;

import me.liuweiqiang.customauth.technique.TicketService;
import me.liuweiqiang.customauth.common.config.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.LinkedList;
import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ticket.path}")
    private String path;
    @Value("${ticket.page}")
    private String page;
    @Value("${token.names.credentialsname}")
    private String credentialsName;
    @Value("${token.names.principalname}")
    private String principalName;
    @Value("${server.session.maximumsessions}")
    private int maximumSessions;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private SessionRegistry sessionRegistry;
    private TicketService ticketService;

    @Autowired
    public void initialize(SessionAuthenticationStrategy sessionAuthenticationStrategy,
                           SessionRegistry sessionRegistry,
                           TicketService ticketService) {
        TicketAuthToken.setCredentialsName(credentialsName);
        TicketAuthToken.setPrincipalName(principalName);
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        this.sessionRegistry = sessionRegistry;
        this.ticketService = ticketService;
    }

    @Bean
    public HttpSessionEventPublisher publisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
        List<SessionAuthenticationStrategy> delegateStrategies = new LinkedList<>();

        ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrentSessionControlAuthenticationStrategy.setMaximumSessions(maximumSessions);
        delegateStrategies.add(concurrentSessionControlAuthenticationStrategy);

        delegateStrategies.add(new SessionFixationProtectionStrategy());

        delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry));
        CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy =
                new CompositeSessionAuthenticationStrategy(delegateStrategies);
        return compositeSessionAuthenticationStrategy;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        TicketAuthProvider provider = new TicketAuthProvider();
        provider.setTicketService(ticketService);
        auth.authenticationProvider(provider);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        TicketAuthFilter ticketAuthFilter = new TicketAuthFilter(path);
        ticketAuthFilter.setAuthenticationManager(authenticationManager());
        ticketAuthFilter.setAuthenticationSuccessHandler(new TicketSucessHandler());
        ticketAuthFilter.setAuthenticationFailureHandler(new TicketFailureHandler());
        ticketAuthFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);

        ConcurrentSessionFilter concurrentSessionFilter = new ConcurrentSessionFilter(sessionRegistry,
                new SessionExpiredStrategy());

        logger.debug("maximumSessions: {}", maximumSessions);
        httpSecurity
                .addFilterAt(ticketAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(concurrentSessionFilter, ConcurrentSessionFilter.class)
                    .authorizeRequests()
                    .antMatchers(page).permitAll()
                    .anyRequest().authenticated()
                .and()
                    .sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy)
                .and()
                    .csrf().disable();
    }
}
